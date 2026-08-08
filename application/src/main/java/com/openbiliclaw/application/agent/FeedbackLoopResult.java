package com.openbiliclaw.application.agent;

import com.openbiliclaw.application.feedback.RecordFeedbackResult;
import com.openbiliclaw.application.profile.UpdateProfileResult;

public record FeedbackLoopResult(
        RecordFeedbackResult recordFeedbackResult,
        UpdateProfileResult updateProfileResult
) {
}

