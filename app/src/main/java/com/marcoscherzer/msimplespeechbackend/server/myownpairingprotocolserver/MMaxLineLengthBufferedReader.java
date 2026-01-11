package com.marcoscherzer.msimplespeechbackend.server.myownpairingprotocolserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * @version 0.0.1 , raw SSL-Sockets
 * unready intermediate state
 * Author, Ideas, APIs, Nomenclatures & Architectures: Marco Scherzer
 * Copyright Marco Scherzer, All rights reserved
 *
 * A safe line reader that prevents unbounded memory usage by limiting
 * the maximum allowed line length.
 */
public final class MMaxLineLengthBufferedReader extends BufferedReader {

    public MMaxLineLengthBufferedReader(Reader in) {
        super(in);
    }

    /**
     * @version 0.0.1 , raw SSL-Sockets
     * unready intermediate state
     * Author, Ideas, APIs, Nomenclatures & Architectures: Marco Scherzer
     *  Copyright Marco Scherzer, All rights reserved
     *
     * Reads a line safely with a maximum length limit.
     * Prevents DoS attacks via extremely long lines.
     */
    public final String readLine(int maxLineLength) throws IOException {
        StringBuilder sb = new StringBuilder();
        int c;

        while ((c = super.read()) != -1) {
            if (c == '\n') {
                break;
            }

            sb.append((char) c);

            if (sb.length() > maxLineLength) {
                throw new IOException("Line too long");
            }
        }

        // If stream ended and nothing was read return null
        if (c == -1 && sb.length() == 0) {
            return null;
        }

        return sb.toString();
    }
}

