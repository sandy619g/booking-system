package com.doodle.exceptions;

public class ParticipantUnavailableException extends RuntimeException {

    public ParticipantUnavailableException(Long userId) {
        super("Participant " + userId + " has no available slot");
    }
}
