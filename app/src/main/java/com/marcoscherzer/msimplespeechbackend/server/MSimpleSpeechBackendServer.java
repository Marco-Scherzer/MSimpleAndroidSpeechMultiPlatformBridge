package com.marcoscherzer.msimplespeechbackend.server;

import static com.marcoscherzer.msimplespeechbackend.server.MSimpleSpeechBackendServer.RECORD_TRIGGER_LOCATION_MODE.CLIENTSIDE_CONNECTED_RECORDBUTTON;
import static com.marcoscherzer.msimplespeechbackend.server.MSimpleSpeechBackendServer.RECORD_TRIGGER_LOCATION_MODE.SERVERSIDE_CONNECTED_RECORDBUTTON;

import java.io.*;
import java.net.Socket;
import java.security.KeyStore;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.content.Context;

import com.marcoscherzer.msimplespeechbackend.R;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
/**
 * 0:50
 * @version 0.0.2 ,  raw SSL-Sockets
 * unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
public final class MSimpleSpeechBackendServer {

    private final MISpeechRecognitionManager recognizer;
    private Runnable onPairHandler;
    public PrintStream out;

    private SSLServerSocket serverSocket;
    private final ExecutorService serverLoop = Executors.newSingleThreadExecutor();
    private volatile boolean canceled;

    private final ExecutorService pollingThread = Executors.newSingleThreadExecutor();
    /**
     * @version 0.0.1
     * unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    public static final class MClientInformation {
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
        public final String toString() {
            return "MClientInformation{" +
                    "nextRecordEndpoint='" + nextRecordEndpoint + '\'' +
                    ", ip='" + ip + '\'' +
                    ", registeredClientId='" + registeredClientId + '\'' +
                    '}';
        }
    }

    private MClientInformation clientInformation;
    private static final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
    private static final int UUID_REGEX_LENGTH = 32 + 4;
    private static final String INITIALIZE_UUID = "8f3c2b4e-7c1a-4d8a-9e3e-2b0f6a9d4c12";//um extrastring prüfung für "init" zu sparen

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
        serverLoop.submit(() -> {
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
     * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    public static enum RECORD_TRIGGER_LOCATION_MODE{
        SERVERSIDE_CONNECTED_RECORDBUTTON,  //z.B headsetbutton
        CLIENTSIDE_CONNECTED_RECORDBUTTON; //z.B client-side softwarebutton
    }

    private RECORD_TRIGGER_LOCATION_MODE mode;

    private CompletableFuture<Void> recordEventTrigger;

    /**
     * @version 0.0.2 ,  raw SSL-Sockets
     * unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    public final void startRecordEvent(){
        recordEventTrigger.complete(null);
    }

    /**
     * @version 0.0.2 ,  raw SSL-Sockets
     * unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    private void handleClient(Socket socket) {
            MMaxLineLengthBufferedReader reader = null;
            PrintWriter writer = null;
            try {
                socket.setSoTimeout(3000);//initialtimeout vor upgrade
                reader = new MMaxLineLengthBufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(socket.getOutputStream(), true);

                // Erste Zeile: Client-ID
                String incomingClientId = reader.readLine(UUID_REGEX_LENGTH);
                if (incomingClientId != null) incomingClientId = incomingClientId.trim();
                out.println("Incoming clientId: " + incomingClientId);

                if (incomingClientId == null || !incomingClientId.matches(UUID_REGEX)) {
                    writer.println("error");
                    writer.println("Invalid client ID");
                    return;
                }

                // Zweite Zeile: Endpoint Request
                String requestEndpoint = reader.readLine(UUID_REGEX_LENGTH);
                if (requestEndpoint != null) requestEndpoint = requestEndpoint.trim();
                out.println("Request endpoint: " + requestEndpoint);

                if (requestEndpoint == null || !requestEndpoint.matches(UUID_REGEX)) {
                    writer.println("error");
                    writer.println("Invalid requestEndpoint");
                    return;
                }

                // Registrierung ( z.B. connect Button )
                if (requestEndpoint.equals(INITIALIZE_UUID) && clientInformation.registeredClientId == null) {
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

                // Speech Recognition ( server side record Button )
                if(mode == SERVERSIDE_CONNECTED_RECORDBUTTON)
                if (clientInformation.nextRecordEndpoint.equals(requestEndpoint)) {
                    String results;
                    clientInformation.nextRecordEndpoint = UUID.randomUUID().toString();
                    writer.println(clientInformation.nextRecordEndpoint);
                    if (isPaired()) {
                            recordEventTrigger = new CompletableFuture<Void>();
                            socket.setSoTimeout(30000);
                            try {
                                System.out.println("polling and waiting for recordEvent");
                                recordEventTrigger.get(25000, TimeUnit.MILLISECONDS);
                                System.out.println("recordEvent");
                                out.println("Starting recognizer...");
                                recognizer.startListening();
                                results = recognizer.waitOnResults();
                                out.println("Recognition complete.");
                                writer.println(results);
                            } catch (TimeoutException exc) {
                                System.out.println("Timeout: kein recordEvent, just polling new ");//
                                writer.println("");
                            }
                    }
                } else {
                    writer.println("error");
                    writer.println("Unknown or expired endpoint");
                }


                // Speech Recognition ( client side record Button )
                if(mode == CLIENTSIDE_CONNECTED_RECORDBUTTON)
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
        serverLoop.shutdownNow();
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
        return !clientInformation.nextRecordEndpoint.equals(INITIALIZE_UUID);
    }
    /**
     * @version 0.0.1
     * unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    public final void setOnPair(Runnable handler) {
        this.onPairHandler = handler;
    }

}









