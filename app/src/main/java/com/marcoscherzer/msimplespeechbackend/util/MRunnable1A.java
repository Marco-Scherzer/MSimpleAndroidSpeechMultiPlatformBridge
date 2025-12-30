package com.marcoscherzer.msimplespeechbackend.util;

/**
 * @version 0.0.1 ,  unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */

public abstract class MRunnable1A<T> {
    protected final T attribute1;
    public MRunnable1A(T attribute1){
        this.attribute1=attribute1;
    }
    public abstract void run();

}
