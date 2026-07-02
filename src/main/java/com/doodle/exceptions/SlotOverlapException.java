package com.doodle.exceptions;

public class SlotOverlapException extends RuntimeException {

    public SlotOverlapException() {
        super("Slot overlaps with an existing slot");
    }
}
