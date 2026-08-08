package com.openbiliclaw.domain.discovery;

import com.openbiliclaw.domain.profile.SoulProfile;
import java.util.List;

public interface DiscoveryStrategy {
    DiscoveryStrategyType type();

    List<DiscoveredContent> discover(SoulProfile profile);
}

