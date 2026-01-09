package com.marcoscherzer.msimplespeechbackend.client;

import com.marcoscherzer.msimplespeechbackend.util.MRunnable1P;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
public final class MSimpleSpeechClient {

    private final String baseUrl;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final String host;
    private final String clientId;

    // Handler für Record-Job
    private MRunnable1P<String> onRecordResponseHandler;
    private MRunnable1P<String> onRecordFailureHandler;
    private Runnable onRecordJobStartHandler;

    // Handler für FirstConnection-Job
    private MRunnable1P<String> onFirstConnectionResponseHandler;
    private MRunnable1P<String> onFirstConnectionFailureHandler;
    private Runnable onFirstConnectionJobStartHandler;

    private State state = null;//State.recordingJobFinished;
    private String recordEndpoint;

    public PrintStream out;

    /**
     * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
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
     * Setter für Record-Job-Handler
    */
    public final void setOnRecordResponse(MRunnable1P<String> handler) {
        this.onRecordResponseHandler = handler;
    }

    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
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
       * Setter für FirstConnection-Job-Handler
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
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    private StringBuilder doServerRequest(String endpoint, StringBuilder response_out) {
        StringBuilder response = new StringBuilder();
        try {
            out.println("request baseUrl = \"" + baseUrl + "\" endpoint = \"" + endpoint + "\"");
            URL url = new URL(baseUrl + endpoint);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setHostnameVerifier((hostname, session) -> host.equalsIgnoreCase(hostname));
            conn.setRequestProperty("X-Client-ID", clientId);
            out.println("reading response...");
            int responseCode = conn.getResponseCode();
            out.println("response code = \"" + responseCode + "\"");
            InputStream inputStream = (responseCode == 200) ? conn.getInputStream() : conn.getErrorStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }
            out.println("response = \"" + response + "\"");
            reader.close();

            response_out.append(response);
            recordEndpoint = conn.getHeaderField("X-Record-Endpoint");
            out.println("received record-endpoint = \"" + recordEndpoint + "\"");

        } catch (Exception exc) {
            out.println("Exception " + exc.getMessage());
            if (endpoint.equals("/initialize")) {
                if (onFirstConnectionFailureHandler != null) onFirstConnectionFailureHandler.run(exc.getMessage());
            } else {
                if (onRecordFailureHandler != null) onRecordFailureHandler.run(exc.getMessage());
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
    public MSimpleSpeechClient(String host, int port, Certificate ca, String caAlias,PrintStream out) throws Exception {
        this.out = out;
        this.host = host;
        this.baseUrl = "https://" + host + ":" + port;
        this.clientId = UUID.randomUUID().toString();

        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null, null);
        trustStore.setCertificateEntry(caAlias, ca);

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
    }

    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    public final void submitRecordJob() {
       if(recordEndpoint != null) executor.execute(recordJob);
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







