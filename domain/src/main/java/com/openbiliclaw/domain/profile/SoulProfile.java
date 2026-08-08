package com.openbiliclaw.domain.profile;

import java.time.Instant;
import java.util.List;

public record SoulProfile(
        ProfileId id,
        String portrait,
        List<String> coreTraits,
        List<String> deepNeeds,
        List<InterestTag> interests,
        List<InterestTag> dislikedTopics,
        Instant updatedAt
) {
}

