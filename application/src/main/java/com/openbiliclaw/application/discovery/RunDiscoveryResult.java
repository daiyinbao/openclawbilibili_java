package com.openbiliclaw.application.discovery;

import com.openbiliclaw.domain.discovery.CandidateId;
import java.util.List;

public record RunDiscoveryResult(List<CandidateId> createdCandidateIds, int discoveredCount) {
}

