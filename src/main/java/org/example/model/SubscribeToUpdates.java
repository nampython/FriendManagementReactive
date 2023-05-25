package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.model.friends.Friendship;
import org.example.model.friends.Subscription;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SubscribeToUpdates extends ResponseObject {
    private String success = "false";
    private Subscription subscription = null;
}
