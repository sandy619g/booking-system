package com.doodle.service;

import com.doodle.dto.request.CreateMeetingRequest;
import com.doodle.dto.response.MeetingResponse;
import com.doodle.entity.Meeting;
import com.doodle.entity.Slot;
import com.doodle.entity.SlotStatus;
import com.doodle.entity.User;
import com.doodle.exceptions.InvalidMeetingException;
import com.doodle.exceptions.MeetingNotFoundException;
import com.doodle.exceptions.ParticipantUnavailableException;
import com.doodle.exceptions.SlotAlreadyBookedException;
import com.doodle.exceptions.SlotNotFoundException;
import com.doodle.exceptions.UserNotFoundException;
import com.doodle.repo.MeetingRepository;
import com.doodle.repo.SlotRepository;
import com.doodle.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final SlotRepository slotRepository;
    private final UserRepository userRepository;

    public MeetingResponse create(CreateMeetingRequest request) {

        // Validate duplicate participants
        Set<Long> participantIds = new HashSet<>(request.getParticipantIds());

        if (participantIds.size() != request.getParticipantIds().size()) {
            throw new InvalidMeetingException("Duplicate participants are not allowed.");
        }

        // Lock organizer slot
        Slot organizerSlot = slotRepository.findByIdForUpdate(request.getOrganizerSlotId())
                .orElseThrow(() -> new SlotNotFoundException(request.getOrganizerSlotId()));

        if (organizerSlot.getStatus() == SlotStatus.BUSY) {
            throw new SlotAlreadyBookedException();
        }

        Long organizerId = organizerSlot.getUser().getId();

        if (participantIds.contains(organizerId)) {
            throw new InvalidMeetingException("Organizer cannot also be a participant.");
        }

        Meeting meeting = Meeting.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .build();

        List<User> participants = new ArrayList<>();
        List<Slot> slotsToUpdate = new ArrayList<>();

        // Load participants
        for (Long participantId : participantIds) {

            User participant = userRepository.findById(participantId)
                    .orElseThrow(() -> new UserNotFoundException(participantId));

            participants.add(participant);
        }

        meeting.setParticipants(participants);
        meeting = meetingRepository.save(meeting);

        // Reserve organizer slot
        organizerSlot.setStatus(SlotStatus.BUSY);
        organizerSlot.setMeeting(meeting);
        slotsToUpdate.add(organizerSlot);

        // Reserve participant slots
        for (User participant : participants) {

            Slot participantSlot = slotRepository
                    .findAvailableSlotsForUpdate(
                            participant.getId(),
                            SlotStatus.FREE,
                            organizerSlot.getStartTime(),
                            organizerSlot.getEndTime())
                    .stream()
                    .findFirst()
                    .orElseThrow(() ->
                            new ParticipantUnavailableException(participant.getId()));

            participantSlot.setStatus(SlotStatus.BUSY);
            participantSlot.setMeeting(meeting);

            slotsToUpdate.add(participantSlot);
        }

        slotRepository.saveAll(slotsToUpdate);

        return MeetingResponse.builder()
                .id(meeting.getId())
                .title(meeting.getTitle())
                .description(meeting.getDescription())
                .participants(
                        participants.stream()
                                .map(User::getId)
                                .toList()
                )
                .build();
    }

    public void cancel(Long meetingId) {

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new MeetingNotFoundException(meetingId));

        meeting.getBookedSlots().forEach(slot -> {
            slot.setStatus(SlotStatus.FREE);
            slot.setMeeting(null);
        });

        slotRepository.saveAll(meeting.getBookedSlots());
        meetingRepository.delete(meeting);
    }
}