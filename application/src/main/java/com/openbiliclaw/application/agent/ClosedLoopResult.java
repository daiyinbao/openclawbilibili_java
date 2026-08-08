package com.openbiliclaw.application.agent;

import com.openbiliclaw.application.discovery.EvaluateCandidatesResult;
import com.openbiliclaw.application.discovery.RunDiscoveryResult;
import com.openbiliclaw.application.event.IngestEventResult;
import com.openbiliclaw.application.profile.UpdateProfileResult;
import com.openbiliclaw.application.recommendation.GenerateRecommendationsResult;

public record ClosedLoopResult(
        IngestEventResult ingestEventResult,
        UpdateProfileResult updateProfileResult,
        RunDiscoveryResult runDiscoveryResult,
        EvaluateCandidatesResult evaluateCandidatesResult,
        GenerateRecommendationsResult generateRecommendationsResult
) {
}

