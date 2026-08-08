/**
 * file: DefaultUpdateProfileUseCase.java
 * author: daiyinbao
 * date: 2026-08-08
 */
package com.openbiliclaw.application.profile;

import java.util.Objects;

import com.openbiliclaw.domain.event.EventRepository;
import com.openbiliclaw.domain.profile.PreferenceAnalyzer;
import com.openbiliclaw.domain.profile.PreferenceProfile;
import com.openbiliclaw.domain.profile.PreferenceProfileRepository;
import com.openbiliclaw.domain.profile.SoulProfile;
import com.openbiliclaw.domain.profile.SoulProfileBuilder;
import com.openbiliclaw.domain.profile.SoulProfileRepository;

public class DefaultUpdateProfileUseCase implements UpdateProfileUseCase{

      private final EventRepository eventRepository;
      private final PreferenceAnalyzer preferenceAnalyzer;
      private final SoulProfileBuilder soulProfileBuilder;
      private final PreferenceProfileRepository preferenceProfileRepository;
      private final SoulProfileRepository soulProfileRepository;


      public DefaultUpdateProfileUseCase(
              EventRepository eventRepository,
              PreferenceAnalyzer preferenceAnalyzer,
              SoulProfileBuilder soulProfileBuilder,
              PreferenceProfileRepository preferenceProfileRepository,
              SoulProfileRepository soulProfileRepository
      ) {
          this.eventRepository = Objects.requireNonNull(eventRepository, "eventRepository must not be null");
          this.preferenceAnalyzer = Objects.requireNonNull(preferenceAnalyzer, "preferenceAnalyzer must not be null");
          this.soulProfileBuilder = Objects.requireNonNull(soulProfileBuilder, "soulProfileBuilder must not be null");
          this.preferenceProfileRepository = Objects.requireNonNull(preferenceProfileRepository, "preferenceProfileRepository must not be null");
          this.soulProfileRepository = Objects.requireNonNull(soulProfileRepository, "soulProfileRepository must not be null");
      }
    @Override
    public UpdateProfileResult execute(UpdateProfileCommand command) {
        
          Objects.requireNonNull(command, "command must not be null");

          // 第一版仍然按“读取最近事件 -> 分析 -> 构建画像 -> 落库”这条简单链路执行。
          // fullRebuild 参数先保留给后续扩展，当前不区分增量与全量。
          var recentEvents = eventRepository.findRecent(50);

          PreferenceProfile preferenceProfile = preferenceAnalyzer.analyze(recentEvents);
          SoulProfile soulProfile = soulProfileBuilder.build(preferenceProfile, recentEvents);

          preferenceProfileRepository.save(preferenceProfile);
          soulProfileRepository.save(soulProfile);

          return new UpdateProfileResult(
                  preferenceProfile,
                  soulProfile,
                  recentEvents.size()
          );
    }
    


}
