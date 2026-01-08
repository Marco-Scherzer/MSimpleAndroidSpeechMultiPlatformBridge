package com.marcoscherzer.msimplespeechbackend.server;

import android.content.Intent;

/**
 *  * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 * Gemeinsames Interface für alle Speech-Recognition-Implementierungen.
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
