package com.marcoscherzer.msimplespeechbackend.app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.marcoscherzer.msimplespeechbackend.R;
import com.marcoscherzer.msimplespeechbackend.client.MSimpleSpeechClientCreator;
import com.marcoscherzer.msimplespeechbackend.server.MISpeechRecognitionManager;
import com.marcoscherzer.msimplespeechbackend.server.MIntentBasedSpeechRecognitionManager;
import com.marcoscherzer.msimplespeechbackend.server.MSimpleSpeechBackendServer;
import com.marcoscherzer.msimplespeechbackend.client.MSimpleSpeechClient;
import com.marcoscherzer.msimplespeechbackend.server.MSimpleSpeechServerCreator;

import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;


/**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
public final class MMain extends AppCompatActivity {

    private static MMain instance;
    private static final int REQUEST_RECORD_AUDIO = 1;
    private MSimpleSpeechBackendServer server;
    private MSimpleSpeechClient client;
    private MMiniGui gui;
    private MISpeechRecognitionManager speechRecognitionManager;
    MSimpleSpeechClientCreator clientCreator;
    MSimpleSpeechServerCreator serverCreator;
    private Certificate ca;

    /**
     * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    public static MMain get() { return instance; }

    /**
     * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    public MSimpleSpeechBackendServer getServer() { return server; }

    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    @Override
    protected final void onCreate(Bundle savedInstanceState) {
       try {
           super.onCreate(savedInstanceState);
           instance = this;
           System.out.println("Application started");
           if (getSupportActionBar() != null) getSupportActionBar().hide();

           gui = new MMiniGui(this);
           setContentView(gui.getLayout());
           clientCreator = new MSimpleSpeechClientCreator(gui.getClientPanel()) {
               @Override
               public void runOnUiThread(Runnable r) {
                   MMain.this.runOnUiThread(r);
               }
           };

           serverCreator = new MSimpleSpeechServerCreator(gui.getServerPanel());

           if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
               ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO);
           }

           //speechRecognitionManager = new MSpeechRecognitionManager(this);
           speechRecognitionManager = new MIntentBasedSpeechRecognitionManager(this);
           gui.getServerPanel().getResetButton().setOnClickListener(v -> {
               server = serverCreator.createServer( speechRecognitionManager );
               server.setRecordTriggerToServerSideRecordTrigger(false);
           });

           gui.getClientPanel().getRecordButton().setEnabled(false);
           CertificateFactory cf = CertificateFactory.getInstance("X.509");
           try (InputStream caInput = gui.getLayout().getContext().getResources().openRawResource(R.raw.speechcert)) {
               ca = cf.generateCertificate(caInput);
           }

           client = clientCreator.createClient(ca);
           gui.getClientPanel().getConnectButton().setOnClickListener(v -> {
               client.submitFirstConnectionJob();
           });
           gui.getClientPanel().getRecordButton().setOnClickListener(v -> {
               //MSimpleSpeechClient.State state = client.getState();
               //if (state == MSimpleSpeechClient.State.recordingJobFinished) {
               if(client.isReady()) client.submitRecordJob();
               //}
           });
       }catch(Exception exc){
            exc.printStackTrace();
       }
    }

    /**
     * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    @Override protected final void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        speechRecognitionManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {  //server side record button dbg
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            System.out.println("VOLUME UP pressed → triggering speech job");
            if(client.isReady()) {
                //System.out.println(MMain.get().getServer().getClientInformation());//dbg
                server.startRecordEventAndSendResultToClient();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    @Override
    protected final void onDestroy() {
        super.onDestroy();
        if (server != null) server.stop();
        if (client != null) client.shutdown();
    }
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    @Override
    public final void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                gui.getServerPanel().getLogArea().append("Microphone access forbidden.\n");
                //gui.getClientPanel().getRecordButton().setEnabled(false);
            }
        }
    }
}










