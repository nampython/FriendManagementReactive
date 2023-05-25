package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.model.friends.Friendship;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FriendConnection {
    private Friendship friendship = null;
}
