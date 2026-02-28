package com.example.event_service.service.impl;

import com.example.event_service.dto.request.TaskAssignRequest;
import com.example.event_service.dto.request.TaskCreateRequest;
import com.example.event_service.dto.request.TaskStatusUpdateRequest;
import com.example.event_service.dto.request.TaskUpdateRequest;
import com.example.event_service.dto.response.TaskResponse;
import com.example.event_service.entity.Event;
import com.example.event_service.entity.Task;
import com.example.event_service.enums.EventStatus;
import com.example.event_service.enums.TaskStatus;
import com.example.event_service.client.MemberClient;
import com.example.event_service.repository.EventRepository;
import com.example.event_service.repository.TaskRepository;
import com.example.event_service.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final EventRepository eventRepository;
    private final MemberClient memberClient;

    @Override
    @Transactional
    public TaskResponse createTask(String eventId, TaskCreateRequest request, Integer requesterId) {
        Event event = checkHasEditorOrOwnerPermission(eventId, requesterId);

        validateAssignee(eventId, request.getAssigneeId(), event);

        Task task = Task.builder()
                .event(event)
                .title(request.getTitle())
                .description(request.getDescription())
                .assigneeId(request.getAssigneeId())
                .dueDate(request.getDueDate())
                .status(request.getAssigneeId() != null ? TaskStatus.PENDING : TaskStatus.OPEN)
                .isLate(false)
                .build();

        task = taskRepository.save(task);
        return mapToResponse(task);
    }

    @Override
    public List<TaskResponse> getEventTasks(String eventId, Integer requesterId) {
        checkHasMemberPermission(eventId, requesterId);

        List<Task> tasks = taskRepository.findByEventId(eventId);
        return tasks.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TaskResponse updateTask(String eventId, Long taskId, TaskUpdateRequest request, Integer requesterId) {
        // Permissions: OWNER, EDITOR
        Event event = checkHasEditorOrOwnerPermission(eventId, requesterId);

        // Rule: Return error if event has expired (Assuming status closed or past end
        // time)
        if (event.getStatus() == EventStatus.COMPLETED || event.getStatus() == EventStatus.CANCELLED
                || (event.getEndTime() != null && Instant.now().isAfter(event.getEndTime()))) {
            throw new RuntimeException("Action blocked: Event has already expired");
        }

        Task task = taskRepository.findByIdAndEventId(taskId, eventId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());

        task = taskRepository.save(task);
        return mapToResponse(task);
    }

    @Override
    @Transactional
    public TaskResponse assignTask(String eventId, Long taskId, TaskAssignRequest request, Integer requesterId) {
        // Permissions: OWNER, EDITOR
        checkHasEditorOrOwnerPermission(eventId, requesterId);

        Task task = taskRepository.findByIdAndEventId(taskId, eventId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        validateAssignee(eventId, request.getAssigneeId(), task.getEvent());

        task.setAssigneeId(request.getAssigneeId());
        task.setStatus(request.getAssigneeId() != null ? TaskStatus.PENDING : TaskStatus.OPEN);
        task = taskRepository.save(task);
        return mapToResponse(task);
    }

    @Override
    @Transactional
    public TaskResponse updateTaskStatus(String eventId, Long taskId, TaskStatusUpdateRequest request,
            Integer requesterId) {
        // Permissions: OWNER, EDITOR, or specific Assignee
        Task task = taskRepository.findByIdAndEventId(taskId, eventId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        Event event = task.getEvent();

        boolean isAssignee = requesterId.equals(task.getAssigneeId());

        if (!isAssignee) {
            // Check if OWNER or EDITOR
            if (!event.getOwnerId().equals(requesterId)) {
                boolean isEditor = memberClient.checkMemberRole(eventId, requesterId, "EDITOR");
                if (!isEditor) {
                    throw new RuntimeException("Access denied: Requires EDITOR, OWNER role or must be the assignee");
                }
            }
        }

        // Check if late (Optional based on business rule, it says this is allowed even
        // if event has expired)
        if (request.getStatus() == TaskStatus.FINISHED) {
            if (task.getDueDate() != null && Instant.now().isAfter(task.getDueDate())) {
                task.setIsLate(true);
            }
        }

        task.setStatus(request.getStatus());
        task = taskRepository.save(task);
        return mapToResponse(task);
    }

    @Override
    @Transactional
    public void deleteTask(String eventId, Long taskId, Integer requesterId) {
        // Permissions: OWNER, EDITOR
        checkHasEditorOrOwnerPermission(eventId, requesterId);

        Task task = taskRepository.findByIdAndEventId(taskId, eventId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        taskRepository.delete(task);
    }

    @Override
    @Transactional
    public TaskResponse acceptTask(String eventId, Long taskId, Integer requesterId) {
        Task task = taskRepository.findByIdAndEventId(taskId, eventId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!requesterId.equals(task.getAssigneeId())) {
            throw new RuntimeException("Access denied: Only the assigned user can accept this task");
        }

        if (task.getStatus() != TaskStatus.PENDING) {
            throw new RuntimeException("Action blocked: Task is not in PENDING status");
        }

        task.setStatus(TaskStatus.PROCESSING);
        task = taskRepository.save(task);
        return mapToResponse(task);
    }

    private void validateAssignee(String eventId, Integer assigneeId, Event event) {
        if (assigneeId != null) {
            if (event.getOwnerId().equals(assigneeId)) {
                return;
            }
            boolean isEditor = memberClient.checkMemberRole(eventId, assigneeId, "EDITOR");
            if (!isEditor) {
                throw new RuntimeException("Assignee must be an OWNER or EDITOR of the event");
            }
        }
    }

    private Event checkHasEditorOrOwnerPermission(String eventId, Integer userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (!event.getOwnerId().equals(userId)) {
            boolean isEditor = memberClient.checkMemberRole(eventId, userId, "EDITOR");
            if (!isEditor) {
                throw new RuntimeException("Access denied: Requires EDITOR or OWNER role");
            }
        }
        return event;
    }

    private void checkHasMemberPermission(String eventId, Integer userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (!event.getOwnerId().equals(userId)) {
            boolean isMember = memberClient.isMember(eventId, userId);
            if (!isMember) {
                throw new RuntimeException("Access denied: Not a member of this event");
            }
        }
    }

    private TaskResponse mapToResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .eventId(task.getEvent().getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .assigneeId(task.getAssigneeId())
                .dueDate(task.getDueDate())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .isLate(task.getIsLate())
                .build();
    }
}
