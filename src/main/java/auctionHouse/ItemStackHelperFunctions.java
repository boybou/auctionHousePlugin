package auctionHouse;

import org.bukkit.inventory.ItemStack;
import org.json.JSONObject;

public class ItemStackHelperFunctions {

    public static int ItemStackToCustomHashCode(ItemStack itemStack){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("amount",itemStack.getAmount());
        jsonObject.put("name",itemStack.getType().toString());

        String enchantments = itemStack.getEnchantments().toString();
        jsonObject.put("enchantments",enchantments);

        return jsonObject.toString().hashCode();

    }
}
