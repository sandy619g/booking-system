package com.doodle.exceptions;

public class SlotAlreadyBookedException extends RuntimeException {

    public SlotAlreadyBookedException() {
        super("Slot is already booked");
    }
}