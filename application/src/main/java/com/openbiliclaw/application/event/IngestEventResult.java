package com.openbiliclaw.application.event;

import com.openbiliclaw.domain.event.EventId;
import java.util.List;

public record IngestEventResult(
        List<EventId> savedEventIds,
        int acceptedCount,
        int rejectedCount
) {
}

