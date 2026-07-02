package com.doodle.repo;

import com.doodle.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingRepository
        extends JpaRepository<Meeting, Long> {
}
