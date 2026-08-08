package com.openbiliclaw.domain.profile;

import java.util.Optional;

public interface SoulProfileRepository {
    Optional<SoulProfile> loadCurrent();

    void save(SoulProfile profile);
}

