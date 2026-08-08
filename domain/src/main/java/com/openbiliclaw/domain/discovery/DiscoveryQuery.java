package com.openbiliclaw.domain.discovery;

import com.openbiliclaw.domain.source.SourcePlatform;

public record DiscoveryQuery(
        String keyword,
        SourcePlatform sourcePlatform,
        DiscoveryStrategyType strategyType
) {
}

