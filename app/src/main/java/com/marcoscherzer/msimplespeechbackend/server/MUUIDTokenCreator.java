package com.marcoscherzer.msimplespeechbackend.server;

import java.util.UUID;

/**
 * @version 0.0.2 ,  raw SSL-Sockets
 * unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
public class MUUIDTokenCreator implements MITokenCreator<UUID> {
    private static final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
    //private static final int UUID_REGEX_LENGTH = 32 + 4;
    private static final UUID INITIALIZE_UUID = UUID.fromString("8f3c2b4e-7c1a-4d8a-9e3e-2b0f6a9d4c12");//um extrastring prüfung für "init" zu sparen
    /**
     * @version 0.0.2 ,  raw SSL-Sockets
     * unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    @Override
    public final UUID createNewToken() {
        return UUID.randomUUID();
    }
    /**
     * @version 0.0.2 ,  raw SSL-Sockets
     * unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    @Override
    public final UUID getInitialToken() {
        return INITIALIZE_UUID;
    }
    /**
     * @version 0.0.2 ,  raw SSL-Sockets
     * unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
    @Override
    public final String getValidationPattern() {
        return UUID_REGEX;
    }
}
