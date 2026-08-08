package com.openbiliclaw.application.event;

import java.util.List;

public record IngestEventCommand(List<UserEventPayload> events) {
}

