package com.doodle.repo;

import com.doodle.entity.Slot;
import com.doodle.entity.SlotStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface SlotRepository extends JpaRepository<Slot, Long> {

    List<Slot> findByUserId(Long userId);

    List<Slot> findByUserIdAndStatus(Long userId, SlotStatus status);

    List<Slot> findByUserIdAndStartTimeBetween(
            Long userId,
            LocalDateTime from,
            LocalDateTime to
    );

    boolean existsByUserIdAndIdNotAndStartTimeLessThanAndEndTimeGreaterThan(
            Long userId,
            Long slotId,
            LocalDateTime end,
            LocalDateTime start
    );

    List<Slot> findByUserIdAndStatusAndStartTimeAndEndTime(
            Long userId,
            SlotStatus status,
            LocalDateTime start,
            LocalDateTime end
    );

    @Query("""
       SELECT s
       FROM Slot s
       WHERE s.user.id = :userId
       AND s.startTime < :to
       AND s.endTime > :from
       ORDER BY s.startTime
       """)
    List<Slot> findCalendarSlots(
            Long userId,
            LocalDateTime from,
            LocalDateTime to
    );

}