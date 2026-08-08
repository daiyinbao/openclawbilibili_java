package com.openbiliclaw.domain.recommendation;

import java.time.Instant;
import java.util.List;

public record RecommendationBatch(List<Recommendation> items, Instant generatedAt) {
}

