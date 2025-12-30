package com.marcoscherzer.msimplespeechbackend.client;


import android.content.Context;

import com.marcoscherzer.msimplespeechbackend.R;
import com.marcoscherzer.msimplespeechbackend.client.gui.MClientPanel;
import com.marcoscherzer.msimplespeechbackend.util.MRunnable1P;

import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import com.marcoscherzer.msimplespeechbackend.R;
import com.marcoscherzer.msimplespeechbackend.util.MRunnable1P;
import com.marcoscherzer.msimplespeechbackend.util.gui.MSimpleConsole2TextAreaRedirector;

import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
/**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
public abstract class MSimpleSpeechClientCreator {

    private MSimpleSpeechClient client;
    private MClientPanel gui;

    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    public MSimpleSpeechClientCreator(MClientPanel gui){
        this.gui = gui;
    }


    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    public final MSimpleSpeechClient createClient(Certificate ca) {
        try {
            if (client != null) client.shutdown();
            client = new MSimpleSpeechClient("localhost", 8443, ca, "mykey",new MSimpleConsole2TextAreaRedirector(gui.getLogArea()));
            client.setOnFirstConnectionJobStart(() -> runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gui.getConnectButton().startClickAnimation("connecting...");
                }
            }));

            client.setOnFirstConnectionResponse(new MRunnable1P<String>() {
                @Override
                public void run(String response) {
                    runOnUiThread(new Runnable() {
                        final String response_ = response;
                        @Override
                        public void run() {
                            System.out.println("onFirstConnectionResponse \"" + response_ + "\"");
                            gui.getConnectButton().stopClickAnimation("2. Connect");
                            gui.getRecordButton().setEnabled(true);
                        }
                    });
                }
            });

            client.setOnFirstConnectionFailure(new MRunnable1P<String>() {
                @Override
                public void run(final String error) {
                    runOnUiThread(new Runnable() {
                        final String error_ = error;
                        @Override
                        public void run() {
                            System.out.println("OnFirstConnectionFailure " + error_ + "\n");
                        }
                    });
                }
            });

            // Aufnahme-Job startet
            client.setOnRecordJobStart(() -> runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gui.getRecordButton().setEnabled(false);
                    gui.getRecordButton().startClickAnimation("recording...");
                }
            }));

            // Aufnahme-Job liefert Antwort
            client.setOnRecordResponse(new MRunnable1P<String>() {
                @Override
                public void run(String response) {
                    runOnUiThread(new Runnable() {
                        final String response_ = response;
                        @Override
                        public void run() {
                            System.out.println("onRecordResponse \"" + response_ + "\"");
                            gui.getRecordButton().stopClickAnimation("Microphone");
                            gui.getRecordButton().setEnabled(true);
                            gui.getLogArea().append(response_ + "\n");
                        }
                    });
                }
            });

            // Aufnahme-Job schlägt fehl
            client.setOnRecordFailure(new MRunnable1P<String>() {
                @Override
                public void run(final String error) {
                    runOnUiThread(new Runnable() {
                        final String error_ = error;
                        @Override
                        public void run() {
                            System.out.println("onRecordFailure " + error_ + "\n");
                            gui.getRecordButton().setText("Start");
                            gui.getRecordButton().setEnabled(true);
                        }
                    });
                }
            });

        } catch (Exception exc) {
            System.out.println("Initialization error: ");
            try{gui.getLogArea().append(exc.getMessage() + "\n");} catch(Exception exc2) {exc2.printStackTrace();}
            exc.printStackTrace();
        }
        return client;
    }

    /**
     * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    public abstract void runOnUiThread(Runnable r);
}
