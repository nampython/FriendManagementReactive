package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.model.friends.Subscription;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SubscribeToUpdates {
    private Subscription subscription = null;
}
