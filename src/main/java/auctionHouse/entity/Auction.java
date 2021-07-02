package auctionHouse.entity;


import java.io.Serializable;
import java.util.UUID;

public class Auction extends WritableEntity{


    private UUID storageUuid;
    private String enchantments;
    private int itemHashCode;
    private String itemName;
    private int amount;
    private int price;


    public Auction(UUID storageUuid, String enchantments, int itemHashCode, String itemName, int amount, int price) {
        this.storageUuid = storageUuid;
        this.enchantments = enchantments;
        this.itemHashCode = itemHashCode;
        this.itemName = itemName;
        this.amount = amount;
        this.price = price;
    }


    public UUID getStorageUuid() {
        return storageUuid;
    }

    public void setStorageUuid(UUID storageUuid) {
        this.storageUuid = storageUuid;
    }

    public String getEnchantments() {
        return enchantments;
    }

    public void setEnchantments(String enchantments) {
        this.enchantments = enchantments;
    }

    public int getItemHashCode() {
        return itemHashCode;
    }

    public void setItemHashCode(int itemHashCode) {
        this.itemHashCode = itemHashCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
