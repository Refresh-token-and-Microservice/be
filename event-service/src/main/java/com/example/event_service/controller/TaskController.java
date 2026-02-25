package com.example.event_service.controller;

import com.example.event_service.dto.request.TaskAssignRequest;
import com.example.event_service.dto.request.TaskCreateRequest;
import com.example.event_service.dto.request.TaskStatusUpdateRequest;
import com.example.event_service.dto.request.TaskUpdateRequest;
import com.example.event_service.dto.response.TaskResponse;
import com.example.event_service.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events/{eventId}/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @PathVariable String eventId,
            @RequestBody TaskCreateRequest request) {
        return ResponseEntity.ok(taskService.createTask(eventId, request, getUserId()));
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getEventTasks(@PathVariable String eventId) {
        return ResponseEntity.ok(taskService.getEventTasks(eventId, getUserId()));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable String eventId,
            @PathVariable Long taskId,
            @RequestBody TaskUpdateRequest request) {
        return ResponseEntity.ok(taskService.updateTask(eventId, taskId, request, getUserId()));
    }

    @PatchMapping("/{taskId}/assign")
    public ResponseEntity<TaskResponse> assignTask(
            @PathVariable String eventId,
            @PathVariable Long taskId,
            @RequestBody TaskAssignRequest request) {
        return ResponseEntity.ok(taskService.assignTask(eventId, taskId, request, getUserId()));
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(
            @PathVariable String eventId,
            @PathVariable Long taskId,
            @RequestBody TaskStatusUpdateRequest request) {
        return ResponseEntity.ok(taskService.updateTaskStatus(eventId, taskId, request, getUserId()));
    }

    @PatchMapping("/{taskId}/accept")
    public ResponseEntity<TaskResponse> acceptTask(
            @PathVariable String eventId,
            @PathVariable Long taskId) {
        return ResponseEntity.ok(taskService.acceptTask(eventId, taskId, getUserId()));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable String eventId,
            @PathVariable Long taskId) {
        taskService.deleteTask(eventId, taskId, getUserId());
        return ResponseEntity.noContent().build();
    }

    private Integer getUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Integer.parseInt(principal.toString());
    }
}
