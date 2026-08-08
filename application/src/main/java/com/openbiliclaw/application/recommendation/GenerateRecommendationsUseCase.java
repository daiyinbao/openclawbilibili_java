package com.openbiliclaw.application.recommendation;

public interface GenerateRecommendationsUseCase {
    GenerateRecommendationsResult execute(GenerateRecommendationsCommand command);
}

