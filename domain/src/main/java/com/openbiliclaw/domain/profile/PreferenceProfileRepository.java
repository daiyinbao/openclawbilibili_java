package com.openbiliclaw.domain.profile;

import java.util.Optional;

public interface PreferenceProfileRepository {
    Optional<PreferenceProfile> loadCurrent();

    void save(PreferenceProfile profile);
}

