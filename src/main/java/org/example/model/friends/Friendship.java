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
@Table(name = "friendship", schema = "friendsmanagement", catalog = "")
public class Friendship {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "friendship_id")
    private Integer friendshipId;
    @Basic
    @Column(name = "user_id")
    private Integer userId;
    @Basic
    @Column(name = "friend_id")
    private Integer friendId;
    @Basic
    @Column(name = "status")
    private String status;

}
