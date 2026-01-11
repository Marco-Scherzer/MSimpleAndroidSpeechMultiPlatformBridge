package com.marcoscherzer.msimplespeechbackend.server.myownpairingprotocolserver;

import java.util.UUID;

/**
 * @version 0.0.2 ,  raw SSL-Sockets
 * unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
public final class MUUIDTokenCreator implements MITokenCreator {
    private static final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
    private static final UUID INITIAL_UUID = UUID.fromString("8f3c2b4e-7c1a-4d8a-9e3e-2b0f6a9d4c12");//um extrastring prüfung für "init" zu sparen
    /**
     * @version 0.0.2 ,  raw SSL-Sockets
     * unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    @Override
    public final String createNewToken() {
        return UUID.randomUUID().toString();
    }
    /**
     * @version 0.0.2 ,  raw SSL-Sockets
     * unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    @Override
    public final String getInitialToken() {
        return INITIAL_UUID.toString();
    }
    /**
     * @version 0.0.2 ,  raw SSL-Sockets
     * unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    @Override
    public final String getValidationPattern() {
        return UUID_REGEX;
    }
    /**
     * @version 0.0.2 ,  raw SSL-Sockets
     * unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    @Override
    public final boolean validate(String in, String val) {
       return in.equals(val);
    }

}
