package org.example.model.friends;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "updatemention", schema = "friendsmanagement", catalog = "")
public class Updatemention {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "mention_id")
    private int mentionId;
    @Basic
    @Column(name = "sender_id")
    private int senderId;
    @Basic
    @Column(name = "mentioned_id")
    private int mentionedId;

    public int getMentionId() {
        return mentionId;
    }

    public void setMentionId(int mentionId) {
        this.mentionId = mentionId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getMentionedId() {
        return mentionedId;
    }

    public void setMentionedId(int mentionedId) {
        this.mentionedId = mentionedId;
    }
}
