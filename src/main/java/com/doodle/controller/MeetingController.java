package com.doodle.controller;

import com.doodle.dto.request.CreateMeetingRequest;
import com.doodle.dto.response.MeetingResponse;
import com.doodle.service.MeetingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/meetings")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    @PostMapping
    public MeetingResponse create(@Valid @RequestBody CreateMeetingRequest request) {
        return meetingService.create(request);
    }

    @DeleteMapping("/{id}")
    public void cancel(
            @PathVariable Long id) {

        meetingService.cancel(id);
    }
}
