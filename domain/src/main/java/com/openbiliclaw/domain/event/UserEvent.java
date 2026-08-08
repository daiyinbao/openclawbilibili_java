package com.openbiliclaw.domain.event;

import com.openbiliclaw.domain.source.SourcePlatform;
import java.time.Instant;
import java.util.Map;

public record UserEvent(
        EventId id,
        EventType eventType,
        String title,
        String url,
        Instant occurredAt,
        SourcePlatform sourcePlatform,
        Map<String, String> metadata
) {
}

