package com.example.common_dto.constant;

public class RabbitMQConstants {
    public static final String SAGA_EXCHANGE = "saga-exchange";

    public static final String USER_ACTIVATE_QUEUE = "user.activate.queue";
    public static final String AUTH_ROLLBACK_QUEUE = "auth.rollback.queue";

    public static final String AUTH_USER_REGISTERED = "auth.user.registered";

    public static final String ORCHESTRATOR_USER_ACTIVATE = "orchestrator.user.activate";

    public static final String ORCHESTRATOR_AUTH_ROLLBACK = "orchestrator.auth.rollback";

    public static final String PROFILE_CREATE_QUEUE = "profile.create.queue";

    public static final String ORCHESTRATOR_PROFILE_CREATE = "orchestrator.profile.create";

    public static final String USER_PROFILE_CREATED = "user.profile.created";

    public static final String USER_PROFILE_FAILED = "user.profile.failed";

    public static final String AUTH_REGISTERED_QUEUE = "auth.registered.queue";

    public static final String PROFILE_CREATED_QUEUE = "profile.created.queue";

    public static final String PROFILE_FAILED_QUEUE = "profile.failed.queue";

    // Email Update Saga
    public static final String EMAIL_UPDATE_REQUESTED = "user.email.update.requested";
    public static final String ORCHESTRATOR_AUTH_EMAIL_UPDATE = "orchestrator.auth.email.update";
    public static final String AUTH_EMAIL_UPDATED = "auth.email.updated";
    public static final String AUTH_EMAIL_UPDATE_FAILED = "auth.email.update.failed";
    public static final String ORCHESTRATOR_USER_CONFIRM_EMAIL = "orchestrator.user.confirm.email";
    public static final String ORCHESTRATOR_USER_DISCARD_EMAIL = "orchestrator.user.discard.email";

    public static final String EMAIL_UPDATE_REQUESTED_QUEUE = "email.update.requested.queue";
    public static final String UPDATE_AUTH_EMAIL_QUEUE = "update.auth.email.queue";
    public static final String AUTH_EMAIL_UPDATED_QUEUE = "auth.email.updated.queue";
    public static final String AUTH_EMAIL_UPDATE_FAILED_QUEUE = "auth.email.update.failed.queue";
    public static final String CONFIRM_EMAIL_UPDATE_QUEUE = "confirm.email.update.queue";
    public static final String DISCARD_EMAIL_UPDATE_QUEUE = "discard.email.update.queue";
}