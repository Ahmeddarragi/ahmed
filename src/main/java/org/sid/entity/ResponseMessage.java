package org.sid.entity;

import jakarta.persistence.Column;

public class ResponseMessage {
	@Column(name="message")
    private String message;

    public ResponseMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}