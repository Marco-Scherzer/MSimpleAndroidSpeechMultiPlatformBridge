package com.marcoscherzer.msimplespeechbackend.server;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.content.Context;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
/**
 * @version 0.0.2 ,  raw SSL-Sockets
 * @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
---
## Protocol Definition (my own custom network device pairing protocol)

### Handshake
- **Step 0:** The user has to start the pairing handshake process on the server (e.g., by pressing a button)
- **Step 1:** The client registers with it's token ID at the server.
- **Step 2:** The server answers with a one time usable endpoint-token for the next request.


### Content Transmission
- **Step 3:** The client requests the next content by using the one-time usable endpoint-token.
- **Step 4:** The server sends the content plus the next one‑time usable endpoint-token … and so on (3,4, 3,4, …)


 **Security Features:**
1. Once a client was connected for the first time, the server blocks any other (unknown) client that has another ID pr tries to connect to the paired server.

2. The One‑time usable endpoints(tokens) make it impossible to reuse the current prepared endpoint-token.
If an unauthenticated client steals the ID and uses the current endpoint-token, the authenticated client cannot use the endpoint anymore and so cannot connect.
This indicates unauthenticated use (and can optionally trigger an alarm on the client-side as well as on the server-side).
Establishing a new connection after a case of unauthenticated use forces the user to actively restart the pairing (handshake) process on the server (e.g., by pressing a button) to reconnect and so to prevent unauthorized access.

3. Protocol-Mode shutdownOnPossibleSecurityRisk:
If shutdownOnPossibleSecurityRisk protocol‑mode is activated and the authenticated client cannot connect, or a client with a wrong ID or endpoint tries to connect, the server is, for security reasons, shut down immediately and has to be restarted actively by the user (e.g., by pressing a button), and the pairing with the client has to be renewed.
---
 */
public abstract class MSimplePairingProtocolServer {

    private MITokenCreator tokenCreator;
    public PrintStream out;
    private final SSLServerSocket serverSocket;
    private final ExecutorService serverLoop = Executors.newSingleThreadExecutor();
    private volatile boolean canceled;
    private boolean shutdownOnPossibleSecurityRisk = false;

    private Runnable onPairHandler;
    private Runnable onInvalidRequestEndpoint;
    private Runnable onUnknownClient;
    private Runnable onUnknownOrExpiredEndpoint;

    /**
     * @version 0.0.1
     * unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    public static final class MClientInformation {
        public int currentTokenLength;
        private String nextEndpoint;
        private String ip;
        private String registeredClientId;

        /**
         * @version 0.0.1
         * unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
         */
        MClientInformation(String nextEndpoint, String ip, String registeredClientId) {
            setNextEndpoint(nextEndpoint);
            this.ip = ip;
            this.registeredClientId = registeredClientId;
        }
        /**
         * @version 0.0.2
         * unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
         */
        public final void setNextEndpoint(String nextEndpoint) {
            this.nextEndpoint = nextEndpoint;
            this.currentTokenLength = nextEndpoint.length();
        }
        /**
         * @version 0.0.2
         * unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
         */
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


    /**
     * @version 0.0.2 ,  raw SSL-Sockets
     * unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    public MSimplePairingProtocolServer(int port,  Context context, PrintStream out) throws Exception {
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
    protected abstract SSLContext createSSLContext(Context context) throws Exception;


    /**
     * @version 0.0.2 ,  raw SSL-Sockets
     * unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    public final void start(MITokenCreator tokenCreator, boolean shutdownOnPossibleSecurityRisk) {
        out.println("Starting server...");
        this.tokenCreator = tokenCreator;
        this.shutdownOnPossibleSecurityRisk = shutdownOnPossibleSecurityRisk;
        clientInformation = new MClientInformation(tokenCreator.getInitialToken(), null, null);
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
                String incomingClientId = reader.readLine(clientInformation.currentTokenLength);
                if (incomingClientId != null) incomingClientId = incomingClientId.trim();
                out.println("Incoming clientId: " + incomingClientId);

                if (incomingClientId == null || !incomingClientId.matches(tokenCreator.getValidationPattern())) {
                    writer.println("error");
                    writer.println("Invalid client ID");
                    return;
                }

                // Zweite Zeile: Endpoint Request
                String requestEndpoint = reader.readLine(clientInformation.currentTokenLength);
                if (requestEndpoint != null) requestEndpoint = requestEndpoint.trim();
                out.println("Request endpoint: " + requestEndpoint);

                if (requestEndpoint == null || !requestEndpoint.matches(tokenCreator.getValidationPattern())) {
                    writer.println("error");
                    writer.println("Invalid requestEndpoint");
                    if(shutdownOnPossibleSecurityRisk) stop();
                    if(onInvalidRequestEndpoint!=null) onInvalidRequestEndpoint.run();
                    return;
                }

                // Registrierung ( z.B. connect Button )
                if (tokenCreator.validate(requestEndpoint,tokenCreator.getInitialToken()) && clientInformation.registeredClientId == null) {
                    clientInformation.registeredClientId = incomingClientId;
                    clientInformation.ip = socket.getInetAddress().getHostAddress();
                    out.println("Registered new client ID = \"" + incomingClientId + "\"");

                    if (onPairHandler != null) { onPairHandler.run();}

                    clientInformation.setNextEndpoint(tokenCreator.createNewToken());

                    writer.println(clientInformation.nextEndpoint);
                    writer.println("Paired successfully");
                    return;

                } else if (!tokenCreator.validate(incomingClientId, clientInformation.registeredClientId)) {
                    writer.println("error");
                    writer.println("Unknown client");
                    if(shutdownOnPossibleSecurityRisk) stop();
                    if(onUnknownClient!=null) onUnknownClient.run();
                    return;
                }
                if (isPaired()) { //!clientInformation.nextRecordEndpoint.equals(INITIALIZE_UUID);
                    if (tokenCreator.validate(clientInformation.nextEndpoint,requestEndpoint)) {
                        clientInformation.setNextEndpoint(tokenCreator.createNewToken());
                        writer.println(clientInformation.nextEndpoint);
//---------------------------------------------- Payload Creation (pairing protocol independent) ------------------------------------
                        handlePayload(socket, writer);
//---------------------------------------------- End of Payload creation (pairing protocol independent) ------------------------------------

                    } else {
                        writer.println("error");
                        writer.println("Unknown or expired endpoint");
                        if(shutdownOnPossibleSecurityRisk) stop();
                        if(onUnknownOrExpiredEndpoint!=null) onUnknownOrExpiredEndpoint.run();
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
    protected abstract void handlePayload(Socket socket, PrintWriter writer) throws Exception;

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
        return !clientInformation.nextEndpoint.equals(tokenCreator.getInitialToken());
    }
    /**
     * @version 0.0.1
     * unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    public final void setOnPair(Runnable handler) {
        this.onPairHandler = handler;
    }
    /**
     * @version 0.0.2
     * unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    public final void setOnInvalidRequestEndpoint(Runnable handler) {
        this.onInvalidRequestEndpoint = handler;
    }
    /**
     * @version 0.0.2
     * unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    public final void setOnUnknownClient(Runnable handler) {
        this.onUnknownClient = handler;
    }

    /**
     * @version 0.0.2
     * unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    public final void setOnUnknownOrExpiredEndpoint(Runnable handler) {
        this.onUnknownOrExpiredEndpoint = handler;
    }



}









