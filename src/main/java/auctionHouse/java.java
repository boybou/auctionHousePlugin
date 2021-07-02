package auctionHouse;

import auctionHouse.entity.Storage;
import com.sun.media.jfxmedia.logging.Logger;
import com.sun.net.httpserver.HttpServer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import java.net.URI;
import java.util.UUID;
import java.util.logging.Level;



public final class java extends JavaPlugin implements Listener {

    private AuctionHouseHttpServer auctionHouseHttpServer;

    @EventHandler
    public void onSignChange(SignChangeEvent signChangeEvent){

        String[] lines = signChangeEvent.getLines();
        if(lines[0].equals("[storage]") && lines[1].equals("")) {
            Block block =  signChangeEvent.getBlock();
            if(block.getType() == Material.OAK_WALL_SIGN) {
                WallSign blockData = (WallSign) block.getBlockData();
                BlockFace signFace = blockData.getFacing();
                Block relative = block.getRelative(signFace, -1);
                if (relative.getType() == Material.CHEST) {
                    Storage storage = createStorage(signChangeEvent.getPlayer(),block,relative);
                    signChangeEvent.setLine(1,storage.getUuid().toString().substring(0,6));
                    InstanceCollection.storageService.saveStorage(storage);
                }
            }
        }

    }

    private Storage createStorage(Player player,Block signBlock,Block chestBlock){
        UUID worldUuid = signBlock.getWorld().getUID();
        Storage storage = new Storage(
                worldUuid,
                chestBlock.getX(),
                chestBlock.getY(),
                chestBlock.getZ(),
                signBlock.getX(),
                signBlock.getY(),
                signBlock.getZ()
        );

        return storage;
    }



    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this,this);
        InstanceCollection.instantiate();

        try{
            this.auctionHouseHttpServer = new AuctionHouseHttpServer();
            auctionHouseHttpServer.start();
        }
        catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
