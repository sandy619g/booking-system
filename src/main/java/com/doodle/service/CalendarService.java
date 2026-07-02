package com.doodle.service;

import com.doodle.dto.response.CalendarResponse;
import com.doodle.entity.Slot;
import com.doodle.repo.SlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarService {

    private final SlotRepository slotRepository;

    public List<CalendarResponse> getCalendar(
            Long userId,
            LocalDateTime from,
            LocalDateTime to) {

        return slotRepository.findCalendarSlots(
                        userId,
                        from,
                        to)
                .stream()
                .map(this::map)
                .toList();
    }

    private CalendarResponse map(Slot slot) {

        return CalendarResponse.builder()
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .status(slot.getStatus())
                .meetingId(
                        slot.getMeeting() != null
                                ? slot.getMeeting().getId()
                                : null)
                .meetingTitle(
                        slot.getMeeting() != null
                                ? slot.getMeeting().getTitle()
                                : null)
                .build();
    }
}
