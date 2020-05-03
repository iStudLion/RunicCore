package aw.rmjtromp.RunicCore.utilities;

import java.util.Random;
import java.util.UUID;

import org.bukkit.Location;

import aw.rmjtromp.RunicCore.RunicCore;

public class RunicUtils {
	
	private final static RunicCore plugin = RunicCore.getInstance();
    private static Random random = new Random();
	
	public static String generateRandomWord() {
		return generateRandomWords(1)[0];
	}
	
	public static int generateRandomNumber(int min, int max) {
		return random.nextInt(max-min) + min;
	}
	
	public static String[] generateRandomWords(int amount) {
	    String[] randomStrings = new String[amount];
	    for(int i = 0; i < amount; i++) {
	        char[] word = new char[random.nextInt(8)+3]; // words of length 3 through 10. (1 and 2 letter words are boring.)
	        for(int j = 0; j < word.length; j++) {
	            word[j] = (char)('a' + random.nextInt(26));
	        }
	        randomStrings[i] = new String(word);
	    }
	    return randomStrings;
	}
	
	public static String generateRandomString() {
		return generateRandomString(16);
	}
	
	public static String generateRandomString(int length) {
	    int leftLimit = 48; // numeral '0'
	    int rightLimit = 122; // letter 'z'
	 
	    String generatedString = random.ints(leftLimit, rightLimit + 1)
	      .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
	      .limit(length)
	      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
	      .toString();
	 
	    return generatedString;
	}
	
	public static Location str2loc(String str) {
		return str2loc(str, true);
	}

	public static Location str2loc(String str, boolean round) {
		if(!str.isEmpty() ) {
			String[] str2loc = str.split(";");
			if(str2loc.length == 4 || str2loc.length == 6) {
				Location loc = new Location(plugin.getServer().getWorld(str2loc[0]), 0.0D, 0.0D, 0.0D);
				loc.setX(Double.parseDouble(str2loc[1]));
				loc.setY(Double.parseDouble(str2loc[2]));
			    loc.setZ(Double.parseDouble(str2loc[3]));
			    if(str2loc.length == 6) {
			    	loc.setYaw(Float.parseFloat(str2loc[4]));
			    	loc.setPitch(Float.parseFloat(str2loc[5]));
			    }
			    return round ? roundLocation(loc) : loc;
			}
		}
		return null;
	}
	
	public static String loc2str(Location loc) {
		return loc2str(loc, true);
	}
	  
	public static String loc2str(Location loc, boolean round) {
		if(loc != null) {
			if(round) {
				Location location = roundLocation(loc);
				return location.getWorld().getName() + ";" + location.getX() + ";" + location.getY() + ";" + location.getZ() + ";" + location.getYaw() + ";" + location.getPitch();
			} else return loc.getWorld().getName() + ";" + loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + ";" + loc.getYaw() + ";" + loc.getPitch();
		}
		return null;
	}
	
	public static Location roundLocation(Location loc) {
		return new Location(loc.getWorld(), roundToHalf(loc.getX()), roundToHalf(loc.getY()), roundToHalf(loc.getZ()), roundToNearest45Deg(loc.getYaw()), roundToNearest45Deg(loc.getPitch()));
		
	}
	
	public static UUID stringToUUID(String string) {
		if(string.matches("^(\\b[0-9a-f]{8}[0-9a-f]{4}[0-9a-f]{4}[0-9a-f]{4}[0-9a-f]{12}\\b|\\b[0-9a-f]{8}\\b-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-\\b[0-9a-f]{12}\\b)$")) {
			String stringUUID = "";
			if(string.matches("\\b[0-9a-f]{8}[0-9a-f]{4}[0-9a-f]{4}[0-9a-f]{4}[0-9a-f]{12}\\b")) {
				string = string.substring(0, 8) + "-" + string.substring(8, string.length());
				string = string.substring(0, 13) + "-" + string.substring(13, string.length());
				string = string.substring(0, 18) + "-" + string.substring(18, string.length());
				string = string.substring(0, 23) + "-" + string.substring(23, string.length());
		 		
		 		stringUUID = string;
			} else stringUUID = string;
			
			if(stringUUID.length() == 36) {
				try {
					UUID uuid = UUID.fromString(stringUUID);
					return uuid;
				} catch(IllegalArgumentException e) {
					//
				}
			}
		}
		return null;
	}
	
	private static double roundToHalf(double d) {
	    return Math.round(d * 2) / 2.0;
	}
	
	private static float roundToNearest45Deg(float f) {
		return Math.round(f * 45) / 45;
	}
	
}
