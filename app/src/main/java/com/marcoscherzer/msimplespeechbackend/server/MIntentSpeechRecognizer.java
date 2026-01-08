package com.marcoscherzer.msimplespeechbackend.server;
import android.app.Activity;
import android.content.Intent;
import android.speech.RecognizerIntent;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

/**
 *@version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 * erster entwurf   temporär zu kompatiblität der public api von MSpeechRecognizer
 * Startet den Google-Sprachdialog (modal, stabil, zukunftssicher?!)
 */
public final class MIntentSpeechRecognizer {

    private static final int REQ_CODE_SPEECH_INPUT = 1001;

    private final Activity activity;
    private String resultText = "";
    private CountDownLatch latch;

    public MIntentSpeechRecognizer(Activity activity) {
        this.activity = activity;
    }
/**
 *@version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
  * erster entwurf  temporär zur kompatiblität der public api von MSpeechRecognizer
 * Startet den Google-Sprachdialog (modal, stabil, zukunftssicher)
 */
    public void startListening() {
        latch = new CountDownLatch(1);

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "de-DE");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Bitte sprechen…");
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);

        activity.startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
    }
    /**
     *@version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
    erster entwurf
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_SPEECH_INPUT) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (results != null && !results.isEmpty()) {
                    resultText = results.get(0);
                } } latch.countDown();
        }
    }

   /**
     *@version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
       erster entwurf  temporär zur kompatiblität der public api von MSpeechRecognizer
     */
    public String waitOnResults() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return resultText;
    }


    public void destroy() {
    }
}

