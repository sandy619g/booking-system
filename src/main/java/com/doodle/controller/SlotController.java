package com.doodle.controller;

import com.doodle.dto.request.CreateSlotRequest;
import com.doodle.dto.response.SlotResponse;
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

    @GetMapping
    public List<SlotResponse> getSlots(@PathVariable Long userId) {
        return slotService.getSlots(userId);
    }

    @DeleteMapping("/{slotId}")
    public void delete(@PathVariable Long slotId) {
        slotService.delete(slotId);
    }
}
