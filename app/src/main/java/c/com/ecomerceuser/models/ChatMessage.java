package c.com.ecomerceuser.models;

/**
 * Created by user on 01-05-2017.
 */
public class ChatMessage {
    String senderId,receiverId,message,messageSent,sent_time,senderId_receiverId;
    String userThumbnail;

    public String getUserThumbnail() {
        return userThumbnail;
    }

    public void setUserThumbnail(String userThumbnail) {
        this.userThumbnail = userThumbnail;
    }

    public String getSenderId_receiverId() {
        return senderId_receiverId;
    }

    public void setSenderId_receiverId(String senderId_receiverId) {
        this.senderId_receiverId = senderId_receiverId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageSent() {
        return messageSent;
    }

    public void setMessageSent(String messageSent) {
        this.messageSent = messageSent;
    }

    public String getSent_time() {
        return sent_time;
    }

    public void setSent_time(String sent_time) {
        this.sent_time = sent_time;
    }
}
