package com.marcoscherzer.msimplespeechbackend.server;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
public final class MSpeechRecognizer {

    private final SpeechRecognizer recognizer;
    private final StringBuilder resultText = new StringBuilder();

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition stateChanged = lock.newCondition();
    private volatile boolean speechRecognizerIsRunning;

    private final Runnable recognizerRunnable;

    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    public MSpeechRecognizer(Context context) {
        recognizer = SpeechRecognizer.createSpeechRecognizer(context);

        recognizer.setRecognitionListener(new RecognitionListener() {
            /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
            @Override
            public final void onReadyForSpeech(Bundle params) { //onReadyForSpeech – der Dienst ist bereit, Audio anzunehmen.
                System.out.println("SpeechRecognizer.onReadyForSpeech(params=" + params + ")|");
            }
            /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
            @Override
            public final void onBeginningOfSpeech() { //onBeginningOfSpeech – tatsächlich kommen Sprachdaten an.
                System.out.println("SpeechRecognizer.onBeginningOfSpeech|");
                lock.lock();
                try {
                    speechRecognizerIsRunning = true;
                    stateChanged.signalAll();
                } finally {
                    lock.unlock();
                }
            }
            /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
            @Override
            public final void onRmsChanged(float rmsdB) {
                //System.out.println("SpeechRecognizer.onRmsChanged| (rmsdB=" + rmsdB + ")|");
            }
            /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
            @Override
            public final void onBufferReceived(byte[] buffer) {
                System.out.println("SpeechRecognizer.onBufferReceived| (buffer=" + buffer + ")|");
            }
            /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
            @Override
            public final void onEndOfSpeech() { //onEndOfSpeech – die Aufnahme stoppt, der Dienst hat keine weiteren Audio-Frames mehr.
                System.out.println("SpeechRecognizer.onEndOfSpeech|");
            }
            /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
            @Override
            public final void onError(int error) { //nach interner Verarbeitung/Analyse des Audiostreams
                System.out.println("SpeechRecognizer.onError(error=" + error + ")|");
                lock.lock();
                try {
                    speechRecognizerIsRunning = false;
                    stateChanged.signalAll();
                } finally {
                    lock.unlock();
                }
            }
            /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
            @Override
            public final void onResults(Bundle results) {  //nach interner Verarbeitung/Analyse des Audiostreams
                System.out.println("SpeechRecognizer.onResults(results=" + results + ")|");

                ArrayList<String> matches =
                        results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null) {
                    for (String result : matches) {
                        resultText.append(result).append("\n");
                    }
                }

                System.out.println("SpeechRecognizer.onResults| results ready");
                lock.lock();
                try {
                    speechRecognizerIsRunning = false;
                    stateChanged.signalAll();
                } finally {
                    lock.unlock();
                }
            }

            private String lastPartial = "";
            /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
            @Override
            public final void onPartialResults(Bundle partialResults) {
                ArrayList<String> partial =
                        partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (partial != null && !partial.isEmpty()) {
                    String current = partial.get(0);
                    if (!current.equals(lastPartial)) {
                        // optional: resultText.append(current).append("\n");
                        lastPartial = current;
                    }
                }
            }
            /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
            @Override
            public final void onEvent(int eventType, Bundle params) {
                System.out.println("SpeechRecognizer.onResults(eventType=" + eventType + ", params="+params+")|");
            }
        });
        /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
        recognizerRunnable = () -> {
            System.out.println("MSpeechRecognizer.recognizerRunnable| Runnable running");
            resultText.setLength(0); // Reset

            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "de-DE");
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
            intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false);
            intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 5000);
            intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 3000);
            intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 2000);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "com.marcoscherzer.msimplespeechbackend");

            recognizer.startListening(intent);
        };



    }
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    public final void startListening() {
        new Handler(Looper.getMainLooper()).post(recognizerRunnable);

        System.out.println("MSpeechRecognizer.startListening| Runnable submitted");
    }
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    public final String waitOnResults() {
        System.out.println("MSpeechRecognizer.waitOnResults| waiting on speechRecognizerIsRunning=true");
        lock.lock();
        try {
            // auf START warten
            while (!speechRecognizerIsRunning) {
                stateChanged.await();
            }
            System.out.println("MSpeechRecognizer.waitOnResults| speechRecognizerIsRunning=" + speechRecognizerIsRunning);
            System.out.println("MSpeechRecognizer.waitOnResults| waiting on speechRecognizerIsRunning=false");
            // auf ENDE warten
            while (speechRecognizerIsRunning) {
                stateChanged.await();
            }
            System.out.println("MSpeechRecognizer.waitOnResults| speechRecognizerIsRunning=" + speechRecognizerIsRunning);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Warten unterbrochen", e);
        } finally {
            lock.unlock();
        }
        return resultText.toString();
    }
    /**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
    public final void destroy() {
        recognizer.destroy();
    }
}



