package com.doodle.service;

import com.doodle.dto.request.CreateSlotRequest;
import com.doodle.dto.request.UpdateSlotRequest;
import com.doodle.dto.response.SlotResponse;
import com.doodle.entity.Slot;
import com.doodle.entity.SlotStatus;
import com.doodle.entity.User;
import com.doodle.repo.SlotRepository;
import com.doodle.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SlotService {

    private final SlotRepository slotRepository;
    private final UserRepository userRepository;

    public SlotResponse create(Long userId, CreateSlotRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Slot slot = Slot.builder()
                .user(user)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status(SlotStatus.FREE)
                .build();

        return map(slotRepository.save(slot));
    }

    public List<SlotResponse> getSlots(Long userId) {

        return slotRepository.findByUserId(userId)
                .stream()
                .map(this::map)
                .toList();
    }

    public SlotResponse update(Long slotId,
                               UpdateSlotRequest request) {

        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        if (slot.getStatus() == SlotStatus.BUSY) {
            throw new RuntimeException("Booked slots cannot be modified");
        }

        boolean overlap = slotRepository
                .existsByUserIdAndIdNotAndStartTimeLessThanAndEndTimeGreaterThan(
                        slot.getUser().getId(),
                        slot.getId(),
                        request.getEndTime(),
                        request.getStartTime()
                );

        if (overlap &&
                (!slot.getStartTime().equals(request.getStartTime())
                        || !slot.getEndTime().equals(request.getEndTime()))) {

            throw new RuntimeException("Overlapping slot");
        }

        slot.setStartTime(request.getStartTime());
        slot.setEndTime(request.getEndTime());

        return map(slotRepository.save(slot));
    }

    public SlotResponse updateStatus(
            Long slotId,
            SlotStatus status) {

        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        slot.setStatus(status);

        return map(slotRepository.save(slot));
    }

    public void delete(Long slotId) {

        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        if (slot.getStatus() == SlotStatus.BUSY) {
            throw new RuntimeException("Booked slot cannot be deleted");
        }

        slotRepository.delete(slot);
    }

    public List<SlotResponse> getByStatus(
            Long userId,
            SlotStatus status) {

        return slotRepository
                .findByUserIdAndStatus(userId, status)
                .stream()
                .map(this::map)
                .toList();
    }

    public List<SlotResponse> getBetween(
            Long userId,
            LocalDateTime from,
            LocalDateTime to) {

        return slotRepository
                .findByUserIdAndStartTimeBetween(
                        userId,
                        from,
                        to
                )
                .stream()
                .map(this::map)
                .toList();
    }

    private SlotResponse map(Slot slot) {

        return SlotResponse.builder()
                .id(slot.getId())
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .status(slot.getStatus())
                .build();
    }


}
