package com.fanap.podchat.mainmodel;

import java.util.List;

public class CreateThreadRequest {

    private int type;
    private String ownerSsoId;
    private List<Invitee> invitees;
    private String title;
    private RequestThreadInnerMessage message;

    public CreateThreadRequest(Builder builder) {
        this.type = builder.type;
        this.message = builder.message;
        this.title = builder.title;
        this.invitees = builder.invitees;
    }

    public static class Builder {
        private final int type;
        private final List<Invitee> invitees;
        private String title;
        private final RequestThreadInnerMessage message;

        public Builder(int type, List<Invitee> invitees, RequestThreadInnerMessage message) {
            this.invitees = invitees;
            this.type = type;
            this.message = message;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public CreateThreadRequest build() {
            return new CreateThreadRequest(this);
        }
    }

    public RequestThreadInnerMessage getMessage() {
        return message;
    }

    public void setMessage(RequestThreadInnerMessage message) {
        this.message = message;
    }

    public String getOwnerSsoId() {
        return ownerSsoId;
    }

    public void setOwnerSsoId(String ownerSsoId) {
        this.ownerSsoId = ownerSsoId;
    }

    public List<Invitee> getInvitees() {
        return invitees;
    }

    public void setInvitees(List<Invitee> invitees) {
        this.invitees = invitees;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
