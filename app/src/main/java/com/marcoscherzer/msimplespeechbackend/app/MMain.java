package com.marcoscherzer.msimplespeechbackend.app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.marcoscherzer.msimplespeechbackend.R;
import com.marcoscherzer.msimplespeechbackend.client.MSimpleSpeechClientCreator;
import com.marcoscherzer.msimplespeechbackend.server.MISpeechRecognitionManager;
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
    @Override
    protected final void onCreate(Bundle savedInstanceState) {
       try {
           super.onCreate(savedInstanceState);


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

           gui.getServerPanel().getResetButton().setOnClickListener(v -> {
               server = serverCreator.createServer( speechRecognitionManager );
           });

           gui.getClientPanel().getRecordButton().setEnabled(false);
           CertificateFactory cf = CertificateFactory.getInstance("X.509");
           try (InputStream caInput = gui.getLayout().getContext().getResources().openRawResource(R.raw.speechcert)) {
               ca = cf.generateCertificate(caInput);
           }

           gui.getClientPanel().getConnectButton().setOnClickListener(v -> {
               client = clientCreator.createClient(ca);
               client.submitFirstConnectionJob();
           });
           gui.getClientPanel().getRecordButton().setOnClickListener(v -> {
               MSimpleSpeechClient.State state = client.getState();
               //if (state == MSimpleSpeechClient.State.recordingJobFinished) {
               client.submitRecordJob();
               //}
           });
       }catch(Exception exc){
            exc.printStackTrace();
       }
    }

    /*
    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        speechRecognitionManager.onActivityResult(requestCode, resultCode, data);
    }
*/
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
                gui.getClientPanel().getRecordButton().setEnabled(false);
            }
        }
    }
}










