package com.openbiliclaw.domain.discovery;

import java.time.Instant;

public record CandidateContent(
        CandidateId candidateId,
        DiscoveredContent content,
        CandidateStatus status,
        Double relevanceScore,
        String relevanceReason,
        Instant discoveredAt,
        Instant updatedAt
) {
}

