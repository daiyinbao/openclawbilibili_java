package com.openbiliclaw.domain.event;

import java.time.Instant;
import java.util.List;

public interface EventRepository {
    EventId save(UserEvent event);

    List<EventId> saveAll(List<UserEvent> events);

    List<UserEvent> findRecent(int limit);

    List<UserEvent> findByTimeRange(Instant fromInclusive, Instant toExclusive);
}

