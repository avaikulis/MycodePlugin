package technium.treasurechest;

import org.bukkit.inventory.meta.ItemMeta;
import java.util.Iterator;
import java.util.Set;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.plugin.java.JavaPlugin;

public class TreasureChest extends JavaPlugin
{
    private List<Chests> chests;
    private List<String> chestNames;
    
    public TreasureChest() {
        this.chests = new ArrayList<Chests>();
        this.chestNames = new ArrayList<String>();
    }
    
    static HashMap<Integer, String> playerinv;
    static HashMap<Integer, String> playersubinv;
    static HashMap<Integer, String> tplocation;
    
    
    @SuppressWarnings("unchecked")
	public void onEnable() {
        super.onEnable();
        this.getServer().getPluginManager().registerEvents((Listener) new Events(this), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener) new OpeningBlocks(this), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener) new OpeningPBlocks(this), (Plugin)this);
        this.getServer().getPluginManager().addPermission(new Permissions().admin);
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        this.setupChests();
        
        playerinv = (HashMap<Integer, String>) load(new File(getDataFolder(),"chests.dat"));
        playersubinv = (HashMap<Integer, String>) load(new File(getDataFolder(),"subchests.dat"));
        tplocation = (HashMap<Integer, String>) load(new File(getDataFolder(),"tplocation.dat"));
        
        
        if (playerinv == null){
        	playerinv = new HashMap<Integer, String>();
        }
        if (playersubinv == null){
        	playersubinv = new HashMap<Integer, String>();
        }
        if (tplocation == null){
        	tplocation = new HashMap<Integer, String>();
        }
    }
    
    public void onDisable() {
        super.onDisable();
        save(playerinv, new File(getDataFolder(), "chests.dat"));
        save(playersubinv, new File(getDataFolder(), "subchests.dat"));
        save(tplocation, new File(getDataFolder(), "tplocation.dat"));
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (label.equalsIgnoreCase("givekey")) {
            if (!sender.hasPermission(new Permissions().admin)) {
                sender.sendMessage("§cYou do not have permission to use this command!");
                return false;
            }
            if (args.length == 3) {
                Player player;
                try {
                    player = Bukkit.getPlayer(args[0]);
                }
                catch (Exception e) {
                    sender.sendMessage("§cThat is not a valid playername!");
                    return false;
                }
                if (!this.chestNames.contains(args[1])) {
                    sender.sendMessage("§cValid Chest Names: " + this.chestNames);
                    return false;
                }
                int num = 0;
                try {
                    num = Integer.parseInt(args[2]);
                }
                catch (Exception e2) {
                    sender.sendMessage("§cPlease enter a valid number!");
                    return false;
                }
                this.getConfig().set("Keys." + player.getUniqueId() + "." + args[1], (Object)num);
                this.saveConfig();
                sender.sendMessage("§6Gave " + player.getName() + " " + num + " " + args[1] + " key(s)!");
                return true;
            }
            else {
                sender.sendMessage("§cUse: /givekey [playername] [tier] [amount]");
            }
            
           
        }
        if (label.equalsIgnoreCase("addcords")){
        	if (!sender.hasPermission(new Permissions().admin)) {
                sender.sendMessage("§cYou do not have permission to use this command!");
                return false;
        	}
        	//if (args.length < 1){
        	//sender.sendMessage("/addcords <Open Chest to Add Before>");
        	//return false;
        	//}
			HumanEntity player = (HumanEntity) sender;
			if (Shorten.getXYZ(player, "x") == Boolean.FALSE){
				sender.sendMessage("You did not open a chest after restart/reload");
				return false;				
			}
        	int X = (int) Shorten.getXYZ(player, "x");
        	int Y = (int) Shorten.getXYZ(player, "y");
        	int Z = (int) Shorten.getXYZ(player, "z");

	    	String note = X + "," + Y + "," + Z;
	    	
	    	String chest1 = X + "," + Y + "," + (Z-2);
	    	String chest2 = (X-2) + "," + Y + "," + Z;
	    	String chest3 = X + "," + Y + "," + (Z+2);
	    	String chest4 = (X+2) + "," + Y + "," + Z;
	    	
	    
	    			
	    	
	    	playerinv.put(playerinv.size() + 1, note);
	    	playersubinv.put(playersubinv.size() + 1, ("________" + X + "," + Y + "," + Z + "________"));
	    	playersubinv.put(playersubinv.size() + 1, chest1);
	    	playersubinv.put(playersubinv.size() + 1, chest2);
	    	playersubinv.put(playersubinv.size() + 1, chest3);
	    	playersubinv.put(playersubinv.size() + 1, chest4);
	    	playersubinv.put(playersubinv.size() + 1, ("________" + X + "," + Y + "," + Z + "________"));
	    	sender.sendMessage("Added Cords of last clicked block after startup/restart , cords are " + note);
        	return true;
        }
        if (label.equalsIgnoreCase("clearcords")){
        	if (!sender.hasPermission(new Permissions().admin)) {
                sender.sendMessage("§cYou do not have permission to use this command!");
                return false;
        	}
        	if (args.length == 0){
        		sender.sendMessage("No Arguments were given");
        		return false;
        	}
        	if (args.length >= 3){
        		sender.sendMessage("Too much arguments given");
        		return false;
        	}
        	if (args[0].equals("all") || args[0].equals("sel")){
		    	if (args[0].toLowerCase().equals("all")){
		    		if (playerinv.isEmpty()){
		    			sender.sendMessage("There are no cords in chests.dat to be deleted");
		    			return false;
		    		}
		    		playerinv.clear();
		    		sender.sendMessage("Cleared all cords");
		    		return true;
		
		
		    	}
		    	if (args[0].toLowerCase().equals("sel")){
		    		if (playerinv.isEmpty()){
		    			sender.sendMessage("There are none cords in chests.dat to be deleted");
		    			return false;
		    		}
		    		if (args[1].toLowerCase().equals("lastopen")){
		    			if (Shorten.getXYZ((HumanEntity) sender, "x") == Boolean.FALSE){
		    				sender.sendMessage("You did not open a chest after restart/reload");
		    				return false;				
		    			}
		        		int X = (int) Shorten.getXYZ((HumanEntity) sender, "x");
		        		int Y = (int) Shorten.getXYZ((HumanEntity) sender, "y");
		        		int Z = (int) Shorten.getXYZ((HumanEntity) sender, "z");
		        		String XYZ = X + "," + Y + "," + Z;

						
		    			for(int i = 1;i <= playerinv.size(); i++){
		    				if (playerinv.get(i).equals(XYZ)){
		    					playerinv.remove(i);
		    					sender.sendMessage("Deleted Cords " + XYZ + " from datebase.");
		    					return true;
		    				}
		    				
		    				}
		    			sender.sendMessage("Last Open cords are not found in chests.dat");
	    				return false;
		    			}
		    	
		    			
		    		}

		    	}

        	}
        if(label.equalsIgnoreCase("tcaddtp")){
        	if (!sender.hasPermission(new Permissions().admin)) {
                sender.sendMessage("§cYou do not have permission to use this command!");
                return false;
        	}
        	if (args.length == 0){
        		sender.sendMessage("No Arguments were given");
        		return false;
        	}
        	if (args.length > 10){
        		sender.sendMessage("Too much arguments given");
        		return false;
        	}
        	for (int i = 0; i < (args.length); i++){
        		String Check = args[i];
        		for (int ia = 0; ia < Check.length();ia++ ){
        			if (!args[i].equalsIgnoreCase("lastopen")){
            			String Checking = Character.toString(Check.charAt(ia));
            			boolean reply = Shorten.AlphaSpecialCheck(Checking);
	        			if (reply == true){
	        				sender.sendMessage("Invalid Syntax!");
	        				return false;
	        				
	        			}
        			}
        		}
        	}
        	
        	
        	
        	if (args[0].equalsIgnoreCase("lastopen")){
        		if (Shorten.getXYZ((HumanEntity) sender, "x") == Boolean.FALSE){
    				sender.sendMessage("You did not open a chest after restart/reload");
    				return false;				
    			}
        		int X = (int) Shorten.getXYZ((HumanEntity) sender, "x");
        		int Y = (int) Shorten.getXYZ((HumanEntity) sender, "y");
        		int Z = (int) Shorten.getXYZ((HumanEntity) sender, "z");
        		
        		String TpBlock = args[1].toLowerCase();
        		String Full = X + "," + Y + "," + Z + "%" + TpBlock;
        		tplocation.put((tplocation.size() + 1), Full);
        		sender.sendMessage("Put Specified Cords Successfully");
        		return true;
        	}
        	String curblock =  args[0].toLowerCase();
        	String TpBlock = args[1].toLowerCase();
        	String Full = curblock + "%" + TpBlock;
        	tplocation.put((tplocation.size() + 1), Full);
        	sender.sendMessage("Put Specified Cords Successfully");
        	return true;
        	
        }
        
       
        
        
        	
        
        return false;
        
    }
    
    public void setupChests() {
        this.chests.clear();
        if (this.getConfig().getConfigurationSection("Chests") == null) {
            return;
        }
        final Set<String> keys = (Set<String>)this.getConfig().getConfigurationSection("Chests").getKeys(false);
        for (final String key : keys) {
            final String name = this.removeSpace(key);
            this.chestNames.add(name);
            final ItemStack item = new ItemStack(Material.CHEST);
            final ItemMeta im = item.getItemMeta();
            im.setDisplayName("§c" + key);
            item.setItemMeta(im);
            final int slot = this.getConfig().getInt("Chests." + key + ".slot");
            final List<String> cmds = (List<String>)this.getConfig().getStringList("Chests." + key + ".commands");
            final List<Integer> ids = (List<Integer>)this.getConfig().getIntegerList("Chests." + key + ".items");
            final List<Integer> perc = (List<Integer>)this.getConfig().getIntegerList("Chests." + key + ".percentages");
            final List<String> messages = (List<String>)this.getConfig().getStringList("Chests." + key + ".messages");
            final Chests chest = new Chests(key, item, slot, cmds, ids, perc, messages);
            this.chests.add(chest);
        }
    }
    
    public List<Chests> getChests() {
        return this.chests;
    }
    
    public List<String> getChestNames() {
        return this.chestNames;
    }
    
    public String removeSpace(String str) {
        while (str.contains(" ")) {
            str = str.replace(" ", "");
        }
        return str;
    }
    
    public static void save(Object o, File f){
    	try{
    		if (!f.exists())
    			f.createNewFile();

    		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
    		oos.reset();
    		oos.writeObject(o);
    		oos.flush();
    		oos.close();
    		} catch (Exception e){
    			e.printStackTrace();
    		}
    	
    	
    }
    
    public static Object load(File f){
    	try{
    		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
    		
    		Object result = ois.readObject();
    		ois.close();
    		return result;
    	}catch (Exception e){
    		return null;
    	}
    }
    public static String get(String type, int i){
    	if (type.toLowerCase().equals("main"))
    		return playerinv.get(i);
    	if (type.toLowerCase().equals("sub"))
    		return playersubinv.get(i);
    	return null;
    }
    public static int sizeget(String type){
    	if (type.toLowerCase().equals("main"))
    		return playerinv.size();
    	if (type.toLowerCase().equals("sub"))
    		return playersubinv.size();
    	return -100;
    }
    public static HashMap<Integer, String> get123(){
    		return tplocation;
    }
    public static int sizeget123(){
    	return tplocation.size();
    }
}
