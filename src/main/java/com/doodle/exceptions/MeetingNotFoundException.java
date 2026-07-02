package com.doodle.exceptions;

public class MeetingNotFoundException extends RuntimeException {

    public MeetingNotFoundException(Long id) {
        super("Meeting " + id + " not found");
    }
}
