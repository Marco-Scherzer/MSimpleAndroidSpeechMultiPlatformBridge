package com.marcoscherzer.msimplespeechbackend.server.gui;

import android.os.Handler;
import android.os.Looper;

import com.marcoscherzer.msimplespeechbackend.server.MISpeechRecognitionManager;
import com.marcoscherzer.msimplespeechbackend.server.MSimpleSpeechRecognitionServer;
import com.marcoscherzer.msimplespeechbackend.server.myownpairingprotocolserver.MUUIDTokenCreator;
import com.marcoscherzer.msimplespeechbackend.util.gui.MSimpleConsole2TextAreaRedirector;

/**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
public class MSimpleSpeechRecognitionServerCreator {

    private MSimpleSpeechRecognitionServer server;
    private MServerPanel gui;

    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    public MSimpleSpeechRecognitionServerCreator(MServerPanel gui){
        this.gui = gui;
    }


    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    public final MSimpleSpeechRecognitionServer createServer(MISpeechRecognitionManager recognitionManager, boolean setRecordTriggerToServerSide, boolean shutdownOnPossibleSecurityRisk) {
        try {
            gui.getResetButton().startClickAnimation("Waiting for Client...");
            if (server != null) server.stop();
            server = new MSimpleSpeechRecognitionServer(8443, recognitionManager, gui.getLayout().getContext(), new MSimpleConsole2TextAreaRedirector(gui.getLogArea()));
            server.setRecordTriggerToServerSide(setRecordTriggerToServerSide);
            server.setOnPair(new Runnable() {
                @Override
                public void run() {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("paired");
                            gui.getResetButton().stopClickAnimation("1. Start Client Pairing");
                        }
                    });
                }
            });
            server.start(new MUUIDTokenCreator(),shutdownOnPossibleSecurityRisk);
            //gui.getServerPanel().logArea.setSelection(gui.getServerPanel().logArea.getText().length());
        } catch (Exception exc) {
            try{gui.getLogArea().append(exc.getMessage() + "\n");} catch(Exception exc2) {exc2.printStackTrace();}
            exc.printStackTrace();
        }
        return server;
    }

}
