package com.openbiliclaw.application.agent;

import com.openbiliclaw.application.discovery.EvaluateCandidatesCommand;
import com.openbiliclaw.application.discovery.EvaluateCandidatesUseCase;
import com.openbiliclaw.application.discovery.RunDiscoveryCommand;
import com.openbiliclaw.application.discovery.RunDiscoveryUseCase;
import com.openbiliclaw.application.event.IngestEventCommand;
import com.openbiliclaw.application.event.IngestEventUseCase;
import com.openbiliclaw.application.event.UserEventPayload;
import com.openbiliclaw.application.feedback.RecordFeedbackCommand;
import com.openbiliclaw.application.feedback.RecordFeedbackUseCase;
import com.openbiliclaw.application.profile.UpdateProfileCommand;
import com.openbiliclaw.application.profile.UpdateProfileUseCase;
import com.openbiliclaw.application.recommendation.GenerateRecommendationsCommand;
import com.openbiliclaw.application.recommendation.GenerateRecommendationsUseCase;
import com.openbiliclaw.domain.recommendation.RecommendationBatch;
import java.util.List;

public final class DefaultAgentCoordinator implements AgentCoordinator {
    private final IngestEventUseCase ingestEventUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;
    private final RunDiscoveryUseCase runDiscoveryUseCase;
    private final EvaluateCandidatesUseCase evaluateCandidatesUseCase;
    private final GenerateRecommendationsUseCase generateRecommendationsUseCase;
    private final RecordFeedbackUseCase recordFeedbackUseCase;

    public DefaultAgentCoordinator(
            IngestEventUseCase ingestEventUseCase,
            UpdateProfileUseCase updateProfileUseCase,
            RunDiscoveryUseCase runDiscoveryUseCase,
            EvaluateCandidatesUseCase evaluateCandidatesUseCase,
            GenerateRecommendationsUseCase generateRecommendationsUseCase,
            RecordFeedbackUseCase recordFeedbackUseCase
    ) {
        this.ingestEventUseCase = ingestEventUseCase;
        this.updateProfileUseCase = updateProfileUseCase;
        this.runDiscoveryUseCase = runDiscoveryUseCase;
        this.evaluateCandidatesUseCase = evaluateCandidatesUseCase;
        this.generateRecommendationsUseCase = generateRecommendationsUseCase;
        this.recordFeedbackUseCase = recordFeedbackUseCase;
    }

    @Override
    public ClosedLoopResult ingestAndRefresh(List<UserEventPayload> events) {
        var ingestResult = ingestEventUseCase.execute(new IngestEventCommand(events));
        var profileResult = updateProfileUseCase.execute(new UpdateProfileCommand(false));
        var discoveryResult = runDiscoveryUseCase.execute(new RunDiscoveryCommand(10, true));
        var evaluationResult = evaluateCandidatesUseCase.execute(new EvaluateCandidatesCommand(20));
        var recommendationResult = generateRecommendationsUseCase.execute(new GenerateRecommendationsCommand(10));
        return new ClosedLoopResult(
                ingestResult,
                profileResult,
                discoveryResult,
                evaluationResult,
                recommendationResult
        );
    }

    @Override
    public RecommendationBatch refreshRecommendations() {
        return generateRecommendationsUseCase.execute(new GenerateRecommendationsCommand(10)).batch();
    }

    @Override
    public FeedbackLoopResult recordFeedback(RecordFeedbackCommand command) {
        var feedbackResult = recordFeedbackUseCase.execute(command);
        var profileResult = updateProfileUseCase.execute(new UpdateProfileCommand(false));
        return new FeedbackLoopResult(feedbackResult, profileResult);
    }
}

