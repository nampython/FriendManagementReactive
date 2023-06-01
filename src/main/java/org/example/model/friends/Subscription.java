package org.example.model.friends;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "subscription", schema = "friendsmanagement", catalog = "")
public class Subscription {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "subscription_id")
    private int subscriptionId;
    @Basic
    @Column(name = "subscriber_id")
    private int subscriberId;
    @Basic
    @Column(name = "target_id")
    private int targetId;

    public Subscription(int subscriberId, int targetId) {
        this.subscriberId = subscriberId;
        this.targetId = targetId;
    }
}
