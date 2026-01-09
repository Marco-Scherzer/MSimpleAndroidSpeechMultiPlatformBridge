package com.marcoscherzer.msimplespeechbackend.server;

import java.io.*;
import java.net.Socket;
import java.security.KeyStore;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;

import com.marcoscherzer.msimplespeechbackend.R;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
/**
 * @version 0.0.2 ,  raw SSL-Sockets
 * unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
public final class MSimpleSpeechBackendServer {

    private final MISpeechRecognitionManager recognizer;
    private Runnable onPairHandler;
    public PrintStream out;

    private SSLServerSocket serverSocket;
    private final ExecutorService pool = Executors.newSingleThreadExecutor();
    private volatile boolean canceled;

    /**
     * @version 0.0.1
     * unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    public static class MClientInformation {
        /**
         * @version 0.0.1
         * unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
         */
        MClientInformation(String nextRecordEndpoint, String ip, String registeredClientId) {
            this.nextRecordEndpoint = nextRecordEndpoint;
            this.ip = ip;
            this.registeredClientId = registeredClientId;
        }

        private String nextRecordEndpoint;
        private String ip;
        private String registeredClientId;

        @Override
        public String toString() {
            return "MClientInformation{" +
                    "nextRecordEndpoint='" + nextRecordEndpoint + '\'' +
                    ", ip='" + ip + '\'' +
                    ", registeredClientId='" + registeredClientId + '\'' +
                    '}';
        }
    }

    private MClientInformation clientInformation;
    private static final String UUID_REGEX = "^[A-Za-z0-9_-]{1,64}$";

    /**
     * @version 0.0.2 ,  raw SSL-Sockets
     * unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    public MSimpleSpeechBackendServer(int port, MISpeechRecognitionManager recognitionManager, Context context, PrintStream out) throws Exception {
        this.recognizer = recognitionManager;
        this.out = out;
        out.println("Initializing server...");
        SSLContext sslContext = createSSLContext(context);
        SSLServerSocketFactory factory = sslContext.getServerSocketFactory();
        serverSocket = (SSLServerSocket) factory.createServerSocket(port);
        out.println("Server initialized on port " + port);
    }

    /**
     * @version 0.0.2 ,  raw SSL-Sockets
     * unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
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
     * @version 0.0.2 ,  raw SSL-Sockets
     * unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    public final void start() {
        out.println("Starting server...");
        clientInformation = new MClientInformation("initialize", null, null);
        pool.submit(() -> {
            while (!canceled) {
                try {
                    out.println("listening for new connection...");
                    Socket socket = serverSocket.accept();
                    out.println("new connection accepted...");
                    handleClient(socket);
                    socket.close();
                } catch (IOException e) {
                    if (!canceled) e.printStackTrace(out);
                }
            }
        });
        out.println("Server started.\nWaiting for client to pair...");
    }


    /**
     * @version 0.0.2 ,  raw SSL-Sockets
     * unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    private void handleClient(Socket socket) {
        MMaxLineLengthLineReader reader = null;
            PrintWriter writer = null;

            try {
                reader = new MMaxLineLengthLineReader(new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(socket.getOutputStream(), true);

                // Erste Zeile: Client-ID
                String incomingClientId = reader.readLine(200);
                out.println("Incoming clientId: " + incomingClientId);

                if (incomingClientId == null || !incomingClientId.matches(UUID_REGEX)) {
                    writer.println("error");
                    writer.println("Invalid client ID");
                    return;
                }

                // Zweite Zeile: Endpoint Request
                String requestEndpoint = reader.readLine(200);
                out.println("Request endpoint: " + requestEndpoint);

                if (requestEndpoint == null || !requestEndpoint.matches(UUID_REGEX)) {
                    writer.println("error");
                    writer.println("Invalid requestEndpoint");
                    return;
                }

                // Registrierung
                if (requestEndpoint.equals("initialize") && clientInformation.registeredClientId == null) {
                    clientInformation.registeredClientId = incomingClientId;
                    clientInformation.ip = socket.getInetAddress().getHostAddress();
                    out.println("Registered new client ID = \"" + incomingClientId + "\"");

                    if (onPairHandler != null) {
                        onPairHandler.run();
                    }

                    clientInformation.nextRecordEndpoint = UUID.randomUUID().toString();

                    writer.println(clientInformation.nextRecordEndpoint);
                    writer.println("Paired successfully");
                    return;

                } else if (!incomingClientId.equals(clientInformation.registeredClientId)) {
                    writer.println("error");
                    writer.println("Unknown client");
                    return;
                }

                if (clientInformation.nextRecordEndpoint.equals(requestEndpoint)) {
                    String results = "";
                    if (isPaired()) {
                        out.println("Starting recognizer...");
                        recognizer.startListening();
                        results = recognizer.waitOnResults();
                        out.println("Recognition complete.");
                    }

                    clientInformation.nextRecordEndpoint = UUID.randomUUID().toString();
                    writer.println(clientInformation.nextRecordEndpoint);
                    writer.println(results);

                } else {
                    writer.println("error");
                    writer.println("Unknown or expired endpoint");
                }

            } catch (Exception exc) {
                exc.printStackTrace(out);
            } finally {
                try { if (reader != null) reader.close();} catch (IOException exc) {}
                if (writer != null) writer.close();
                try {socket.close();} catch (IOException exc) {}
            }
    }
    /**
     * @version 0.0.2 ,  raw SSL-Sockets
     * unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    public final void stop() {
        out.println("Stopping server...");
        canceled = true;
        pool.shutdownNow();
        try {
            serverSocket.close();
        } catch (IOException ignored) {}
        out.println("Server stopped.");
    }
    /**
     * @version 0.0.1
     * unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    public final boolean isPaired() {
        return !clientInformation.nextRecordEndpoint.equals("initialize");
    }
    /**
     * @version 0.0.1
     * unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    public final void setOnPair(Runnable handler) {
        this.onPairHandler = handler;
    }

}









