package com.openbiliclaw.application.feedback;

import com.openbiliclaw.domain.recommendation.FeedbackType;
import com.openbiliclaw.domain.recommendation.RecommendationId;

public record RecordFeedbackCommand(
        RecommendationId recommendationId,
        FeedbackType feedbackType,
        String comment
) {
}

