package com.doodle.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MeetingResponse {

    private Long id;

    private String title;

    private String description;

    private List<Long> participants;
}
