package com.js.event;

import org.springframework.context.ApplicationEvent;

public class EnvironmentChangeEvent extends ApplicationEvent {

    public EnvironmentChangeEvent(Object source) {
        super(source);
    }
}
