package com.doodle.repo;

import com.doodle.entity.Slot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;

public interface SlotRepository extends JpaRepository<Slot,Long> {
    List<Slot> findByUserId(Long userId);
}
