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
 * 11.01.2026 14:08
 * @version 0.0.2 ,  raw SSL-Sockets
 * unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
public abstract class MSimplePairingProtocolServer {

    private Runnable onPairHandler;
    public PrintStream out;
    private SSLServerSocket serverSocket;
    private final ExecutorService serverLoop = Executors.newSingleThreadExecutor();
    private volatile boolean canceled;
    private boolean shutdownOnPossibleSecurityRisk = false;

    /**
     * @version 0.0.1
     * unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    public static final class MClientInformation {
        /**
         * @version 0.0.1
         * unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
         */
        MClientInformation(String nextEndpoint, String ip, String registeredClientId) {
            this.nextEndpoint = nextEndpoint;
            this.ip = ip;
            this.registeredClientId = registeredClientId;
        }

        private String nextEndpoint;
        private String ip;
        private String registeredClientId;

        @Override
        public final String toString() {
            return "MClientInformation{" +
                    "nextRecordEndpoint='" + nextEndpoint + '\'' +
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
    public MSimplePairingProtocolServer(int port, Context context, PrintStream out) throws Exception {
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
    public final void start(boolean shutdownOnPossibleSecurityRisk) {
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
     * @version 0.0.2 ,  raw SSL-Sockets
     * unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    private void handleClient(Socket socket) {
        //---------------------------------------------- My own Pairing Protocol ------------------------------------
            MMaxLineLengthBufferedReader reader = null;
            PrintWriter writer = null;
            try {
                socket.setSoTimeout(3000);
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

                    if (onPairHandler != null) { onPairHandler.run();}

                    clientInformation.nextEndpoint = UUID.randomUUID().toString();

                    writer.println(clientInformation.nextEndpoint);
                    writer.println("Paired successfully");
                    return;

                } else if (!incomingClientId.equals(clientInformation.registeredClientId)) {
                    writer.println("error");
                    writer.println("Unknown client");
                    if(shutdownOnPossibleSecurityRisk) stop();
                    return;
                }
                if (isPaired()) { //!clientInformation.nextRecordEndpoint.equals(INITIALIZE_UUID);
                    if (clientInformation.nextEndpoint.equals(requestEndpoint)) {
                        clientInformation.nextEndpoint = UUID.randomUUID().toString();
                        writer.println(clientInformation.nextEndpoint);
//---------------------------------------------- Payload Creation (pairing protocol independent) ------------------------------------
                        handlePayload(socket, writer);
//---------------------------------------------- End of Payload creation (pairing protocol independent) ------------------------------------

                    } else {
                        writer.println("error");
                        writer.println("Unknown or expired endpoint");
                        if(shutdownOnPossibleSecurityRisk) stop();
                    }
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
    public abstract void handlePayload(Socket socket, PrintWriter writer) throws Exception;

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
        return !clientInformation.nextEndpoint.equals(INITIALIZE_UUID);
    }
    /**
     * @version 0.0.1
     * unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    public final void setOnPair(Runnable handler) {
        this.onPairHandler = handler;
    }


}









