package org.floracore.api.event.message;

import org.floracore.api.*;
import org.floracore.api.event.*;
import org.floracore.api.messenger.message.*;

/**
 * 消息接受事件
 */
public class MessageReceiveEvent extends FloraCoreEvent {
    private final Message message;

    public MessageReceiveEvent(FloraCore floraCore, Message message) {
        super(floraCore);
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }
}
