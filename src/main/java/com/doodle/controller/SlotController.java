package com.doodle.controller;

import com.doodle.dto.request.CreateSlotRequest;
import com.doodle.dto.request.UpdateSlotRequest;
import com.doodle.dto.request.UpdateSlotStatusRequest;
import com.doodle.dto.response.SlotResponse;
import com.doodle.entity.SlotStatus;
import com.doodle.service.SlotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/slots")
@RequiredArgsConstructor
public class SlotController {

    private final SlotService slotService;

    @PostMapping
    public SlotResponse create(
            @PathVariable Long userId,
            @Valid @RequestBody CreateSlotRequest request) {

        return slotService.create(userId, request);
    }

    @DeleteMapping("/{slotId}")
    public void delete(@PathVariable Long slotId) {
        slotService.delete(slotId);
    }

    @PutMapping("/{slotId}")
    public SlotResponse update(
            @PathVariable Long slotId,
            @Valid @RequestBody UpdateSlotRequest request) {

        return slotService.update(slotId, request);
    }

    @PatchMapping("/{slotId}/status")
    public SlotResponse updateStatus(@PathVariable Long slotId, @RequestBody UpdateSlotStatusRequest request) {

        return slotService.updateStatus(
                slotId,
                request.getStatus()
        );
    }

    @GetMapping
    public List<SlotResponse> get(@PathVariable Long userId, @RequestParam(required = false) SlotStatus status) {
        if (status == null)
            return slotService.getSlots(userId);

        return slotService.getByStatus(userId, status);
    }
}
