package com.openbiliclaw.application.agent;

import com.openbiliclaw.application.event.UserEventPayload;
import com.openbiliclaw.application.feedback.RecordFeedbackCommand;
import com.openbiliclaw.domain.recommendation.RecommendationBatch;
import java.util.List;

public interface AgentCoordinator {
    ClosedLoopResult ingestAndRefresh(List<UserEventPayload> events);

    RecommendationBatch refreshRecommendations();

    FeedbackLoopResult recordFeedback(RecordFeedbackCommand command);
}

