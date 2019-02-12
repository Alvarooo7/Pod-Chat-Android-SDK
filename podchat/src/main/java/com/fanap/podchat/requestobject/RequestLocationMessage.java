package com.fanap.podchat.requestobject;

import android.app.Activity;

public class RequestLocationMessage extends BaseRequestMapStImage {

    private long threadId;
    private String message;
    private int messageType;
    private Activity activity;

    public RequestLocationMessage(Builder builder) {
        super(builder);
        this.threadId = builder.threadId;
        this.message = builder.message;
        this.messageType = builder.messageType;
        this.activity = builder.activity;
    }


    public static class Builder extends BaseRequestMapStImage.Builder<Builder> {
        private long threadId;
        private String message;
        private int messageType;
        private Activity activity;

        public Builder activity(Activity activity) {
            this.activity = activity;
            return this;
        }

        public Builder messageType(int messageType) {
            this.messageType = messageType;
            return this;
        }

        public Builder threadId(long threadId) {
            this.threadId = threadId;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public RequestLocationMessage build() {
            return new RequestLocationMessage(this);
        }

        @Override
        protected Builder self() {
            return this;
        }


    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
