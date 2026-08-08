package com.openbiliclaw.domain.recommendation;

import com.openbiliclaw.domain.discovery.ContentId;
import com.openbiliclaw.domain.source.SourcePlatform;
import java.time.Instant;

public record Recommendation(
        RecommendationId id,
        ContentId contentId,
        SourcePlatform sourcePlatform,
        String title,
        String contentUrl,
        double score,
        RecommendationReason reason,
        RecommendationStatus status,
        Instant createdAt
) {
}

