package com.openbiliclaw.domain.profile;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record PreferenceProfile(
        ProfileId id,
        List<InterestTag> interests,
        List<InterestTag> dislikedTopics,
        Map<String, Double> styleSignals,
        Instant updatedAt
) {
}

