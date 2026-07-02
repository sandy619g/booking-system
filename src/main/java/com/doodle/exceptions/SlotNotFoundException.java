package com.doodle.exceptions;

public class SlotNotFoundException extends RuntimeException {

    public SlotNotFoundException(Long id) {
        super("Slot with id " + id + " not found");
    }
}
