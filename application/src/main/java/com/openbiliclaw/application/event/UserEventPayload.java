package com.openbiliclaw.application.event;

import com.openbiliclaw.domain.event.EventType;
import com.openbiliclaw.domain.source.SourcePlatform;
import java.time.Instant;
import java.util.Map;

public record UserEventPayload(
        EventType eventType,
        String title,
        String url,
        Instant occurredAt,
        SourcePlatform sourcePlatform,
        Map<String, String> metadata
) {
}

