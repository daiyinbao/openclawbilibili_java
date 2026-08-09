/**
 * file: LlmProvider.java
 * author: daiyinbao
 * date: 2026-08-09
 */
package com.openbiliclaw.domain.llm;

public interface LlmProvider {
    
    String providerName();

    LlmResponse complete(LlmRequest llmRequest);

}
