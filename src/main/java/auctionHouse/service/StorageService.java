package auctionHouse.service;

import auctionHouse.InstanceCollection;
import auctionHouse.ItemStackHelperFunctions;
import auctionHouse.entity.Storage;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.FutureTask;

public class StorageService {

    synchronized public JSONObject getInventoryJson(String storageId){
        InstanceCollection.storageRepository.getChestInventory(storageId);
        return new JSONObject();
    }

    synchronized public JSONObject getInventoryJsonFromShortUuid(String shortStorageUuid){
        try {
            ArrayList<Storage> storages = InstanceCollection.storageRepository.read();
            for(Storage storage : storages){
                if(storage.getUuid().toString().substring(0,6).equals(shortStorageUuid)) {

                    final FutureTask<Object> ft = new FutureTask<Object>(() -> {}, new Object());
                    JSONObject jsonObject = new JSONObject();

                    BukkitRunnable bukkitRunnable = new BukkitRunnable() {
                        @Override
                        public void run() {

                            World world =Bukkit.getWorld(storage.getWorldUuid());
                            Block chestBlock = world.getBlockAt(storage.getChestX(),storage.getChestY(),storage.getChestZ());

                            BlockState blockState = chestBlock.getState();

                            Chest chest = (Chest) blockState;

                            ArrayList<JSONObject>  jsonObjects = new ArrayList<>();
                            for(ItemStack itemStack :  chest.getBlockInventory().getContents()){
                                if(itemStack != null){

                                    JSONObject jsonObject1 = new JSONObject();
                                    jsonObject1.put("amount",itemStack.getAmount());
                                    jsonObject1.put("name",itemStack.getType().toString());

                                    String enchantments = itemStack.getEnchantments().toString();
                                    jsonObject1.put("enchantments",enchantments);
                                    jsonObject1.put("hashCode", ItemStackHelperFunctions.ItemStackToCustomHashCode(itemStack));


                                    jsonObjects.add(jsonObject1);

                                }

                            }
                            jsonObject.put("inventory",jsonObjects);
                            ft.run();
                        }
                    };
                    bukkitRunnable.runTask(Bukkit.getPluginManager().getPlugin("auctionHouse"));
                    ft.get();
                    jsonObject.put("storageId",storage.getUuid().toString());

                    return jsonObject;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    synchronized private Chest storageToChestBlock(Storage storage){
        World world =Bukkit.getWorld(storage.getWorldUuid());
        Block chestBlock = world.getBlockAt(storage.getChestX(),storage.getChestY(),storage.getChestZ());
        BlockState blockState = chestBlock.getState();
        return (Chest) blockState;
    }

    public void saveStorage(Storage storage){
        ArrayList<Storage> storages = new ArrayList<>();
        storages.add(storage);

        try {
            InstanceCollection.storageRepository.write(storages);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
