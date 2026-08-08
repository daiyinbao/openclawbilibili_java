package com.openbiliclaw.application.feedback;

public interface RecordFeedbackUseCase {
    RecordFeedbackResult execute(RecordFeedbackCommand command);
}

