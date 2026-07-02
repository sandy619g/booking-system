package com.doodle.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateMeetingRequest {

    @NotBlank
    private String title;

    private String description;

    @NotNull
    private Long organizerSlotId;

    @NotEmpty
    private List<Long> participantIds;
}
