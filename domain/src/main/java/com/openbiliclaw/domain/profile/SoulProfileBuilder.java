package com.openbiliclaw.domain.profile;

import com.openbiliclaw.domain.event.UserEvent;
import java.util.List;

public interface SoulProfileBuilder {
    SoulProfile build(PreferenceProfile preferenceProfile, List<UserEvent> recentEvents);
}

