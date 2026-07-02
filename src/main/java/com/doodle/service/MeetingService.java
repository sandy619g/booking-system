package com.doodle.service;

import com.doodle.dto.request.CreateMeetingRequest;
import com.doodle.dto.response.MeetingResponse;
import com.doodle.entity.Meeting;
import com.doodle.entity.Slot;
import com.doodle.entity.SlotStatus;
import com.doodle.entity.User;
import com.doodle.repo.MeetingRepository;
import com.doodle.repo.SlotRepository;
import com.doodle.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final SlotRepository slotRepository;
    private final UserRepository userRepository;

    public MeetingResponse create(CreateMeetingRequest request) {

        Slot organizerSlot = slotRepository.findById(
                        request.getOrganizerSlotId())
                .orElseThrow(() ->
                        new RuntimeException("Organizer slot not found"));

        if (organizerSlot.getStatus() == SlotStatus.BUSY) {
            throw new RuntimeException("Slot already booked");
        }

        Meeting meeting = Meeting.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .build();

        List<User> participants = new ArrayList<>();

        for (Long participantId : request.getParticipantIds()) {

            User participant = userRepository.findById(participantId)
                    .orElseThrow(() ->
                            new RuntimeException("User not found"));

            participants.add(participant);
        }

        meeting.setParticipants(participants);

        meeting = meetingRepository.save(meeting);

        organizerSlot.setMeeting(meeting);
        organizerSlot.setStatus(SlotStatus.BUSY);

        slotRepository.save(organizerSlot);

        for (User participant : participants) {

            Slot slot = slotRepository
                    .findByUserIdAndStatusAndStartTimeAndEndTime(

                            participant.getId(),

                            SlotStatus.FREE,

                            organizerSlot.getStartTime(),

                            organizerSlot.getEndTime())

                    .stream()
                    .findFirst()

                    .orElseThrow(() ->
                            new RuntimeException(
                                    participant.getName()
                                            + " has no available slot"));

            slot.setMeeting(meeting);
            slot.setStatus(SlotStatus.BUSY);

            slotRepository.save(slot);
        }
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

    @Transactional
    public void cancel(Long meetingId) {

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() ->
                        new RuntimeException("Meeting not found"));

        for (Slot slot : meeting.getBookedSlots()) {

            slot.setStatus(SlotStatus.FREE);
            slot.setMeeting(null);

            slotRepository.save(slot);
        }

        meetingRepository.delete(meeting);
    }

}