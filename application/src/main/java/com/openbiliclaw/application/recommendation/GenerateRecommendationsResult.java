package com.openbiliclaw.application.recommendation;

import com.openbiliclaw.domain.recommendation.RecommendationBatch;

public record GenerateRecommendationsResult(
        RecommendationBatch batch,
        int sourceCandidateCount
) {
}

