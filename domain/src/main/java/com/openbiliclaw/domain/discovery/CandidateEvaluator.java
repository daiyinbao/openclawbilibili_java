package com.openbiliclaw.domain.discovery;

import com.openbiliclaw.domain.profile.SoulProfile;

public interface CandidateEvaluator {
    CandidateContent evaluate(CandidateContent candidate, SoulProfile profile);
}

