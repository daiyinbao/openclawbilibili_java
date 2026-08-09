/**
 * file: DefaultLlmService.java
 * author: daiyinbao
 * date: 2026-08-09
 */
package com.openbiliclaw.infrastructure.llm.service;

import java.util.Objects;

import com.openbiliclaw.domain.llm.LlmProvider;
import com.openbiliclaw.domain.llm.LlmRequest;
import com.openbiliclaw.domain.llm.LlmResponse;
import com.openbiliclaw.domain.llm.LlmService;
import com.openbiliclaw.shared.kernel.LlmException;

public class DefaultLlmService implements LlmService {

    private final LlmProvider llmProvider;
     

    public DefaultLlmService(LlmProvider llmProvider) {
        this.llmProvider = Objects.requireNonNull(llmProvider,"llmProvider must not be null");
    }


    @Override
    public LlmResponse complete(LlmRequest request) {
        Objects.requireNonNull(request,"request must not be null");

        try {
            LlmResponse response = llmProvider.complete(request);

            if (response == null) {
                  throw new LlmException("LLM provider returned null response");
            }

            if (response.content() == null || response.content().isBlank()) {
                  throw new LlmException("LLM provider returned blank content");
            }
            return response;
        }catch( LlmException exception){
              throw exception;
        } 
        catch (Exception exception) {
            
            throw new LlmException("LLM completion failed", exception);
        }
    }
    


}
