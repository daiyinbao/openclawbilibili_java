package com.openbiliclaw.domain.recommendation;

import java.util.List;
import java.util.Optional;

public interface RecommendationRepository {
    RecommendationId save(Recommendation recommendation);

    List<RecommendationId> saveAll(List<Recommendation> recommendations);

    Optional<Recommendation> findById(RecommendationId id);

    List<Recommendation> findRecent(int limit);

    void update(Recommendation recommendation);
}

