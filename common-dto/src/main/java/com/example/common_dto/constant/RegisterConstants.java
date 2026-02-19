package com.example.common_dto.constant;

public class RegisterConstants {

    // =========================================================
    // 1. ROUTING KEYS (Khóa định tuyến - Dùng trong convertAndSend)
    // Quy tắc: <đối tượng>.<hành động>.<event/command>
    // =========================================================

    // -- EVENTS (Sự kiện đã xảy ra trong quá khứ) --
    // Auth Service thông báo: "Có user mới vừa đăng ký"
    public static final String ROUTING_KEY_AUTH_USER_REGISTERED = "auth.user.registered.event";

    // Profile Service thông báo: "Đã tạo profile thành công"
    public static final String ROUTING_KEY_PROFILE_CREATED = "profile.created.event";

    // Profile Service thông báo: "Tạo profile THẤT BẠI"
    public static final String ROUTING_KEY_PROFILE_FAILED = "profile.failed.event";

    // -- COMMANDS (Mệnh lệnh yêu cầu thực thi) --
    // Orchestrator ra lệnh: "Profile Service, hãy tạo profile đi"
    public static final String ROUTING_KEY_PROFILE_CREATE = "profile.create.command";

    // Orchestrator ra lệnh: "User Service, hãy kích hoạt user này đi"
    public static final String ROUTING_KEY_USER_ACTIVATE = "user.activate.command";

    // Orchestrator ra lệnh: "Auth Service, hãy rollback (xóa/disable) user này đi"
    public static final String ROUTING_KEY_AUTH_ROLLBACK = "auth.rollback.command";

    // =========================================================
    // 2. QUEUES (Hàng đợi - Dùng trong @RabbitListener)
    // Quy tắc: <tên-service-lắng-nghe>.<mục-đích>.<queue>
    // =========================================================

    // -- ORCHESTRATOR LẮNG NGHE --
    // Nghe Auth báo đăng ký thành công
    public static final String QUEUE_ORCHESTRATOR_USER_REGISTERED = "orchestrator.user.registered.queue";
    // Nghe Profile báo tạo thành công
    public static final String QUEUE_ORCHESTRATOR_PROFILE_CREATED = "orchestrator.profile.created.queue";
    // Nghe Profile báo tạo thất bại
    public static final String QUEUE_ORCHESTRATOR_PROFILE_FAILED = "orchestrator.profile.failed.queue";

    // -- CÁC SERVICE KHÁC LẮNG NGHE LỆNH TỪ ORCHESTRATOR --
    // Profile Service nghe lệnh tạo profile
    public static final String QUEUE_PROFILE_CREATE = "profile-service.profile.create.queue";

    // User Service nghe lệnh kích hoạt user
    public static final String QUEUE_USER_ACTIVATE = "user-service.user.activate.queue";

    // Auth Service nghe lệnh rollback
    public static final String QUEUE_AUTH_ROLLBACK = "auth-service.auth.rollback.queue";

}
