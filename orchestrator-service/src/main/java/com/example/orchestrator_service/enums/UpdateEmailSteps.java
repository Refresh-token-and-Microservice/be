package com.example.orchestrator_service.enums;

public enum UpdateEmailSteps {
    EMAIL_UPDATE_REQUESTED,
    UPDATE_AUTH_EMAIL,
    AUTH_EMAIL_UPDATED,
    AUTH_EMAIL_UPDATE_FAILED,
    CONFIRM_EMAIL_UPDATE,
    DISCARD_EMAIL_UPDATE,
    COMPLETED
}
