package technium.treasurechest;

import org.bukkit.material.Attachable;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.material.Directional;
import net.minecraft.server.v1_8_R3.Packet;

import org.bukkit.craftbukkit.v1_8_R3.block.CraftChest;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockAction;
import net.minecraft.server.v1_8_R3.TileEntityChest;
import net.minecraft.server.v1_8_R3.BlockChest;
import net.minecraft.server.v1_8_R3.BlockPosition;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.Inventory;
import org.bukkit.command.CommandSender;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.mysql.jdbc.Field;

import org.bukkit.util.Vector;
import org.bukkit.inventory.InventoryHolder;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.Effect;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.Material;
import org.bukkit.Sound;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.EventHandler;
import java.util.Iterator;
import org.bukkit.event.player.PlayerPickupItemEvent;
import java.util.HashMap;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import org.bukkit.inventory.ItemStack;
import org.bukkit.block.Block;
import java.util.List;
import org.bukkit.Location;
import java.util.Map;

import javax.swing.JOptionPane;

import org.bukkit.event.Listener;

public class Events implements Listener
{
    Map<String, Location> addBack;
    Map<String, List<Block>>openingChests;
    Map<String, Integer> chestsOpened;
    Map<String, ItemStack> clickedType;
    Map<String, List<Integer>> used;
    Map<String, List<ItemStack>> noPickup;
    Map<String, List<Block>> copyChests;
    Map<String, List<Hologram>> holograms;
    Map<String, List<String>> commandsToExecute;
    Map<String, List<Item>> items;
    private TreasureChest plugin;
    
    static int NowOrNo;

    public static int GetStateAll(){
    	return NowOrNo;
    }
    
    
    public Events(final TreasureChest pl) {
        this.addBack = new HashMap<String, Location>();
        this.openingChests = new HashMap<String, List<Block>>();
        this.chestsOpened = new HashMap<String, Integer>();
        this.clickedType = new HashMap<String, ItemStack>();
        this.used = new HashMap<String, List<Integer>>();
        this.noPickup = new HashMap<String, List<ItemStack>>();
        this.copyChests = new HashMap<String, List<Block>>();
        this.holograms = new HashMap<String, List<Hologram>>();
        this.commandsToExecute = new HashMap<String, List<String>>();
        this.items = new HashMap<String, List<Item>>();
        this.plugin = pl;
    }
    
    @EventHandler
    public void onPlayerPickup(final PlayerPickupItemEvent e) {
        for (final List<ItemStack> list : this.noPickup.values()) {
            if (list.contains(e.getItem().getItemStack())) {
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void PlayerMoves(final PlayerMoveEvent event) {
        if (this.copyChests.containsKey(event.getPlayer().getName())) {
            final double fromX = event.getFrom().getX();
            final double fromY = event.getFrom().getY();
            final double fromZ = event.getFrom().getZ();
            final double toX = event.getTo().getX();
            final double toY = event.getTo().getY();
            final double toZ = event.getTo().getZ();
            if (fromX != toX) {
                event.getTo().setX(fromX);
            }
            if (fromY != toY) {
                event.getTo().setY(fromY);
            }
            if (fromZ != toZ) {
                event.getTo().setZ(fromZ);
            }
        }
        else {
            final Player player = event.getPlayer();
            for (final String s : this.copyChests.keySet()) {
                final List<Block> list = this.copyChests.get(s);
                for (final Block block : list) {
                    if ((event.getTo().getBlockX() == block.getLocation().getBlockX() && event.getTo().getBlockZ() == block.getLocation().getBlockZ()) || (event.getTo().getBlockX() == block.getLocation().getBlockX() + 1 && event.getTo().getBlockZ() == block.getLocation().getBlockZ()) || (event.getTo().getBlockX() == block.getLocation().getBlockX() - 1 && event.getTo().getBlockZ() == block.getLocation().getBlockZ()) || (event.getTo().getBlockX() == block.getLocation().getBlockX() && event.getTo().getBlockZ() == block.getLocation().getBlockZ() + 1) || (event.getTo().getBlockX() == block.getLocation().getBlockX() && event.getTo().getBlockZ() == block.getLocation().getBlockZ() - 1)) {
                        player.setVelocity(player.getEyeLocation().getDirection().multiply(-1));
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onClick(final InventoryClickEvent event) {
        if (!this.isChestItemStack(event.getCurrentItem())) {
            return;
        }
        final ItemStack item = event.getCurrentItem();
        final List<String> lore = (List<String>)item.getItemMeta().getLore();
        for (final String s : lore) {
            if (s.contains("Owned")) {
                final String[] split = s.split(" ");
                final int num = Integer.parseInt(split[split.length - 1]);
                if (num <= 0) {
                    event.getWhoClicked().closeInventory();
                    event.getWhoClicked().sendMessage("§cYou don't have any keys for this chest!");
                    return;
                }
                final Player pl = (Player)event.getWhoClicked();
                final String temp = split[1].substring(0, split[1].length() - 1);
                final String name = String.valueOf(split[0]) + temp;
                this.plugin.getConfig().set("Keys." + pl.getUniqueId() + "." + ChatColor.stripColor(name), (Object)(num - 1));
                this.plugin.saveConfig();
            }
        }
        final Player player = (Player)event.getWhoClicked();
        event.setCancelled(true);
        player.closeInventory();
        final Block b = this.getClosestBlock(player.getLocation(), 54, 5);
        final Block chest = player.getWorld().getBlockAt(new Location(player.getWorld(), (double)b.getX(), (double)(b.getY() + 1), (double)b.getZ()));
        this.addBack.put(player.getName(), chest.getLocation());
        this.clickedType.put(player.getName(), event.getCurrentItem());
        final BukkitScheduler sd = this.plugin.getServer().getScheduler();
        final List<Block> blocksToChange = new ArrayList<Block>();
        final List<Block> blocksToChange2 = new ArrayList<Block>();
        final Block block = player.getWorld().getBlockAt(chest.getLocation());
        chest.setType(Material.AIR);
        final Location l = new Location(player.getWorld(), chest.getX() + 0.5, (double)chest.getY(), chest.getZ() + 0.5);
        player.teleport(l);
        //Blocksing
        blocksToChange.add(player.getWorld().getBlockAt(new Location(player.getWorld(), (double)(block.getX() + 2), (double)block.getY(), (double)block.getZ())));
        blocksToChange.add(player.getWorld().getBlockAt(new Location(player.getWorld(), (double)(block.getX() - 2), (double)block.getY(), (double)block.getZ())));
        blocksToChange.add(player.getWorld().getBlockAt(new Location(player.getWorld(), (double)block.getX(), (double)block.getY(), (double)(block.getZ() + 2))));
        blocksToChange.add(player.getWorld().getBlockAt(new Location(player.getWorld(), (double)block.getX(), (double)block.getY(), (double)(block.getZ() - 2))));
        blocksToChange2.add(player.getWorld().getBlockAt(new Location(player.getWorld(), (double)(block.getX() + 2), (double)block.getY(), (double)block.getZ())));
        blocksToChange2.add(player.getWorld().getBlockAt(new Location(player.getWorld(), (double)(block.getX() - 2), (double)block.getY(), (double)block.getZ())));
        blocksToChange2.add(player.getWorld().getBlockAt(new Location(player.getWorld(), (double)block.getX(), (double)block.getY(), (double)(block.getZ() + 2))));
        blocksToChange2.add(player.getWorld().getBlockAt(new Location(player.getWorld(), (double)block.getX(), (double)block.getY(), (double)(block.getZ() - 2))));
        this.openingChests.put(player.getName(),blocksToChange);
        this.copyChests.put(player.getName(), blocksToChange2);
        sd.scheduleSyncDelayedTask((Plugin)this.plugin, (Runnable)new Runnable() {
            @Override
            public void run() {
            	NowOrNo = 1;
                blocksToChange.get(3).setType(Material.CHEST);
                Events.setFacing(blocksToChange.get(3), BlockFace.SOUTH);
                player.getWorld().playEffect(blocksToChange.get(3).getLocation(), Effect.STEP_SOUND, 4);
                sd.scheduleSyncDelayedTask((Plugin)Events.this.plugin, (Runnable)new Runnable() {
                    @Override
                    public void run() {
                        blocksToChange.get(0).setType(Material.CHEST);
                        Events.setFacing(blocksToChange.get(0), BlockFace.WEST);
                        player.getWorld().playEffect(blocksToChange.get(0).getLocation(), Effect.STEP_SOUND, 4);
                        sd.scheduleSyncDelayedTask((Plugin)Events.this.plugin, (Runnable)new Runnable() {
                            @Override
                            public void run() {
                                blocksToChange.get(2).setType(Material.CHEST);
                                Events.setFacing(blocksToChange.get(2), BlockFace.NORTH);
                                player.getWorld().playEffect(blocksToChange.get(2).getLocation(), Effect.STEP_SOUND, 4);
                                sd.scheduleSyncDelayedTask((Plugin)Events.this.plugin, (Runnable)new Runnable() {
                                    @Override
                                    public void run() {
                                        blocksToChange.get(1).setType(Material.CHEST);
                                        Events.setFacing(blocksToChange.get(1), BlockFace.EAST);
                                        player.getWorld().playEffect(blocksToChange.get(1).getLocation(), Effect.STEP_SOUND, 4);
                                        NowOrNo = 0;
                                    }
                                }, 20L);
                            }
                        }, 20L);
                    }
                }, 20L);
            }
        }, 20L);
    }
    
    public static Block blocking123;
    public static Block blocknor; 
    public static ItemStack[] chestinvt;
    @EventHandler
    public void onInteract(final PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getClickedBlock().getType() != Material.CHEST) {
            return;
        }
        if (NowOrNo == 1){
    		HumanEntity play = event.getPlayer();
    		play.sendMessage("Wait Until All Blocks are Placed");
    		event.setCancelled(true);
    		return;
    	}
        final Block block = event.getClickedBlock();
        blocking123 = block;
        final Player player2 = event.getPlayer();
        String X = Integer.toString(block.getX());
        String Y = Integer.toString(block.getY());
        String Z = Integer.toString(block.getZ());
        final Location blockUnder = new Location(player2.getWorld(), (double)block.getX(), (double)(block.getY() - 1), (double)block.getZ());
        final Location blockits = new Location(player2.getWorld(), (double)block.getX(), (double)block.getY(), (double)block.getZ());
        String BlockLocation = X + "," + Y + "," + Z;
        
        
        CraftChest ches123 = (CraftChest) block.getState();
        
        int OutcomeSubLocation = 0;
        int OutcomeLocation = 0;
        int Outcome123 = 0;
        //player2.sendMessage(BlockLocation);
        for (int i = 1; i <= TreasureChest.sizeget("main"); i++){
        	
        	//player2.sendMessage(TreasureChest.get(i));
        	String get = TreasureChest.get("main", i);
        	if (BlockLocation.equals(get)){
        		OutcomeLocation = 1;
        		//player2.sendMessage(BlockLocation + " = " + get);
        	}
        }
        for (int i = 1; i <= TreasureChest.sizeget("sub"); i++){
        	
        	//player2.sendMessage(TreasureChest.get("sub", i));
        	String get = TreasureChest.get("sub",i);
        	if (BlockLocation.equals(get)){
        		OutcomeSubLocation = 1;
        		//player2.sendMessage(BlockLocation + " = " + get);
        	}
        }
        if (OutcomeLocation == 1 || OutcomeSubLocation == 1){
        	Outcome123 = 1;
        }
        if (Outcome123 == (int)0){
        	return;
        }
        
        
        if (player2.getWorld().getBlockAt(blockUnder).getType() != Material.AIR ) {
        	String Name = "";
        	try{
        	java.lang.reflect.Field inventoryField = ches123.getClass().getDeclaredField("chest");
            inventoryField.setAccessible(true);
            TileEntityChest teChest123 = ((TileEntityChest) inventoryField.get(ches123));
            Name = teChest123.getName();
        	}catch (Exception ex){
        		return;
        	}
        	
        	
        	
        	
        	if (Name.equals("TE")){
        		blocknor = event.getClickedBlock(); 
        		event.setCancelled(true);
        		final InventoryHolder Creates = null;
        		final Inventory inv = Bukkit.createInventory((InventoryHolder)Creates, 45, "Treasure Chest");
        		
        		for (final Chests chest : this.plugin.getChests()) {
        			final ItemStack item = chest.getItemStack();
        			final ItemMeta im = item.getItemMeta();
        			final List<String> lore = new ArrayList<String>();
        			int num = 0;
        			try {
        				final String remove = this.plugin.removeSpace(chest.getName());
        				num = this.plugin.getConfig().getInt("Keys." + player2.getUniqueId() + "." + remove);
        			}
        			catch (Exception ex) {}
        			lore.add("");
        			lore.add("§7" + chest.getName() + "s Owned: " + num);
        			lore.add("");
        			im.setLore((List)lore);
        			item.setItemMeta(im);
        			inv.setItem(chest.getSlot(), item);
        		}
        		//player2.playNote(blockits, (byte)1, (byte)1);
                final BlockPosition position = new BlockPosition(blockits.getBlockX(), blockits.getBlockY(), blockits.getBlockZ());
                final PacketPlayOutBlockAction blockActionPacket = new PacketPlayOutBlockAction(position, net.minecraft.server.v1_8_R3.Block.getById(blockits.getBlock().getTypeId()), 1, 1);
                ((CraftPlayer)player2).getHandle().playerConnection.sendPacket((Packet)blockActionPacket);
                player2.playSound(blockits, Sound.CHEST_OPEN, 1, 1);
        		player2.openInventory(inv);
        		
        		
            
            
        }
        }
        if (player2.getWorld().getBlockAt(blockUnder).getType() == Material.AIR ) {
        	return;
        }
        final Location conditional = new Location(player2.getWorld(), (double)block.getX(), (double)(block.getY() - 3), (double)block.getZ());
        if(player2.getWorld().getBlockAt(conditional).getType() != Material.NETHER_BRICK){
        	OutcomeSubLocation = 0;
        	for (int i = 1; i <= TreasureChest.sizeget("sub"); i++){
            	
            	//player2.sendMessage(TreasureChest.get("sub", i));
            	String get = TreasureChest.get("sub",i);
            	if (BlockLocation.equals(get)){
            		OutcomeSubLocation = 1;
            		//player2.sendMessage(BlockLocation + " = " + get);
            	}
            }
        	if (OutcomeSubLocation == 0)
        		return;
        	
        }

  
        
        
        boolean contains = false;
        String key = "";
        for (String name : this.openingChests.keySet()) {
            if (this.openingChests.get(name).contains(event.getClickedBlock()) && name.equalsIgnoreCase(player2.getName())) {
                key = name;
                contains = true;
            }
        }
        if (contains) {
        	player2.sendMessage("FUCK YOU BITCH");
            final Player player3 = event.getPlayer();
            final List<Block> blocks = this.openingChests.get(player3.getName());
            blocks.remove(event.getClickedBlock());
            this.openingChests.put(player3.getName(), blocks);
            boolean hi = this.chestsOpened.containsKey(player3.getName());
            event.setCancelled(true);
            final ItemStack item2 = this.clickedType.get(event.getPlayer().getName());
            final List<String> commands = this.getCommandsFromItem(item2);
            final List<Integer> ids = this.getItemsFromItem(item2);
            final List<Integer> percent = this.getPercentagesFromItem(item2);
            final List<String> messages = this.getMessagesFromItem(item2);
            if (!this.chestsOpened.containsKey(player3.getName())) {
                this.chestsOpened.put(player3.getName(), 0);
                player2.sendMessage("1");
            }
            int rnd = (int)(Math.random() * 100.0) + 1;
            int number = 0;
            int index = 0;
            for (int i = 0; i < percent.size(); ++i) {
                if (rnd > number && rnd < number + percent.get(i)) {
                    index = i;
                    break;
                }
                number += percent.get(i);
            }
            if (!this.used.containsKey(player3.getName())) {
            	player2.sendMessage("2");
                final List<Integer> list = new ArrayList<Integer>();
                list.add(ids.get(index));
                this.used.put(player3.getName(), list);
                this.chestsOpened.put(player3.getName(), this.chestsOpened.get(player3.getName()) + 1);
                changeChestState(event.getClickedBlock().getLocation(), true);
                final ItemStack j = new ItemStack((int)ids.get(index));
                if (!this.noPickup.containsKey(player3.getName())) {
                	player2.sendMessage("3");
                	//layer2.sendMessage("Godd Blesss");
                    final List<ItemStack> items = new ArrayList<ItemStack>();
                    items.add(j);
                    this.noPickup.put(key, items);
                }
                else {
                	player2.sendMessage("4");
                	//player2.sendMessage("Godd Blesss");
                    final List<ItemStack> items = this.noPickup.get(player3.getName());
                    items.add(j);
                    this.noPickup.put(key, items);
                }
                final Location l = event.getClickedBlock().getLocation();
                final Location up1 = new Location(l.getWorld(), l.getX() + 0.5, l.getY() + 1.0, l.getZ() + 0.5);
                final Item drop = event.getClickedBlock().getWorld().dropItem(up1, j);
                drop.setVelocity(new Vector(0, 0, 0));
                drop.teleport(up1);
                drop.setPickupDelay(999);
                if (this.items.containsKey(player3.getName())) {
                	player2.sendMessage("5");
                	//player2.sendMessage("Godd Blesss");
                    final List<Item> li = this.items.get(player3.getName());
                    li.add(drop);
                    this.items.put(player3.getName(), li);
                }
                else {
                	player2.sendMessage("6");
                	//player2.sendMessage("Godd Blesss");
                    final List<Item> li = new ArrayList<Item>();
                    li.add(drop);
                    this.items.put(player3.getName(), li);
                }
                final Hologram h = HologramsAPI.createHologram((Plugin)this.plugin, new Location(l.getWorld(), l.getX() + 0.5, l.getY() + 2.0, l.getZ() + 0.5));
                h.appendTextLine(ChatColor.translateAlternateColorCodes('&', (String)messages.get(index)));
                if (this.commandsToExecute.containsKey(player3.getName())) {
                	player2.sendMessage("7");
                    final List<String> cmds = this.commandsToExecute.get(player3.getName());
                    cmds.add(commands.get(index));
                    this.commandsToExecute.put(player3.getName(), cmds);
                }
                else {
                	player2.sendMessage("8");
                    final List<String> cmds = new ArrayList<String>();
                    cmds.add(commands.get(index));
                    this.commandsToExecute.put(player3.getName(), cmds);
                }
                if (this.holograms.containsKey(player3.getName())) {
                	player2.sendMessage("9");
                    final List<Hologram> lit = this.holograms.get(player3.getName());
                    lit.add(h);
                    this.holograms.put(player3.getName(), lit);

                }
                else {
                	player2.sendMessage("10");
                    final List<Hologram> lit = new ArrayList<Hologram>();
                    lit.add(h);
                    this.holograms.put(player3.getName(), lit);
                }
            }
            else {
            	player2.sendMessage("11");
                while (this.used.get(player3.getName()).contains(ids.get(index))) {
                    rnd = (int)(Math.random() * 100.0) + 1;
                    number = 0;
                    index = 0;
                    for (int i = 0; i < percent.size(); ++i) {
                        if (rnd > number && rnd < number + percent.get(i)) {
                            index = i;
                            break;
                        }
                        number += percent.get(i);
                    }
                }
                final List<Integer> k = this.used.get(player3.getName());
                k.add(ids.get(index));
                this.used.put(player3.getName(), k);
                this.chestsOpened.put(player3.getName(), this.chestsOpened.get(player3.getName()) + 1);
                changeChestState(event.getClickedBlock().getLocation(), true);
                final ItemStack j = new ItemStack((int)ids.get(index));
                if (!this.noPickup.containsKey(player3.getName())) {
                	player2.sendMessage("12");
                    final List<ItemStack> items = new ArrayList<ItemStack>();
                    items.add(j);
                    this.noPickup.put(key, items);
                }
                else {
                	player2.sendMessage("13");
                    final List<ItemStack> items = this.noPickup.get(player3.getName());
                    items.add(j);
                    this.noPickup.put(key, items);
                }
                final Location loc = event.getClickedBlock().getLocation();
                final Location up1 = new Location(loc.getWorld(), loc.getX() + 0.5, loc.getY() + 1.0, loc.getZ() + 0.5);
                final Item drop = event.getClickedBlock().getWorld().dropItem(up1, j);
                drop.setVelocity(new Vector(0, 0, 0));
                drop.teleport(up1);
                drop.setPickupDelay(999);
                if (this.items.containsKey(player3.getName())) {
                	player2.sendMessage("14");
                    final List<Item> li = this.items.get(player3.getName());
                    li.add(drop);
                    this.items.put(player3.getName(), li);
                }
                else {
                	player2.sendMessage("15");
                    final List<Item> li = new ArrayList<Item>();
                    li.add(drop);
                    this.items.put(player3.getName(), li);
                }
                final Hologram h = HologramsAPI.createHologram((Plugin)this.plugin, new Location(loc.getWorld(), loc.getX() + 0.5, loc.getY() + 2.0, loc.getZ() + 0.5));
                //player2.sendMessage("Godd Blesss");
                h.appendTextLine(ChatColor.translateAlternateColorCodes('&', (String)messages.get(index)));
                if (this.commandsToExecute.containsKey(player3.getName())) {
                	player2.sendMessage("16");
                    final List<String> cmds = this.commandsToExecute.get(player3.getName());
                    cmds.add(commands.get(index));
                    this.commandsToExecute.put(player3.getName(), cmds);
                }
                else {
                	player2.sendMessage("17");
                    final List<String> cmds = new ArrayList<String>();
                    cmds.add(commands.get(index));
                    this.commandsToExecute.put(player3.getName(), cmds);
                }
                if (this.holograms.containsKey(player3.getName())) {
                	player2.sendMessage("18");
                    final List<Hologram> list2 = this.holograms.get(player3.getName());
                    list2.add(h);
                    this.holograms.put(player3.getName(), list2);
                }
                else {
                	player2.sendMessage("19");
                    final List<Hologram> list2 = new ArrayList<Hologram>();
                    list2.add(h);
                    this.holograms.put(player3.getName(), list2);
                }
                if (this.chestsOpened.get(player3.getName()) == 4) {
                	player2.sendMessage("20");
                    final BukkitScheduler sd = this.plugin.getServer().getScheduler();
                    sd.scheduleSyncDelayedTask((Plugin)this.plugin, (Runnable)new Runnable() {
                        @Override
                        public void run() {
                            Events.this.used.remove(player3.getName());
                            Events.this.chestsOpened.remove(player3.getName());
                            final Location l = Events.this.addBack.get(player3.getName());
                            //**
                            Location TpLocation = null;
                            String Cords = blocknor.getX() + "," + blocknor.getY() + "," + blocknor.getZ();
                            int Turth = 1;
                            if (Shorten.TpLocationHash(Cords, "x") == Boolean.FALSE){
                				TpLocation = new Location(l.getWorld(), l.getX() + 1.0, l.getY(), l.getZ() + 1.0);
                				
                				Turth = 0;
                			}
                            if (Turth == (int)1){
	                        	int X = (int) Shorten.TpLocationHash(Cords, "x");
	                        	int Y = (int) Shorten.TpLocationHash(Cords, "y");
	                        	int Z = (int) Shorten.TpLocationHash(Cords, "z");
	                        	TpLocation = new Location(l.getWorld(), X, Y, Z); 
                            }
                            player3.teleport(TpLocation);
                            player3.sendMessage(TpLocation.toString());
                            final Block b = l.getBlock();
                            b.setType(Material.CHEST);
                            if (b.getType() == Material.CHEST) {
                            	  Chest STATECHEST = (Chest) b.getState();
                            	  CraftChest BukkitChest = (CraftChest) STATECHEST;
                            	  TileEntityChest TECHEST = BukkitChest.getTileEntity();
                            	  TECHEST.a("TE");
                            	}
                            Events.this.addBack.remove(player3.getName());
                            final List<Block> list = Events.this.copyChests.get(player3.getName());
                            for (final Block bl : list) {
                            	//bl.meta
                                bl.setType(Material.AIR);
                                bl.getWorld().playEffect(bl.getLocation(), Effect.STEP_SOUND, 4);
                            }
                            for (final Hologram h : Events.this.holograms.get(player3.getName())) {
                                h.delete();
                            }
                            for (final Item k : Events.this.items.get(player3.getName())) {
                                k.remove();
                            }
                            Events.this.items.remove(player3.getName());
                            Events.this.holograms.remove(player3.getName());
                            Events.this.openingChests.remove(player3.getName());
                            Events.this.copyChests.remove(player3.getName());
                            Events.this.clickedType.remove(player3.getName());
                            final List<ItemStack> no = Events.this.noPickup.get(player3.getName());
                            for (final ItemStack i : no) {
                                i.setAmount(0);
                            }
                            Events.this.noPickup.remove(player3.getName());
                            for (String cmd : Events.this.commandsToExecute.get(player3.getName())) {
                                final String s = cmd;
                                while (cmd.contains("%name%")) {
                                    cmd = cmd.replace("%name%", player3.getName());
                                }
                                Bukkit.getServer().dispatchCommand((CommandSender)Bukkit.getConsoleSender(), cmd);
                                //JOptionPane.showMessageDialog(null, cmd);
                            }
                            Events.this.commandsToExecute.remove(player3.getName());
                            
                            
                        }
                    }, 40L);
                }
            }
            return;
        }
        event.setCancelled(true);
    }
    
    @EventHandler
    public void onClose(final InventoryCloseEvent event){
    	int curinv = event.getView().countSlots();
    	int need = 81;

    	HumanEntity player = event.getPlayer();
    	if (need == curinv){
    		if (event.getInventory().getName().equals("Treasure Chest"))
    		{
    	    int X = blocking123.getX();
    	    int Y = blocking123.getY();
    	    int Z = blocking123.getZ();
    		final Location blockits = new Location(player.getWorld(), (double)X, (double)Y, (double)Z);
    		((CraftPlayer) player).playSound(blockits, Sound.CHEST_CLOSE, 2, 1);
    		final BlockPosition position = new BlockPosition(X, Y, Z);
            final PacketPlayOutBlockAction blockActionPacket = new PacketPlayOutBlockAction(position, net.minecraft.server.v1_8_R3.Block.getById(blockits.getBlock().getTypeId()), 1, 0);
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket((Packet)blockActionPacket);
    		}
    	//if (event.getInventory().getName() != "Treasure Chest")
    		//return;
    	}
    	}
    
    
    public static Block blocking(){
    	return blocking123;
    }
    
    public boolean isChestItemStack(final ItemStack i) {
        for (final Chests chest : this.plugin.getChests()) {
            if (chest.getItemStack().equals((Object)i)) {
                return true;
            }
        }
        return false;
    }
    
    public List<String> getCommandsFromItem(final ItemStack i) {
        for (final Chests chest : this.plugin.getChests()) {
            if (chest.getItemStack().equals((Object)i)) {
                return chest.getCommands();
            }
        }
        return null;
    }
    
    public List<String> getMessagesFromItem(final ItemStack i) {
        for (final Chests chest : this.plugin.getChests()) {
            if (chest.getItemStack().equals((Object)i)) {
                return chest.getMessages();
            }
        }
        return null;
    }
    
    public List<Integer> getPercentagesFromItem(final ItemStack i) {
        for (final Chests chest : this.plugin.getChests()) {
            if (chest.getItemStack().equals((Object)i)) {
                return chest.getPercentages();
            }
        }
        return null;
    }
    
    public List<Integer> getItemsFromItem(final ItemStack i) {
        for (final Chests chest : this.plugin.getChests()) {
            if (chest.getItemStack().equals((Object)i)) {
                return chest.getIds();
            }
        }
        return null;
    }
    
    public Block getClosestBlock(final Location location, final int id, final int Radius) {
        for (int X = location.getBlockX() - Radius; X <= location.getBlockX() + Radius; ++X) {
            for (int Y = location.getBlockY() - Radius; Y <= location.getBlockY() + Radius; ++Y) {
                for (int Z = location.getBlockZ() - Radius; Z <= location.getBlockZ() + Radius; ++Z) {
                    final Block block = location.getWorld().getBlockAt(X, Y, Z);
                    if (block.getTypeId() == id) {
                    	final Block block123 = location.getWorld().getBlockAt(X, Y-1, Z);
                        return block123;
                    }
                }
            }
        }
        return null;
    }
    
    public static void changeChestState(final Location loc, final boolean open) {
        final byte dataByte = (byte)(open ? 1 : 0);
        for (final Player player : Bukkit.getOnlinePlayers()) {
            player.playNote(loc, (byte)1, dataByte);
            final BlockPosition position = new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
            final PacketPlayOutBlockAction blockActionPacket = new PacketPlayOutBlockAction(position, net.minecraft.server.v1_8_R3.Block.getById(loc.getBlock().getTypeId()), 1, (int)dataByte);
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket((Packet)blockActionPacket);
        }
    }
    
    public static void setFacing(final Block block, final BlockFace facing) {
        final MaterialData data = getData(block);
        if (data != null && data instanceof Directional) {
            ((Directional)data).setFacingDirection(facing);
            setData(block, data, true);
        }
    }
    
    public static MaterialData getData(final Block block) {
        return getData(getTypeId(block), getRawData(block));
    }
    
    public static void setData(final Block block, final MaterialData materialData, final boolean doPhysics) {
        block.setData(materialData.getData(), doPhysics);
    }
    
    public static int getTypeId(final Block block) {
        return block.getTypeId();
    }
    
    public static int getRawData(final Block block) {
        return block.getData();
    }
    
    public static MaterialData getData(final int typeId, final int rawData) {
        final Material type = Material.getMaterial(typeId);
        return (type == null) ? new MaterialData(typeId, (byte)rawData) : getData(type, rawData);
    }
    
    public static MaterialData getData(final Material type, final int rawData) {
        if (type == null) {
            return new MaterialData(0, (byte)rawData);
        }
        final MaterialData mdata = type.getNewData((byte)rawData);
        if (mdata instanceof Attachable) {
            final Attachable att = (Attachable)mdata;
            if (att.getAttachedFace() == null) {
                att.setFacingDirection(BlockFace.NORTH);
            }
        }
        return mdata;
    }
}