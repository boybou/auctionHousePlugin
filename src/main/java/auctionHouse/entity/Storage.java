package auctionHouse.entity;

import java.io.Serializable;
import java.util.UUID;

public class Storage extends WritableEntity{

    private UUID worldUuid;
    private int chestX;
    private int chestY;
    private int chestZ;
    private int signX;
    private int signY;
    private int signZ;

    public Storage(UUID worldUuid,int chestX, int chestY, int chestZ, int signX, int signY, int signZ) {
        super();
        this.worldUuid = worldUuid;
        this.chestX = chestX;
        this.chestY = chestY;
        this.chestZ = chestZ;
        this.signX = signX;
        this.signY = signY;
        this.signZ = signZ;
    }


    public UUID getWorldUuid() {
        return worldUuid;
    }

    public void setWorldUuid(UUID worldUuid) {
        this.worldUuid = worldUuid;
    }

    public int getChestX() {
        return chestX;
    }

    public void setChestX(int chestX) {
        this.chestX = chestX;
    }

    public int getChestY() {
        return chestY;
    }

    public void setChestY(int chestY) {
        this.chestY = chestY;
    }

    public int getChestZ() {
        return chestZ;
    }

    public void setChestZ(int chestZ) {
        this.chestZ = chestZ;
    }

    public int getSignX() {
        return signX;
    }

    public void setSignX(int signX) {
        this.signX = signX;
    }

    public int getSignY() {
        return signY;
    }

    public void setSignY(int signY) {
        this.signY = signY;
    }

    public int getSignZ() {
        return signZ;
    }

    public void setSignZ(int signZ) {
        this.signZ = signZ;
    }
}
