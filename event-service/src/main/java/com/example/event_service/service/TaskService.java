package com.example.event_service.service;

import com.example.event_service.dto.request.TaskAssignRequest;
import com.example.event_service.dto.request.TaskCreateRequest;
import com.example.event_service.dto.request.TaskStatusUpdateRequest;
import com.example.event_service.dto.request.TaskUpdateRequest;
import com.example.event_service.dto.response.TaskResponse;

import java.util.List;

public interface TaskService {
    TaskResponse createTask(String eventId, TaskCreateRequest request, Integer requesterId);

    List<TaskResponse> getEventTasks(String eventId, Integer requesterId);

    TaskResponse updateTask(String eventId, Long taskId, TaskUpdateRequest request, Integer requesterId);

    TaskResponse assignTask(String eventId, Long taskId, TaskAssignRequest request, Integer requesterId);

    TaskResponse updateTaskStatus(String eventId, Long taskId, TaskStatusUpdateRequest request, Integer requesterId);

    TaskResponse acceptTask(String eventId, Long taskId, Integer requesterId);

    void deleteTask(String eventId, Long taskId, Integer requesterId);
}
