package auctionHouse.service;

import auctionHouse.InstanceCollection;
import auctionHouse.ItemStackHelperFunctions;
import auctionHouse.entity.Auction;
import auctionHouse.entity.Storage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class AuctionService {


    public Auction removeAuction(UUID auctionUuid){
        try {
            Auction auction = InstanceCollection.auctionRepository.remove(auctionUuid);
            return auction;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject fillAuction(UUID auctionUuid, UUID payingStorageUid){
        final Auction auction = getAuction(auctionUuid);
        if(auction == null) return new JSONObject();
        try {
            ArrayList<Storage> storages = InstanceCollection.storageRepository.read();

            Storage payingStorage = null;
            Storage auctionStorage = null;
            for(Storage storage:storages){
                if(storage.getUuid().toString().equals(payingStorageUid.toString())) payingStorage = storage;
                if(storage.getUuid().toString().equals(auction.getStorageUuid().toString())) auctionStorage = storage;
            }
            if(payingStorage == null || auctionStorage == null) return new JSONObject();

            final Storage payingStorageFinal = payingStorage;
            final Storage auctionStorageFinal = auctionStorage;

            final FutureTask<Object> ft = new FutureTask<Object>(() -> {}, new Object());
            JSONObject jsonObject = new JSONObject();

            BukkitRunnable bukkitRunnable = new BukkitRunnable() {

                private Chest getChestState(Storage storage){
                    World world = Bukkit.getWorld(storage.getWorldUuid());
                    Block chestBlock = world.getBlockAt(storage.getChestX(),storage.getChestY(),storage.getChestZ());

                    BlockState blockState = chestBlock.getState();

                    Chest chest = (Chest) blockState;
                    return chest;
                }

                private boolean checkFunds(Chest chest,int amount){
                    Inventory chestInv = chest.getBlockInventory();
                    int fundSum = 0;
                    for(ItemStack itemStack : chestInv.getContents()){

                        if(itemStack != null) {
                            if(itemStack.getType().toString().equals("GOLD_INGOT")) fundSum += (9*itemStack.getAmount());
                            else if(itemStack.getType().toString().equals("GOLD_NUGGET")) fundSum+=itemStack.getAmount();
                        }
                    }
                    return (fundSum >= amount);
                }
                private boolean checkItems(Chest chest,int hash){
                    Inventory chestInv = chest.getBlockInventory();

                    for(ItemStack itemStack : chestInv.getContents()){

                        if(itemStack != null) {
                            if(ItemStackHelperFunctions.ItemStackToCustomHashCode(itemStack) == hash){
                                return true;
                        }
                        }
                    }
                    return false;
                }

                private boolean checkRoomForPayer(Chest chest){
                    Inventory chestInv = chest.getBlockInventory();

                    for(ItemStack itemStack : chestInv.getContents()){
                        if(itemStack == null) return true;
                    }
                    return false;
                }

                private boolean checkRoomForSeller(Chest chest,int amount){
                    Inventory chestInv = chest.getBlockInventory();
                    int spaces =(amount/9)/64;

                    int emptySpaces = 0;
                    for(ItemStack itemStack : chestInv.getContents()){
                        if(itemStack == null)emptySpaces++;
                    }
                    return (spaces == 0 && emptySpaces > 0) || (spaces != 0 && spaces < emptySpaces);

                }

                private void addAmountToInventory(Inventory inventory,int amount){
                    int toAddIngots = (amount/9);
                    int toAddNuggets = amount - (toAddIngots*9);
                    while (toAddIngots > 64){
                        inventory.addItem(new ItemStack(Material.GOLD_INGOT,64));
                        toAddIngots-=64;
                    }
                    if(toAddIngots>0) inventory.addItem(new ItemStack(Material.GOLD_INGOT,toAddIngots));
                    if(toAddNuggets>0) inventory.addItem(new ItemStack(Material.GOLD_NUGGET,toAddNuggets));
                }

                private void addToInventoryBalance(Inventory inventory,int amountToAdd){

                    int fundSum = collectFunds(inventory);
                    fundSum+=amountToAdd;

                    addFunds(inventory,fundSum);




                }
                private void removeFromInventoryBalance(Inventory inventory,int amountToRemove){

                    int fundSum = collectFunds(inventory);
                    fundSum-=amountToRemove;

                    addFunds(inventory,fundSum);

                }

                private void addFunds(Inventory inventory,int amount){
                    int toAddIngots = (amount/9);
                    int toAddNuggets = amount - (toAddIngots*9);
                    while (toAddIngots > 64){
                        inventory.addItem(new ItemStack(Material.GOLD_INGOT,64));
                        toAddIngots-=64;
                    }
                    if(toAddIngots>0) inventory.addItem(new ItemStack(Material.GOLD_INGOT,toAddIngots));
                    if(toAddNuggets>0) inventory.addItem(new ItemStack(Material.GOLD_NUGGET,toAddNuggets));
                }

                private int collectFunds(Inventory inventory){
                    int fundSum = 0;
                    for(int i =0;i<inventory.getSize();i++){
                        ItemStack itemStack = inventory.getItem(i);
                        if(itemStack != null) {
                            if(itemStack.getType().toString().equals("GOLD_INGOT")) {
                                fundSum += (9*itemStack.getAmount());
                                inventory.clear(i);
                            }
                            else if(itemStack.getType().toString().equals("GOLD_NUGGET")) {
                                fundSum += (itemStack.getAmount());
                                inventory.clear(i);
                            }
                        }
                    }
                    return fundSum;
                }

                private boolean swap(Chest payerChest,Chest sellerChest,int hash,int amount){
                    Inventory payerChestInv = payerChest.getBlockInventory();
                    Inventory sellerChestInv = sellerChest.getBlockInventory();

                    //remove item from seller and put it in payer inv
                    for(int i =0;i<sellerChestInv.getSize();i++){
                        ItemStack itemStack = sellerChestInv.getItem(i);
                        if(itemStack !=null && ItemStackHelperFunctions.ItemStackToCustomHashCode(itemStack) == hash){
                            sellerChestInv.clear(i);
                            payerChestInv.addItem(itemStack);
                        }
                    }

                    //remove funds from payer and put it in seller inv
                    removeFromInventoryBalance(payerChestInv,amount);
                    addToInventoryBalance(sellerChestInv,amount);

                    return true;

                }
                @Override
                public void run() {
                    try{
                        Chest payerChest = getChestState(payingStorageFinal);
                        Chest auctionerChset = getChestState(auctionStorageFinal);

                        boolean enoughFunds = checkFunds(payerChest,auction.getPrice());
                        boolean itemsAvailable = checkItems(auctionerChset,auction.getItemHashCode());

                        boolean enoughRoomForPayer = checkRoomForPayer(payerChest);
                        boolean enoughRoomForSeller = checkRoomForSeller(auctionerChset,auction.getPrice());

                        jsonObject.put("enoughFunds",enoughFunds);
                        jsonObject.put("itemsAvailable",itemsAvailable);
                        jsonObject.put("enoughRoomForPayer",enoughRoomForPayer);
                        jsonObject.put("enoughRoomForSeller",enoughRoomForSeller);

                        if(enoughFunds&& itemsAvailable && enoughRoomForPayer && enoughRoomForSeller){
                            boolean swapped = swap(payerChest,auctionerChset,auction.getItemHashCode(),auction.getPrice());
                            jsonObject.put("swapped",swapped);
                        }else{
                            jsonObject.put("swapped",false);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }



                    ft.run();
                }
            };
            bukkitRunnable.runTask(Bukkit.getPluginManager().getPlugin("auctionHouse"));
            ft.get();
            return jsonObject;

        } catch (IOException | ClassNotFoundException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return new JSONObject();
        }

//        return new JSONObject();
    }

    public Auction getAuction(UUID uuid) {
        try{

            ArrayList<Auction> auctions = InstanceCollection.auctionRepository.read();

            for(Auction auction : auctions){
                if(auction.getUuid().toString().equals(uuid.toString())) return auction;
            }

        }
        catch (Exception e){
        }

        return null;

    }

    public ArrayList<Auction> getAuctions(){
        try{
            ArrayList<Auction> auctions = InstanceCollection.auctionRepository.read();
            return auctions;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void postAuction(Auction auction){
        ArrayList<Auction> auctions = new ArrayList<>();
        auctions.add(auction);
        try {
            InstanceCollection.auctionRepository.write(auctions);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
