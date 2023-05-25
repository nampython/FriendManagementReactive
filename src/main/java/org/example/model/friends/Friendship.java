package org.example.model.friends;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "friendship", schema = "friendsmanagement", catalog = "")
public class Friendship {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "friendship_id")
    private int friendshipId;
    @Basic
    @Column(name = "user_id")
    private int userId;
    @Basic
    @Column(name = "friend_id")
    private int friendId;
    @Basic
    @Column(name = "status")
    private String status;

}
