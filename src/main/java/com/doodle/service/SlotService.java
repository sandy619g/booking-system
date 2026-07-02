package com.doodle.service;

import com.doodle.dto.request.CreateSlotRequest;
import com.doodle.dto.response.SlotResponse;
import com.doodle.entity.Slot;
import com.doodle.entity.SlotStatus;
import com.doodle.entity.User;
import com.doodle.repo.SlotRepository;
import com.doodle.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public void delete(Long slotId) {
        slotRepository.deleteById(slotId);
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
