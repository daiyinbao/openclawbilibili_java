package com.openbiliclaw.domain.discovery;

import java.util.List;

public interface CandidateRepository {
    CandidateId save(CandidateContent candidate);

    List<CandidateId> saveAll(List<CandidateContent> candidates);

    List<CandidateContent> findPending(int limit);

    List<CandidateContent> findAccepted(int limit);

    void update(CandidateContent candidate);
}

