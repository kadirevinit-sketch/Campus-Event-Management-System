package com.campus.event.repository;

import com.campus.event.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByOrderByEventDateDesc();

    List<Event> findByStatusOrderByEventDateAsc(String status);

    List<Event> findByStatusAndEventDateGreaterThanEqualOrderByEventDateAsc(String status, LocalDate date);

    @Query("SELECT e FROM Event e WHERE " +
           "(:status IS NULL OR e.status = :status) AND " +
           "(:category IS NULL OR :category = '' OR LOWER(e.category) = LOWER(:category)) AND " +
           "(:keyword IS NULL OR :keyword = '' OR LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           " OR LOWER(e.venue) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           " OR LOWER(e.organizer) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY e.eventDate ASC, e.startTime ASC")
    List<Event> searchEvents(@Param("status") String status,
                             @Param("category") String category,
                             @Param("keyword") String keyword);

    @Query("SELECT e FROM Event e WHERE " +
           "(:category IS NULL OR :category = '' OR LOWER(e.category) = LOWER(:category)) AND " +
           "(:status IS NULL OR :status = '' OR LOWER(e.status) = LOWER(:status)) AND " +
           "(:keyword IS NULL OR :keyword = '' OR LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           " OR LOWER(e.venue) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           " OR LOWER(e.organizer) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY e.eventDate DESC")
    List<Event> searchAllEventsForAdmin(@Param("category") String category,
                                        @Param("status") String status,
                                        @Param("keyword") String keyword);

    long countByStatus(String status);

    long countByEventDateGreaterThanEqual(LocalDate date);

    long countByEventDateBefore(LocalDate date);
}
