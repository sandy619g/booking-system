package com.doodle.dto.response;

import com.doodle.entity.SlotStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SlotResponse {

    private Long id;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private SlotStatus status;
}
