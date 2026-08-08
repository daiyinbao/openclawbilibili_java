package com.openbiliclaw.application.event;

public interface IngestEventUseCase {
    IngestEventResult execute(IngestEventCommand command);
}

