package auctionHouse.repository;

import auctionHouse.entity.Auction;
import auctionHouse.entity.Storage;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StorageRepository extends Repository<Storage> {


    public StorageRepository() {
        super("storages");
    }

    public Inventory getChestInventory(String storageId){
        this.getChestFromStorageId(storageId);
        return null;
    }

    private Chest getChestFromStorageId(String storageId){

        return null;
    }



}
