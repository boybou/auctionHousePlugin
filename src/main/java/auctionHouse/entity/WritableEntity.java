package auctionHouse.entity;

import java.io.Serializable;
import java.util.UUID;

public class WritableEntity implements Serializable {

    private final UUID uuid;

    public WritableEntity() {
        this.uuid = UUID.randomUUID();
    }

    public UUID getUuid() {
        return uuid;
    }
}
