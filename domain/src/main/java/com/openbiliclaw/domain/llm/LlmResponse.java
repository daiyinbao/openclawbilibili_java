/**
 * file: LlmResponse.java
 * author: daiyinbao
 * date: 2026-08-09
 */
package com.openbiliclaw.domain.llm;

public record LlmResponse(
        String  provider,
        String  model,
        String content
        ) {
    public LlmResponse{

          if (provider == null || provider.isBlank()) {
              throw new IllegalArgumentException("provider must not be blank");
          }
          if (model == null || model.isBlank()) {
              throw new IllegalArgumentException("model must not be blank");
          }
          if (content == null || content.isBlank()) {
              throw new IllegalArgumentException("content must not be blank");
          }
    }


}
