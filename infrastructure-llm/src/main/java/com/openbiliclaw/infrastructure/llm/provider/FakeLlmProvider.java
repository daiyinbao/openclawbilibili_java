/**
 * file: FakeLlmProvider.java
 * author: daiyinbao
 * date: 2026-08-09
 */
package com.openbiliclaw.infrastructure.llm.provider;

import com.openbiliclaw.domain.llm.LlmProvider;
import com.openbiliclaw.domain.llm.LlmRequest;
import com.openbiliclaw.domain.llm.LlmResponse;

public class FakeLlmProvider implements LlmProvider{

    @Override
    public LlmResponse complete(LlmRequest request) {
        
        String content = """
                  {
                    "summary": "fake llm response",
                    "echoModel": "%s",
                    "userPromptPreview": "%s"
                  }
                  """.formatted(
                  request.model(),
                  shorten(request.userPrompt(), 120)
          );

        
        return new LlmResponse(
                  providerName(),
                  request.model(),
                  content
          );
    }

    @Override
    public String providerName() {
        return "fake llm";
    }
    
    private String shorten(String text, int maxLength) {
          if (text == null) {
              return "";
          }
          if (text.length() <= maxLength) {
              return text;
          }
          return text.substring(0, maxLength) + "...";
      }


}
