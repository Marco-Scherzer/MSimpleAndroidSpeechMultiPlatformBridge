package com.marcoscherzer.msimplespeechbackend.server;

import android.os.Handler;
import android.os.Looper;
import com.marcoscherzer.msimplespeechbackend.server.gui.MServerPanel;
import com.marcoscherzer.msimplespeechbackend.util.gui.MSimpleConsole2TextAreaRedirector;

/**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
public class MSimpleSpeechServerCreator {

    private MSimpleSpeechBackendServer server;
    private MServerPanel gui;

    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    public MSimpleSpeechServerCreator(MServerPanel gui){
        this.gui = gui;
    }


    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    public final MSimpleSpeechBackendServer createServer(MISpeechRecognitionManager recognitionManager) {
        try {
            gui.getResetButton().startClickAnimation("Waiting for Client...");
            if (server != null) server.stop();
            server = new MSimpleSpeechBackendServer(8443, recognitionManager, gui.getLayout().getContext(),new MSimpleConsole2TextAreaRedirector(gui.getLogArea()));
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
            server.start();
            //gui.getServerPanel().logArea.setSelection(gui.getServerPanel().logArea.getText().length());
        } catch (Exception exc) {
            try{gui.getLogArea().append(exc.getMessage() + "\n");} catch(Exception exc2) {exc2.printStackTrace();}
            exc.printStackTrace();
        }
        return server;
    }

}
