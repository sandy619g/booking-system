package com.doodle.dto.request;

import com.doodle.entity.SlotStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateSlotStatusRequest {

    @NotNull
    private SlotStatus status;
}
