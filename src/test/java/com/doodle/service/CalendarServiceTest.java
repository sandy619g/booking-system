package com.doodle.service;

import com.doodle.dto.response.CalendarResponse;
import com.doodle.entity.Meeting;
import com.doodle.entity.Slot;
import com.doodle.entity.SlotStatus;
import com.doodle.repo.SlotRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalendarServiceTest {

    @Mock
    private SlotRepository slotRepository;

    @InjectMocks
    private CalendarService calendarService;

    private Slot slotWithMeeting(Long meetingId) {

        Meeting meeting = Meeting.builder()
                .id(meetingId)
                .title("Sprint Planning")
                .build();

        Slot slot = Slot.builder()
                .id(1L)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(1))
                .status(SlotStatus.BUSY)
                .meeting(meeting)
                .build();

        return slot;
    }

    @Test
    void shouldReturnCalendarWithMeetingData() {

        Slot slot = slotWithMeeting(100L);

        when(slotRepository.findCalendarSlots(
                anyLong(),
                any(),
                any()
        )).thenReturn(List.of(slot));

        List<CalendarResponse> result =
                calendarService.getCalendar(
                        1L,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusHours(1)
                );

        assertEquals(1, result.size());

        CalendarResponse response = result.get(0);

        assertEquals(SlotStatus.BUSY, response.getStatus());
        assertEquals(100L, response.getMeetingId());
        assertEquals("Sprint Planning", response.getMeetingTitle());

        verify(slotRepository, times(1))
                .findCalendarSlots(anyLong(), any(), any());
    }

    @Test
    void shouldHandleSlotWithoutMeeting() {

        Slot slot = Slot.builder()
                .id(1L)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(1))
                .status(SlotStatus.FREE)
                .meeting(null)
                .build();

        when(slotRepository.findCalendarSlots(
                anyLong(),
                any(),
                any()
        )).thenReturn(List.of(slot));

        List<CalendarResponse> result =
                calendarService.getCalendar(
                        1L,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusHours(1)
                );

        assertEquals(1, result.size());

        CalendarResponse response = result.get(0);

        assertEquals(SlotStatus.FREE, response.getStatus());
        assertNull(response.getMeetingId());
        assertNull(response.getMeetingTitle());
    }

    @Test
    void shouldReturnEmptyCalendar() {

        when(slotRepository.findCalendarSlots(
                anyLong(),
                any(),
                any()
        )).thenReturn(List.of());

        List<CalendarResponse> result =
                calendarService.getCalendar(
                        1L,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusHours(1)
                );

        assertTrue(result.isEmpty());
    }

}
