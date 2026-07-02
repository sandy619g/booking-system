package com.doodle.repo;

import com.doodle.entity.Slot;
import com.doodle.entity.SlotStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    select s
    from Slot s
    where s.id = :id
    """)
    Optional<Slot> findByIdForUpdate(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    select s
    from Slot s
    where s.user.id = :userId
      and s.status = :status
      and s.startTime = :start
      and s.endTime = :end
    """)
    List<Slot> findAvailableSlotsForUpdate(
            @Param("userId") Long userId,
            @Param("status") SlotStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);


}