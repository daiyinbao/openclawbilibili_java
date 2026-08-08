package com.openbiliclaw.application.feedback;

import com.openbiliclaw.domain.recommendation.RecommendationId;

public record RecordFeedbackResult(RecommendationId recommendationId, boolean recorded) {
}

