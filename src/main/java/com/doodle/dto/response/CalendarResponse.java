package com.doodle.dto.response;

import com.doodle.entity.SlotStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CalendarResponse {

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private SlotStatus status;

    private Long meetingId;

    private String meetingTitle;
}