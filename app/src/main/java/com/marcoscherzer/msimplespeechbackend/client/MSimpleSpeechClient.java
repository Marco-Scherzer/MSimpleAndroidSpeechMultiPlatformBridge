package com.marcoscherzer.msimplespeechbackend.client;

import com.marcoscherzer.msimplespeechbackend.util.MRunnable1P;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * @version 0.0.2 ,  raw SSL-Sockets
 *  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
public final class MSimpleSpeechClient {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final String host;
    private final int port;
    private final String clientId;
    private final SSLContext sslContext;

    // Handler für Record-Job
    private MRunnable1P<String> onRecordResponseHandler;
    private MRunnable1P<String> onRecordFailureHandler;
    private Runnable onRecordJobStartHandler;

    // Handler für FirstConnection-Job
    private MRunnable1P<String> onFirstConnectionResponseHandler;
    private MRunnable1P<String> onFirstConnectionFailureHandler;
    private Runnable onFirstConnectionJobStartHandler;

    private State state = null;
    private String recordEndpoint;

    public PrintStream out;

    public static enum State {
        firstConnectionJobStarted,
        firstConnectionJobFinished,
        recordingJobStarted,
        recordingJobFinished
    }
    /**
     * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    public final State getState() {
        return this.state;
    }
    /**
     * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    public final boolean isReady() {
        return state == State.firstConnectionJobFinished || state == State.recordingJobFinished;
    }
    /**
     * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    public final void setOnRecordResponse(MRunnable1P<String> handler) {
        this.onRecordResponseHandler = handler;
    }
    /**
     * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    /**
     * * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    public final void setOnRecordFailure(MRunnable1P<String> handler) {
        this.onRecordFailureHandler = handler;
    }
    /**
     * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    public final void setOnRecordJobStart(Runnable handler) {
        this.onRecordJobStartHandler = handler;
    }
    /**
     * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    public final void setOnFirstConnectionResponse(MRunnable1P<String> handler) {
        this.onFirstConnectionResponseHandler = handler;
    }
    /**
     * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    public final void setOnFirstConnectionFailure(MRunnable1P<String> handler) {
        this.onFirstConnectionFailureHandler = handler;
    }
    /**
     * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    public final void setOnFirstConnectionJobStart(Runnable handler) {
        this.onFirstConnectionJobStartHandler = handler;
    }

    /**
     * @version 0.0.2 ,  raw SSL-Sockets
     * unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    private StringBuilder doServerRequest(String endpoint, StringBuilder response_out) {
        StringBuilder response = new StringBuilder();

        try {
            out.println("connecting to " + host + ":" + port + " endpoint=" + endpoint);
            SSLSocketFactory factory = sslContext.getSocketFactory();
            try (SSLSocket socket = (SSLSocket) factory.createSocket(host, port);
                 PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                // Client-ID + Endpoint senden
                writer.println(clientId);
                writer.println(endpoint);

                // Erste Zeile = neuer Endpoint
                recordEndpoint = reader.readLine();
                out.println("received record-endpoint = \"" + recordEndpoint + "\"");

                // Restliche Antwort = Content
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line).append("\n");
                }

                out.println("response = \"" + response + "\"");
                response_out.append(response);
            }
        } catch (Exception exc) {
             out.println("Exception " + exc.getMessage());
            if ("/initialize".equals(endpoint)) {
                if (onFirstConnectionFailureHandler != null) {
                    onFirstConnectionFailureHandler.run(exc.getMessage());
                }
            } else {
                if (onRecordFailureHandler != null) {
                    onRecordFailureHandler.run(exc.getMessage());
                }
            }
        }
        return response;
    }
    /**
     * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    private final Runnable recordJob = new Runnable() {
        public void run() {
            state = State.recordingJobStarted;
            if (onRecordJobStartHandler != null) onRecordJobStartHandler.run();

            StringBuilder responseText_out = new StringBuilder();
            doServerRequest(recordEndpoint, responseText_out);

            if (onRecordResponseHandler != null) onRecordResponseHandler.run(responseText_out.toString());

            state = State.recordingJobFinished;
        }
    };
    /**
     * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    private final Runnable firstConnectionJob = new Runnable() {
        public void run() {
            state = State.firstConnectionJobStarted;
            if (onFirstConnectionJobStartHandler != null) onFirstConnectionJobStartHandler.run();

            StringBuilder responseText_out = new StringBuilder();
            doServerRequest("/initialize", responseText_out);

            if (onFirstConnectionResponseHandler != null) onFirstConnectionResponseHandler.run(responseText_out.toString());

            state = State.firstConnectionJobFinished;
        }
    };
    /**
     * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    public MSimpleSpeechClient(String host, int port, Certificate ca, String caAlias, PrintStream out) throws Exception {
        this.out = out;
        this.host = host;
        this.port = port;
        this.clientId = UUID.randomUUID().toString();

        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null, null);
        trustStore.setCertificateEntry(caAlias, ca);

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);

        sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);
    }
    /**
     * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    public final void submitRecordJob() {
        if (recordEndpoint != null) executor.execute(recordJob);
    }
    /**
     * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    public final void submitFirstConnectionJob() {
        executor.execute(firstConnectionJob);
    }
    /**
     * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    public final void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(3, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
}









