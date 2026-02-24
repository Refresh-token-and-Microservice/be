package com.example.event_service.repository;

import com.example.event_service.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, String> {

    @Query("SELECT e FROM Event e WHERE e.ownerId = :userId OR e.id IN (SELECT em.event.id FROM EventMember em WHERE em.userId = :userId)")
    List<Event> findMyEvents(@Param("userId") Integer userId);

}
