package technium.treasurechest;

import java.util.List;
import org.bukkit.inventory.ItemStack;

public class Chests
{
    private ItemStack item;
    private List<String> cmds;
    private List<Integer> ids;
    private List<Integer> percentages;
    private List<String> messages;
    private int slot;
    private String name;
    
    public Chests(final String n, final ItemStack i, final int s, final List<String> c, final List<Integer> id, final List<Integer> percent, final List<String> mess) {
        this.item = i;
        this.name = n;
        this.slot = s;
        this.cmds = c;
        this.ids = id;
        this.percentages = percent;
        this.messages = mess;
    }
    
    public ItemStack getItemStack() {
        return this.item;
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getSlot() {
        return this.slot;
    }
    
    public List<String> getCommands() {
        return this.cmds;
    }
    
    public List<String> getMessages() {
        return this.messages;
    }
    
    public List<Integer> getIds() {
        return this.ids;
    }
    
    public List<Integer> getPercentages() {
        return this.percentages;
    }
}
