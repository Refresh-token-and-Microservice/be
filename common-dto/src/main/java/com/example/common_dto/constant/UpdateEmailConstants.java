package com.example.common_dto.constant;

public class UpdateEmailConstants {

    // User Service báo cáo: "Có người dùng muốn đổi email" (Event)
    public static final String EVENT_EMAIL_UPDATE_REQUESTED = "email.update-requested.event";

    // Orchestrator ra lệnh: "Auth Service, đổi email đi" (Command)
    public static final String COMMAND_AUTH_EMAIL_UPDATE = "auth.email.update.command";

    // Auth Service báo cáo: "Tôi đã đổi email thành công" (Event)
    public static final String EVENT_AUTH_EMAIL_UPDATED = "auth.email.updated.event";

    // Auth Service báo cáo: "Tôi đổi email THẤT BẠI rồi" (Event) - Bổ sung cho đủ
    // luồng
    public static final String EVENT_AUTH_EMAIL_FAILED = "auth.email.failed.event";

    // Orchestrator ra lệnh: "User Service, chốt đổi email đi" (Command)
    public static final String COMMAND_USER_EMAIL_CONFIRM = "user.email.confirm.command";

    // Orchestrator ra lệnh: "User Service, huỷ lệnh đổi email đi" (Command) - Bổ
    // sung
    public static final String COMMAND_USER_EMAIL_DISCARD = "user.email.discard.command";

    // =========================================================
    // 2. QUEUES (Hàng đợi - Dùng trong @RabbitListener)
    // Quy tắc: <tên-service-lắng-nghe>.<mục-đích>.<queue>
    // =========================================================

    // Hàng đợi Orchestrator nghe yêu cầu từ User Service
    public static final String QUEUE_ORCHESTRATOR_EMAIL_UPDATE_REQUESTED = "orchestrator.email.update-requested.queue";

    // Hàng đợi Auth Service nghe lệnh cập nhật từ Orchestrator
    public static final String QUEUE_AUTH_EMAIL_UPDATE = "auth-service.email.update.queue";

    // Hàng đợi Orchestrator nghe kết quả THÀNH CÔNG từ Auth Service
    public static final String QUEUE_ORCHESTRATOR_AUTH_EMAIL_UPDATED = "orchestrator.auth-email.updated.queue";

    // Hàng đợi Orchestrator nghe kết quả THẤT BẠI từ Auth Service - Bổ sung
    public static final String QUEUE_ORCHESTRATOR_AUTH_EMAIL_FAILED = "orchestrator.auth-email.failed.queue";

    // Hàng đợi User Service nghe lệnh CONFIRM từ Orchestrator - Bổ sung
    public static final String QUEUE_USER_EMAIL_CONFIRM = "user-service.email.confirm.queue";

    // Hàng đợi User Service nghe lệnh DISCARD từ Orchestrator - Bổ sung
    public static final String QUEUE_USER_EMAIL_DISCARD = "user-service.email.discard.queue";

}