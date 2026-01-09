package com.marcoscherzer.msimplespeechbackend.server;

import java.io.InputStream;
import java.io.PrintStream;
import java.security.KeyStore;

import android.content.Context;

import com.marcoscherzer.msimplespeechbackend.R;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import java.util.UUID;


/**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
public final class MSimpleSpeechBackendServer {

    private final NanoHTTPD server;
    private final MISpeechRecognitionManager recognizer;

    private Runnable onPairHandler;
    public PrintStream out;

    /**
     * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    public static class MClientInformation{
        private String nextRecordEndpoint = "/initialize";
        private String ip;

        private String registeredClientId;

        @Override
        public String toString() {
            return "MClientInformation" +
                    "nextRecordEndpoint='" + (nextRecordEndpoint != null ? nextRecordEndpoint : "null") + '\'' +
                    ", ip='" + (ip != null ? ip : "null") + '\'' +
                    ", registeredClientId='" + (registeredClientId != null ? registeredClientId : "null") + '\'';
        }
    }

    private MClientInformation clientInformation;

    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    public MSimpleSpeechBackendServer(int port, MISpeechRecognitionManager recognitionManager, Context context, PrintStream out) throws Exception {
        this.recognizer= recognitionManager;
        this.out=out;
        out.println("Initializing server...");
        SSLContext sslContext= createSSLContext(context);
        server = new NanoHTTPD(port) {
            /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
            @Override
            public Response serve(IHTTPSession session) {
                out.println("\nNew request received: " + session.getUri());

                if (!"GET".equalsIgnoreCase(session.getMethod().name())) {
                    out.println("Invalid request method: " + session.getMethod());
                    return NanoHTTPD.newFixedLengthResponse(Status.METHOD_NOT_ALLOWED, "text/plain", "Only GET allowed");
                }

                if (isRateLimited()) {
                    out.println("Rate limited.");
                    return NanoHTTPD.newFixedLengthResponse(Status.TOO_MANY_REQUESTS, "text/plain", "Slow down");
                }
//-----------------------------------------------------
                String clientId = session.getHeaders().get("x-client-id");
                if (clientId == null || clientId.isEmpty()) {
                    out.println("Missing client ID.");
                    return NanoHTTPD.newFixedLengthResponse(Status.BAD_REQUEST, "text/plain", "Missing X-Client-ID header");
                }

                if (clientInformation.registeredClientId == null) {
                    clientInformation.registeredClientId = clientId;
                    out.println("Registered new client ID =  \"" + clientId + "\"");
                }

                if (!clientId.equals(clientInformation.registeredClientId)) {
                    return NanoHTTPD.newFixedLengthResponse(Status.NOT_FOUND, "text/plain", "Unknown client");
                }

                if (!isPaired() && onPairHandler != null) {
                    onPairHandler.run();
                }

                if (session.getUri().equals(clientInformation.nextRecordEndpoint)) {
                    String results = "";
                    if (isPaired()) {
                        out.println("Starting recognizer...");
                        recognizer.startListening();
                        results = recognizer.waitOnResults();
                        out.println("Recognition complete.");
                    }
                    clientInformation.nextRecordEndpoint = "/" + UUID.randomUUID().toString();
                    out.println("Generated new record endpoint = \"" + clientInformation.nextRecordEndpoint + "\"");
                    Response response = NanoHTTPD.newFixedLengthResponse(Status.OK, "text/plain", results);
                    response.addHeader("X-Record-Endpoint", clientInformation.nextRecordEndpoint);
                    return response;
                }

                out.println("Unknown or expired endpoint = \"" + session.getUri() + "\"");
                return NanoHTTPD.newFixedLengthResponse(Status.NOT_FOUND, "text/plain", "Unknown or expired endpoint");
            }

        };
        server.makeSecure(sslContext.getServerSocketFactory(), null);
        out.println("Server initialized on port " + port);
    }


    /**
     * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    public final MClientInformation getClientInformation() {
        return clientInformation;
    }

    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    private SSLContext createSSLContext(Context context) throws Exception {
        out.println("Initializing SSL configuration...");
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (InputStream keystoreStream = context.getResources().openRawResource(R.raw.keystore)) {
            keyStore.load(keystoreStream, "testtest".toCharArray());
            out.println("Keystore successfully loaded.");
        }
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, "testtest".toCharArray());
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), null, null);
        return sslContext;
    }
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    public final void start() throws Exception {
        out.println("Starting server...");
        server.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        out.println("Server started.\nWaiting for client to pair...");
    }
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    public final void stop() {
        out.println("Stopping server...");
        server.stop();
        out.println("Server stopped.");
    }
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    public final boolean isPaired() {
        boolean paired = !clientInformation.nextRecordEndpoint.equals("/initialize");
        return paired;
    }
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    public final void setOnPair(Runnable handler) {
        this.onPairHandler = handler;
    }


    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    public final void reset() {
        out.println("Resetting server state...");
        clientInformation.nextRecordEndpoint = "/initialize";
        clientInformation.registeredClientId = null;
        out.println("State reset complete.");
    }
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    private boolean isRateLimited() {
        return false;
    }
}







