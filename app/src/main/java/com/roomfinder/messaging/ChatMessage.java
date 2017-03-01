package com.roomfinder.messaging;

/**
 * Created by admin on 4/18/16.
 */
public class ChatMessage {

    String fromId;
    String toId;
    String message;
    String companionName;
    String messageMapId;
    String messageId;
    int isSelf;

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCompanionName() {
        return companionName;
    }

    public void setCompanionName(String companionName) {
        this.companionName = companionName;
    }

    public String getMessageMapId() {
        return messageMapId;
    }

    public void setMessageMapId(String messageMapId) {
        this.messageMapId = messageMapId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public int isSelf() {
        return isSelf;
    }

    public void setSelf(int self) {
        isSelf = self;
    }

    // to save in sqlite we put 0 for false and 1 for true.

}
