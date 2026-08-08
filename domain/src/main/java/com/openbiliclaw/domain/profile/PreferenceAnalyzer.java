package com.openbiliclaw.domain.profile;

import com.openbiliclaw.domain.event.UserEvent;
import java.util.List;

public interface PreferenceAnalyzer {
    PreferenceProfile analyze(List<UserEvent> events);
}

