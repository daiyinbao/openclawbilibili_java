/**
 * file: StubUpdateProfileUseCase.java
 * author: daiyinbao
 * date: 2026-08-04
 */
package com.openbiliclaw.application.profile;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import com.openbiliclaw.domain.event.EventRepository;
import com.openbiliclaw.domain.profile.InterestTag;
import com.openbiliclaw.domain.profile.PreferenceProfile;
import com.openbiliclaw.domain.profile.PreferenceProfileRepository;
import com.openbiliclaw.domain.profile.ProfileId;
import com.openbiliclaw.domain.profile.SoulProfile;
import com.openbiliclaw.domain.profile.SoulProfileRepository;

public class StubUpdateProfileUseCase implements UpdateProfileUseCase{

    
      private static final ProfileId DEFAULT_PROFILE_ID = new ProfileId(1L);

      private final EventRepository eventRepository;
      private final PreferenceProfileRepository preferenceProfileRepository;
      private final SoulProfileRepository soulProfileRepository;

      public StubUpdateProfileUseCase(
              EventRepository eventRepository,
              PreferenceProfileRepository preferenceProfileRepository,
              SoulProfileRepository soulProfileRepository
      ) {
          this.eventRepository = eventRepository;
          this.preferenceProfileRepository = preferenceProfileRepository;
          this.soulProfileRepository = soulProfileRepository;
      }

      @Override
      public UpdateProfileResult execute(UpdateProfileCommand command) {
          var recentEvents = eventRepository.findRecent(50);
          Instant now = Instant.now();

          PreferenceProfile preferenceProfile = new PreferenceProfile(
                  DEFAULT_PROFILE_ID,
                  List.of(
                          new InterestTag("architecture", "technology", 0.90),
                          new InterestTag("system design", "technology", 0.85)
                  ),
                  List.of(),
                  Map.of(
                          "depthPreference", 0.90,
                          "noveltyPreference", 0.55
                  ),
                  now
          );

          SoulProfile soulProfile = new SoulProfile(
                  DEFAULT_PROFILE_ID,
                  recentEvents.isEmpty()
                          ? "No events yet. Profile has not been established."
                          : "A user who appears to prefer structured, technical, and explanatory content.",
                  List.of(
                          "structured thinker",
                          "detail oriented"
                  ),
                  List.of(
                          "wants clarity",
                          "prefers deep explanation"
                  ),
                  preferenceProfile.interests(),
                  preferenceProfile.dislikedTopics(),
                  now
          );

          // 先落 profile，再返回结果。
          // 当前仍是 stub 分析，但持久化链路已经是真实链路。
          preferenceProfileRepository.save(preferenceProfile);
          soulProfileRepository.save(soulProfile);

          return new UpdateProfileResult(
                  preferenceProfile,
                  soulProfile,
                  recentEvents.size()
          );
      }
    
    


}
