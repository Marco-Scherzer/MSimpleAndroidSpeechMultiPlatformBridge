package com.marcoscherzer.msimplespeechbackend.server;

import android.content.Intent;

/**
 *  * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 * Gemeinsames Interface für alle Speech-Recognition-Implementierungen.
 * Bisher 2 Implementierungen
 * MSpeechRecognitionManager
 * MIntentBasedSpeechRecognitionManager
 *
 * Weitere Implemtierungen könnten theoretisch auch die neuen web speech-apis direkt nutzen,
 * benutzung der on device speech recognition auf Android jedoch kostenlos und es ist nicht ausgeschlossen dass
 * die speechRecognizer API bald intern die neuen web speech-apis oder lokal gemini nano nutzt.
 * Direkte web API-Impl. für MISpeechRecognitionManager, dennoch nur sinnvoll wenn es Gründe gibt weswegen client device nicht direkt eine rest speech-api aufrufen sollte
 * , da mit web API nutzung direkt auf client aktuell klarer was verwendet wird.
 * (Eventuelle sinnhafte Anwendungfälle: nur bluetooth ohne netzwerk möglich, wenn client device unsicherer als server device bei direktem internet-access, etc.)
 * @version 1.1
 */
public abstract class MISpeechRecognitionManager {

    /**
     *  * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     * Startet die Spracherkennung.
     */
    protected abstract void startListening();

    /**
     *  * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     * Optional: Wartet synchron auf das Ergebnis.
     */
    protected abstract  String waitOnResults();

    /**
     *  * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     * Wird von der Activity weitergeleitet.
     * Für Intent-basierte Implementierungen notwendig.
     * Andere Implementierungen können es ignorieren.
     */
    public abstract void onActivityResult(int requestCode, int resultCode, Intent data);

    /**
     *  * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     * Aufräumen, falls nötig.
     */
    protected abstract void destroy();
}
