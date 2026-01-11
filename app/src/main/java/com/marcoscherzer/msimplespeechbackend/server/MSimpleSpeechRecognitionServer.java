package com.marcoscherzer.msimplespeechbackend.server;

import android.content.Context;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 11.01.2026 14:08
 * @version 0.0.2 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
public class  MSimpleSpeechRecognitionServer extends MSimplePairingProtocolServer {

    //-------------------------------------------------- Speech Recognition specific stuff ------------------------------------------------------------------------
    private final MISpeechRecognitionManager recognizer;
    /**
     * @version 0.0.2 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    private enum RECORD_TRIGGER_LOCATION_MODE{
        RECORD_ONLY_ON_SERVERSIDE_EVENT,  //z.B headsetbutton
        RECORD_ALWAYS_ON_REQUEST; //z.B client-side softwarebutton
    }

    private RECORD_TRIGGER_LOCATION_MODE mode = RECORD_TRIGGER_LOCATION_MODE.RECORD_ALWAYS_ON_REQUEST;

    private CompletableFuture<Void> recordEventTrigger;

    private volatile boolean hasEvent = false;
    /**
     * @param port
     * @param recognitionManager
     * @param context
     * @param out
     * @version 0.0.2 ,  raw SSL-Sockets
     * unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    public MSimpleSpeechRecognitionServer(int port, MISpeechRecognitionManager recognitionManager, Context context, PrintStream out) throws Exception {
        super(port, context, out);
        this.recognizer = recognitionManager;
    }

    /**
     * @version 0.0.2 ,  raw SSL-Sockets
     * unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     * true   RECORD_ONLY_ON_SERVERSIDE_EVENT,  z.B headsetbutton
     * false  RECORD_ALWAYS_ON_REQUEST,  z.B client-side softwarebutton
     * default true RECORD_ALWAYS_ON_REQUEST,  z.B client-side softwarebutton
     */
    public final void setRecordTriggerToServerSide(boolean setRecordTriggerToServerSide){
        mode = setRecordTriggerToServerSide ? RECORD_TRIGGER_LOCATION_MODE.RECORD_ONLY_ON_SERVERSIDE_EVENT : RECORD_TRIGGER_LOCATION_MODE.RECORD_ALWAYS_ON_REQUEST;
    }


    /**
     * @version 0.0.2 ,  raw SSL-Sockets
     * unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    public final void startRecordEventAndSendResultToClient() throws UnsupportedOperationException{
        System.out.println("startRecordEventAndSendResultToClient()");//dbg
        if( mode == RECORD_TRIGGER_LOCATION_MODE.RECORD_ONLY_ON_SERVERSIDE_EVENT) {
            System.out.println("(mode == RECORD_TRIGGER_LOCATION_MODE.RECORD_ONLY_ON_SERVERSIDE_EVENT)");//dbg
            hasEvent = true;
            if (recordEventTrigger != null) {
                recordEventTrigger.complete(null);
            }
        } else throw new UnsupportedOperationException("Error: Calling startRecordEventAndSendResultToClient() is only supported for mode RECORD_ONLY_ON_SERVERSIDE_EVENT. Use setRecordTriggerToServerSideRecordTrigger(..) to change mode.");
    }

    /**
     * @version 0.0.2 ,  raw SSL-Sockets
     * unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    public final void handlePayload(Socket socket, PrintWriter writer) throws SocketException, ExecutionException, InterruptedException {
        //Speech Recognition
        String results;
        switch (mode) {
            case RECORD_ONLY_ON_SERVERSIDE_EVENT:

                // Wenn ein Event gespeichert ist sofort starten
                if (hasEvent) {
                    System.out.println("working of recordEvent triggered during reconnection");
                    hasEvent = false;//Event verbrauchen
                    out.println("recordEvent (queued)");
                    recognizer.startListening();
                    results = recognizer.waitOnResults();
                    writer.println(results);
                    break;
                }

                System.out.println("polling and waiting for recordEvent");
                recordEventTrigger = new CompletableFuture<Void>();
                socket.setSoTimeout(30000);
                try {
                    recordEventTrigger.get(25000, TimeUnit.MILLISECONDS);
                    hasEvent=false;
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
                break;

            case RECORD_ALWAYS_ON_REQUEST:
                out.println("Starting recognizer...");
                recognizer.startListening();
                results = recognizer.waitOnResults();
                out.println("Recognition complete.");
                writer.println(results);
                break;
        }
    }
}
