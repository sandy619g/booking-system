package com.doodle.service;

import com.doodle.dto.request.CreateMeetingRequest;
import com.doodle.dto.response.MeetingResponse;
import com.doodle.entity.*;
import com.doodle.exceptions.InvalidMeetingException;
import com.doodle.exceptions.ParticipantUnavailableException;
import com.doodle.exceptions.SlotAlreadyBookedException;
import com.doodle.repo.MeetingRepository;
import com.doodle.repo.SlotRepository;
import com.doodle.repo.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeetingServiceTest {

    @Mock
    private MeetingRepository meetingRepository;

    @Mock
    private SlotRepository slotRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MeetingService meetingService;

    private Slot createSlot(Long userId, SlotStatus status) {
        return Slot.builder()
                .id(1L)
                .user(User.builder().id(userId).build())
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(1))
                .status(status)
                .build();
    }

    @Test
    void shouldCreateMeetingSuccessfully() {

        CreateMeetingRequest request = new CreateMeetingRequest();
        request.setTitle("Standup");
        request.setOrganizerSlotId(1L);
        request.setParticipantIds(List.of(2L));

        Slot organizerSlot = createSlot(1L, SlotStatus.FREE);

        when(slotRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(organizerSlot));

        User participant = User.builder().id(2L).build();

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(participant));

        Slot participantSlot = createSlot(2L, SlotStatus.FREE);

        when(slotRepository.findAvailableSlotsForUpdate(
                any(), any(), any(), any()
        )).thenReturn(List.of(participantSlot));

        when(meetingRepository.save(any()))
                .thenAnswer(inv -> {
                    Meeting m = inv.getArgument(0);
                    m.setId(100L);
                    return m;
                });

        MeetingResponse response = meetingService.create(request);

        assertNotNull(response);
        assertEquals("Standup", response.getTitle());

        verify(meetingRepository, times(1)).save(any());
    }

    @Test
    void shouldThrowWhenDuplicateParticipants() {

        CreateMeetingRequest request = new CreateMeetingRequest();
        request.setOrganizerSlotId(1L);
        request.setParticipantIds(List.of(2L, 2L));

        assertThrows(InvalidMeetingException.class,
                () -> meetingService.create(request));
    }

    @Test
    void shouldThrowWhenOrganizerIsParticipant() {

        CreateMeetingRequest request = new CreateMeetingRequest();
        request.setOrganizerSlotId(1L);
        request.setParticipantIds(List.of(1L));

        Slot organizerSlot = createSlot(1L, SlotStatus.FREE);

        when(slotRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(organizerSlot));

        assertThrows(InvalidMeetingException.class,
                () -> meetingService.create(request));
    }

    @Test
    void shouldThrowWhenOrganizerSlotBusy() {

        CreateMeetingRequest request = new CreateMeetingRequest();
        request.setOrganizerSlotId(1L);
        request.setParticipantIds(List.of(2L));

        Slot organizerSlot = createSlot(1L, SlotStatus.BUSY);

        when(slotRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(organizerSlot));

        assertThrows(SlotAlreadyBookedException.class,
                () -> meetingService.create(request));
    }

    @Test
    void shouldThrowWhenParticipantHasNoSlot() {

        CreateMeetingRequest request = new CreateMeetingRequest();
        request.setOrganizerSlotId(1L);
        request.setParticipantIds(List.of(2L));

        Slot organizerSlot = createSlot(1L, SlotStatus.FREE);

        when(slotRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(organizerSlot));

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(User.builder().id(2L).build()));

        when(slotRepository.findAvailableSlotsForUpdate(
                any(), any(), any(), any()
        )).thenReturn(List.of()); // no slot

        assertThrows(ParticipantUnavailableException.class,
                () -> meetingService.create(request));
    }

    @Test
    void shouldCancelMeetingSuccessfully() {

        Meeting meeting = Meeting.builder()
                .id(10L)
                .build();

        Slot slot = createSlot(1L, SlotStatus.BUSY);
        slot.setMeeting(meeting);

        meeting.setBookedSlots(List.of(slot));

        when(meetingRepository.findById(10L))
                .thenReturn(Optional.of(meeting));

        meetingService.cancel(10L);

        verify(slotRepository, times(1))
                .saveAll(any());

        verify(meetingRepository, times(1))
                .delete(meeting);
    }
}
