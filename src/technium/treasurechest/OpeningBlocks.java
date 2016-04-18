package technium.treasurechest;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class OpeningBlocks implements Listener {

	public OpeningBlocks(final TreasureChest pl) {
		// TODO Auto-generated constructor stub
	}
	
	static List<String> list = new ArrayList<String>();
	
	@EventHandler
    public void onClick(final PlayerInteractEvent event){
		int Case1 = 0;
		int Case2 = 0;
		int Outcome = 0;
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_AIR)
			return;
		if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
            Case1 = 1;
        }
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK){
			if (event.getClickedBlock().getType() == Material.CHEST){
				Case2 = 0;
			}
			else{
				Case2 = 1;
			}
			
		}
		
		if (Case1 == 0 || Case2 == 0){
			Outcome  = 1;
		}
		if (Outcome == 0){
			return;
		}
		HumanEntity player = event.getPlayer();
		Block block = event.getClickedBlock();
		String name = player.getName();
		String curname;
		int X = block.getX();
		int Y = block.getY();
		int Z = block.getZ();
		if (!list.isEmpty()){
			for (int i = 0;i < list.size();i++){
				curname = list.get(i);
				if (curname.contains(name)){
					list.remove(i);
					break;
				}
			}
		}
		
		
		String setup = player.getName() + "%" + X + "," + Y + "," + Z;
		list.add(setup);
		return;
	}
	
	public static List<String> geting(){
		return list;
	}

}
