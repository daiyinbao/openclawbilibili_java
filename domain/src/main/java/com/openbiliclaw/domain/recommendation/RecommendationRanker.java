package com.openbiliclaw.domain.recommendation;

import com.openbiliclaw.domain.discovery.CandidateContent;
import com.openbiliclaw.domain.profile.SoulProfile;
import java.util.List;

public interface RecommendationRanker {
    List<CandidateContent> rank(List<CandidateContent> acceptedCandidates, SoulProfile profile);
}

