/**
 * file: LlmException.java
 * author: daiyinbao
 * date: 2026-08-09
 */
package com.openbiliclaw.shared.kernel;

public class LlmException extends RuntimeException{

    public LlmException(String message) {
        super(message);
    }

    public LlmException(String message, Throwable cause) {
        super(message, cause);
    }
    


}
