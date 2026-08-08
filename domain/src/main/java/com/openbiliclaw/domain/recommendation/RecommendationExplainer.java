package com.openbiliclaw.domain.recommendation;

import com.openbiliclaw.domain.discovery.CandidateContent;
import com.openbiliclaw.domain.profile.SoulProfile;

public interface RecommendationExplainer {
    RecommendationReason explain(CandidateContent candidate, SoulProfile profile);
}

