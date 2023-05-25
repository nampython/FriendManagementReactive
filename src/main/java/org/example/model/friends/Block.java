package org.example.model.friends;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "block", schema = "friendsmanagement", catalog = "")
public class Block {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "block_id")
    private int blockId;
    @Basic
    @Column(name = "blocker_id")
    private int blockerId;
    @Basic
    @Column(name = "blocked_id")
    private int blockedId;

    public Block(int blockerId, int blockedId) {
        this.blockerId = blockerId;
        this.blockedId = blockedId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Block that = (Block) o;
        return blockId == that.blockId && blockerId == that.blockerId && blockedId == that.blockedId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(blockId, blockerId, blockedId);
    }
}
