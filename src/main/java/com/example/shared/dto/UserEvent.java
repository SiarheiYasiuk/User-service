package com.example.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEvent {
    public enum EventType {
        CREATED, DELETED
    }

    private EventType eventType;
    private String email;
    private String name;
}