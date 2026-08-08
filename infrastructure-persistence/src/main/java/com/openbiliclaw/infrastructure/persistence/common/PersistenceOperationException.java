/**
 * file: PersistenceOperationException.java
 * author: daiyinbao
 * date: 2026-08-03
 */
package com.openbiliclaw.infrastructure.persistence.common;

public class PersistenceOperationException extends RuntimeException{

    public PersistenceOperationException(String message) {
        super(message);
    }

    public PersistenceOperationException(String message, Throwable cause) {
        super(message, cause);
    }
    


}
