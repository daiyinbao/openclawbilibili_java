/**
 * file: DefaultIngestEventUseCase.java
 * author: daiyinbao
 * date: 2026-08-04
 */
package com.openbiliclaw.application.event;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.openbiliclaw.domain.event.EventId;
import com.openbiliclaw.domain.event.EventRepository;
import com.openbiliclaw.domain.event.UserEvent;

public class DefaultIngestEventUseCase implements IngestEventUseCase{

    private final EventRepository eventRepository;
    public DefaultIngestEventUseCase(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }
    
    @Override
    public IngestEventResult execute(IngestEventCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        Objects.requireNonNull(command.events(), "event must not be null");

        if(command.events().isEmpty()){
            throw new IllegalArgumentException("event must not be empty ");
        }
        List<UserEvent> eventToPersist = new ArrayList<>(command.events().size());
        for (UserEventPayload payload : command.events()) {
            eventToPersist.add(toDomainEvent(payload));
        }
        List<EventId> savedIds = eventRepository.saveAll(eventToPersist);
        return new IngestEventResult(
                  savedIds,
                  savedIds.size(),
                  0
          );
    }
    

    private UserEvent toDomainEvent(UserEventPayload payload) {
          Objects.requireNonNull(payload, "event payload must not be null");

          if (payload.eventType() == null) {
              throw new IllegalArgumentException("eventType must not be null");
          }
          if (payload.occurredAt() == null) {
              throw new IllegalArgumentException("occurredAt must not be null");
          }
          if (payload.sourcePlatform() == null) {
              throw new IllegalArgumentException("sourcePlatform must not be null");
          }

          return new UserEvent(
                  null,
                  payload.eventType(),
                  normalizeNullableText(payload.title()),
                  normalizeNullableText(payload.url()),
                  payload.occurredAt(),
                  payload.sourcePlatform(),
                  safeMetadata(payload.metadata())
          );
      }
    
    private String normalizeNullableText(String value) {
          if (value == null) {
              return null;
          }
          String normalized = value.trim();
          return normalized.isEmpty() ? null : normalized;
      }

      private Map<String, String> safeMetadata(Map<String, String> metadata) {
          return metadata == null ? Map.of() : Map.copyOf(metadata);
      }

}
