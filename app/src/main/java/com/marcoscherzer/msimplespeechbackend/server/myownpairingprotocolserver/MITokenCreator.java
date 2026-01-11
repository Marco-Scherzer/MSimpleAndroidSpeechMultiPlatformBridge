package com.marcoscherzer.msimplespeechbackend.server.myownpairingprotocolserver;
/**
 * @version 0.0.2 ,  raw SSL-Sockets
 * unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
 */
public interface MITokenCreator {
    /**
     * @version 0.0.2 ,  raw SSL-Sockets
     * unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
  public abstract String createNewToken();
    /**
     * @version 0.0.2 ,  raw SSL-Sockets
     * unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
  public abstract String getInitialToken();
    /**
     * @version 0.0.2 ,  raw SSL-Sockets
     * unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
     */
  public abstract String getValidationPattern();

  /**
   * @version 0.0.2 ,  raw SSL-Sockets
   * unready intermediate state, @author Marco Scherzer, Author, Ideas, APIs, Nomenclatures & Architectures Marco Scherzer, Copyright Marco Scherzer, All rights reserved
   */
  public abstract boolean validate(String in,String val);

}
