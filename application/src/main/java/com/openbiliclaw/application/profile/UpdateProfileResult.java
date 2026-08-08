package com.openbiliclaw.application.profile;

import com.openbiliclaw.domain.profile.PreferenceProfile;
import com.openbiliclaw.domain.profile.SoulProfile;

public record UpdateProfileResult(
        PreferenceProfile preferenceProfile,
        SoulProfile soulProfile,
        int consumedEventCount
) {
}

