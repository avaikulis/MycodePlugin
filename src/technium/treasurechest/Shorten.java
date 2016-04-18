package technium.treasurechest;

import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

public class Shorten {
	
	public static Object getXYZ(HumanEntity sender,String type){
		List<String>listing = OpeningBlocks.geting();
		String XYZ = null;
		int Turth = 0;
		//sender.sendMessage(Integer.toString(listing.size()));
		for (int i = 0;i < listing.size();i++){
			if (listing.get(i).contains(sender.getName())){
				String noneed = listing.get(i);
				XYZ = noneed.replaceAll(sender.getName(), "");
				String noneed1 = XYZ;
				XYZ = noneed1.replaceAll("%", "");
				Turth = 1;
				break;
			}
			
		}
		if (Turth == 0){
			return false;
		}
		XYZ = XYZ.replaceAll(",", " ");
		XYZ = XYZ + " ";
		String Char;
		String Temp = "";
		Object X1 = 0;
		Object Y1 = 0;
		Object Z1 = 0;
		int what = 1;
		for(int i = 0;i < XYZ.length();i++){
			Char = Character.toString(XYZ.charAt(i));
			if (Character.isDigit(XYZ.charAt(i) ) || Char.equals("-")){
				Temp = Temp + Char;
			}
			if (Character.isSpaceChar(XYZ.charAt(i))){
				if (what == 1){
					X1 = Integer.parseInt(Temp);
					Temp = "";
				}
				if (what == 2){
					Y1 = Integer.parseInt(Temp);
					Temp = "";
				}
				if (what == 3){
					Z1 = Integer.parseInt(Temp);
					Temp = "";
				}
				what++;
			}
			
		}
		if (type.toLowerCase().equals("x")){
			return X1;
		}
		if (type.toLowerCase().equals("y")){
			return Y1;
		}
		if (type.toLowerCase().equals("z")){
			return Z1;
		}
		return null;
	}
	public static boolean AlphaSpecialCheck(String Checking){
		if (Checking.equals("a")
				|| 
				Checking.equals("b")
				||
				Checking.equals("c")
				||
				Checking.equals("d")
				||
				Checking.equals("e")
				||
				Checking.equals("f")
				||
				Checking.equals("g")
				||
				Checking.equals("h")
				||
				Checking.equals("i")
				||
				Checking.equals("g")
				||
				Checking.equals("k")
				||
				Checking.equals("l")
				||
				Checking.equals("m")
				||
				Checking.equals("n")
				||
				Checking.equals("o")
				||
				Checking.equals("p")
				||
				Checking.equals("q")
				||
				Checking.equals("r")
				||
				Checking.equals("s")
				||
				Checking.equals("t")
				||
				Checking.equals("u")
				||
				Checking.equals("v")
				||
				Checking.equals("w")
				||
				Checking.equals("x")
				||
				Checking.equals("y")
				||
				Checking.equals("z")
				||
				Checking.equals("%")
				||
				Checking.equals("$")
				||
				Checking.equals('"')
				||
				Checking.equals("*")
				||
				Checking.equals("&")
				||
				Checking.equals("!")
				||
				Checking.equals("£")
				||
				Checking.equals("^")
				||
				Checking.equals("(")
				||
				Checking.equals(")")
				||
				Checking.equals("+")
				||
				Checking.equals("=")
				||
				Checking.equals("_")
				||
				Checking.equals("{")
				||
				Checking.equals("}")
				||
				Checking.equals("[")
				||
				Checking.equals("]")
				||
				Checking.equals(":")
				||
				Checking.equals(";")
				||
				Checking.equals("@")
				||
				Checking.equals("'")
				||
				Checking.equals("#")
				||
				Checking.equals("~")
				||
				Checking.equals("<")
				||
				Checking.equals(">")
				||
				Checking.equals(".")
				||
				Checking.equals("?")
				||
				Checking.equals("/")
				||
				Checking.equals("\\")
				||
				Checking.equals("|")){
			return true;
			
		}
		return false;
	}
	public static Object TpLocationHash(String Cords,String Type){

		HashMap<Integer, String>listing = TreasureChest.get123();
		String XYZ = null;
		int Turth = 0;
		//sender.sendMessage(Integer.toString(listing.size()));
		for (int i = 1;i <= listing.size();i++){
			if (listing.get(i).contains((Cords + "%"))){
				String noneed = listing.get(i);
				XYZ = noneed.replaceAll((Cords + "%"), "");
				Turth = 1;
				break;
			}
		
		}
		if (Turth == 0){
			
			return false;
		}
		XYZ = XYZ.replaceAll(",", " ");
		XYZ = XYZ + " ";
		String Char;
		String Temp = "";
		Object X1 = 0;
		Object Y1 = 0;
		Object Z1 = 0;
		int what = 1;
		for(int i = 0;i < XYZ.length();i++){
			Char = Character.toString(XYZ.charAt(i));
			if (Character.isDigit(XYZ.charAt(i) ) || Char.equals("-")){
				Temp = Temp + Char;
			}
			if (Character.isSpaceChar(XYZ.charAt(i))){
				if (what == 1){
					X1 = Integer.parseInt(Temp);
					Temp = "";
				}
				if (what == 2){
					Y1 = Integer.parseInt(Temp);
					Temp = "";
				}
				if (what == 3){
					Z1 = Integer.parseInt(Temp);
					Temp = "";
				}
				what++;
			}
			
		}
		if (Type.toLowerCase().equals("x")){
			return X1;
		}
		if (Type.toLowerCase().equals("y")){
			return Y1;
		}
		if (Type.toLowerCase().equals("z")){
			return Z1;
		}
		return null;
	
	}

}
