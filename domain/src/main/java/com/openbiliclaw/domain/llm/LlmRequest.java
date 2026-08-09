/**
 * file: LlmRequest.java
 * author: daiyinbao
 * date: 2026-08-09
 */
package com.openbiliclaw.domain.llm;

public  record LlmRequest(
          String systemPrompt,
          String userPrompt,
          String model,
          double temperature,
          int maxTokens
  ) {
    
    public LlmRequest {
          if (systemPrompt == null || systemPrompt.isBlank()) {
              throw new IllegalArgumentException("systemPrompt must not be blank");
          }
          if (userPrompt == null || userPrompt.isBlank()) {
              throw new IllegalArgumentException("userPrompt must not be blank");
          }
          if (model == null || model.isBlank()) {
              throw new IllegalArgumentException("model must not be blank");
          }
          if (temperature < 0.0 || temperature > 2.0) {
              throw new IllegalArgumentException("temperature must be in [0.0, 2.0]");
          }
          if (maxTokens <= 0) {
              throw new IllegalArgumentException("maxTokens must be > 0");
          }
      }


}
