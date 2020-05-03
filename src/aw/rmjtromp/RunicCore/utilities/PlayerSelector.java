package aw.rmjtromp.RunicCore.utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import aw.rmjtromp.RunicCore.core.other.extensions.RunicPlayer;

public final class PlayerSelector {
	
	private static Random r = new Random();
	
	public static List<RunicPlayer> select(String selector) {
		return select(selector, false, false);
	}
	
	public static List<RunicPlayer> select(String selector, boolean includeVanished) {
		return select(selector, includeVanished, false);
	}

	public static List<RunicPlayer> select(String selector, boolean includeVanished, boolean multiple) {
		List<RunicPlayer> selected = new ArrayList<RunicPlayer>();
		
		if(selector.matches("^(?:\\*{1,2}|@[aA](?:\\[[^\\[\\]]{0,}\\])?)$")) {
			if(includeVanished) for(Player player : Bukkit.getOnlinePlayers()) selected.add(RunicPlayer.cast(player));
			else {
				for(Player player : Bukkit.getOnlinePlayers()) {
					RunicPlayer RP = RunicPlayer.cast(player);
					if(!RP.isVanished()) selected.add(RP);
				}
			}
			
			// @TODO continue working on @a[] selectors
			/*
			 
			 if(selector.matches("^@[aA]\\[[^\\[\\]]{0,}\\]$")) {
				// Flags:
				// r = radius
				// world = player's world
				// x = x coordinate
				// y = y coordinate
				// z = z coordinate
				// rank = player's rank
				// gamemode = player's gamemode
				// name = player's name
				// hasPermission = if player has permission
				
				// Operators:
				// = = equal
				// != = not equal
				// < = less than
				// > = bigger than
				// >= = bigger or equal to
				// <= = less or equal to
				
				Pattern pattern = Pattern.compile("^@[aA]\\[([^\\[\\]]{0,})\\]$", Pattern.CASE_INSENSITIVE);
				Matcher matcher = pattern.matcher(selector);
				
				if(matcher.find() || !matcher.group(1).isEmpty()) {
					String[] flags = matcher.group(1).split(",");
					for(String flag : flags) {
						
					}
				} else {
					// just select all, @a[] is empty
					if(includeVanished) for(Player player : Bukkit.getOnlinePlayers()) selected.add(RunicPlayer.cast(player));
					else {
						for(Player player : Bukkit.getOnlinePlayers()) {
							RunicPlayer RP = RunicPlayer.cast(player);
							if(!RP.isVanished()) selected.add(RP);
						}
					}
				}
			} else {
				if(includeVanished) for(Player player : Bukkit.getOnlinePlayers()) selected.add(RunicPlayer.cast(player));
				else {
					for(Player player : Bukkit.getOnlinePlayers()) {
						RunicPlayer RP = RunicPlayer.cast(player);
						if(!RP.isVanished()) selected.add(RP);
					}
				}
			}*/
		} else if(selector.matches("^@[rR]$")) {
			if(includeVanished) {
				int i = r.nextInt((Bukkit.getOnlinePlayers().size()-1));
				selected.add(RunicPlayer.cast(Bukkit.getOnlinePlayers().toArray()[i]));
			} else {
				List<RunicPlayer> unvanishedPlayers = new ArrayList<RunicPlayer>();
				for(Player player : Bukkit.getOnlinePlayers()) {
					RunicPlayer RP = RunicPlayer.cast(player);
					if(!RP.isVanished()) unvanishedPlayers.add(RP);
				}
				
				if(unvanishedPlayers.size() > 0) {
					int i = r.nextInt(unvanishedPlayers.size()-1);
					selected.add(unvanishedPlayers.get(i));
				}
			}
		} else if(selector.matches("^\\w{1,16}$")) {
			RunicPlayer player = RunicPlayer.cast(Bukkit.getPlayerExact(selector));
			if(player != null) {
				if(player.isOnline()) {
					if(includeVanished) selected.add(player);
					else if(!player.isVanished()) selected.add(player);
				}
			} else {
				List<String> playerNames = new ArrayList<String>();
				// adds all players (depends on vanished ones are included)
				for(Player p : Bukkit.getOnlinePlayers()) {
					if(includeVanished) playerNames.add(p.getName());
					else if(!RunicPlayer.cast(p).isVanished()) playerNames.add(p.getName());
				}
				
				// calculates the distance between selector and player name
				HashMap<String, Double> results = new HashMap<String, Double>();
				if(playerNames.size() > 0) {
					for(String playerName : playerNames) {
						double distance = Levenshtein.similarity(selector, playerName)*100;
						if(distance >= 80) results.put(playerName, distance);
					}
				}
				
				if(results.size() > 0) {
					// sorts the results by most similar player name
					entriesSortedByValues(results);
					if(multiple) {
						for(String playerName : results.keySet()) {
							selected.add(RunicPlayer.cast(Bukkit.getPlayerExact(playerName)));
						}
					} else {
						// adds the first result to the selected list
						selected.add(RunicPlayer.cast(Bukkit.getPlayerExact((String) results.keySet().toArray()[0])));
					}
				} else {
					// if no match is higher than 80% just use bukkit's default player selector
					RunicPlayer p = RunicPlayer.cast(Bukkit.getPlayer(selector));
					if(p != null) selected.add(p);
				}
			}
		} else if(selector.matches("^(\\b[0-9a-f]{8}[0-9a-f]{4}[0-9a-f]{4}[0-9a-f]{4}[0-9a-f]{12}\\b|\\b[0-9a-f]{8}\\b-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-\\b[0-9a-f]{12}\\b)$")) {
			RunicPlayer rp = RunicPlayer.cast(Bukkit.getPlayer(RunicUtils.stringToUUID(selector)));
			if(rp != null) selected.add(rp);
		} else if(selector.matches("^(?:\\/)(.*)(?:\\/(gi?|ig?)?)$")) {
			Pattern p = Pattern.compile("^(?:\\/)(.*)(?:\\/(gi?|ig?)?)$", Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(selector);
			
			if(m.find()) {
				String searchPattern = m.group(2);
				boolean caseInsensitive = false;
				if(m.groupCount() >= 2) {
					if(m.groupCount() == 3) {
						String flags = m.group(3).toLowerCase();
						if(flags.contains("i")) caseInsensitive = true;
						if(flags.contains("g")) multiple = true;
					}
				}

				if(searchPattern != null && !searchPattern.isEmpty()) {
			        try {
			            Pattern pattern = caseInsensitive ? Pattern.compile(searchPattern, Pattern.CASE_INSENSITIVE) : Pattern.compile(searchPattern);
			            
			            for(Player player : Bukkit.getOnlinePlayers()) {
				            Matcher matcher = pattern.matcher(player.getName());
				            if(matcher.find()) {
				            	RunicPlayer rp = RunicPlayer.cast(player);
				            	if(includeVanished || !rp.isVanished()) {
					            	selected.add(rp);
						            if(!multiple) break;
				            	}
				            }
			            }
			        } catch (PatternSyntaxException exception) {
//			        	System.out.print(exception.getDescription());
			        }
				}
			}
		}
		
		return selected;
	}
	
	public static List<String> suggest(String selector) {
		return suggest(selector, false);
	}
	
	public static List<String> suggest(String selector, boolean includeVanished) {
		List<String> suggestion = new ArrayList<String>();
		
		if(selector.isEmpty()) {
			if(includeVanished) {
				for(Player player : Bukkit.getOnlinePlayers()) {
					suggestion.add(player.getName());
				}
			} else {
				for(Player player : Bukkit.getOnlinePlayers()) {
					RunicPlayer RP = RunicPlayer.cast(player);
					if(!RP.isVanished()) suggestion.add(player.getName());
				}
			}
			Collections.sort(suggestion);
		} else if(selector.matches("^\\*{1,2}$")) {
			suggestion.add("*");
			suggestion.add("**");
		} else if(selector.startsWith("@")) {
			suggestion.add("@a");
			suggestion.add("@r");
		} else {
			if(selector.matches("^\\w+$")) {
				if(includeVanished) {
					for(Player player : Bukkit.getOnlinePlayers()) {
						if(player.getName().toLowerCase().startsWith(selector.toLowerCase()) || player.getName().toLowerCase().contains(selector.toLowerCase())) suggestion.add(player.getName());
					}
				} else {
					for(Player player : Bukkit.getOnlinePlayers()) {
						if((player.getName().toLowerCase().startsWith(selector.toLowerCase()) || player.getName().toLowerCase().contains(selector.toLowerCase()))) suggestion.add(player.getName());
					}
				}
				Collections.sort(suggestion);
			} else {
				if(selector.startsWith("/")) {
					if(!Pattern.compile("^(?:\\/)(.*)(?:\\/(gi?|ig?)?)$", Pattern.CASE_INSENSITIVE).matcher(selector).find()) {
						// if its invalid regex (probably missing close slash '/')
						
						// check if its valid if '/' is appended to the end
						if(Pattern.compile("^(?:\\/)(.*)(?:\\/(gi?|ig?)?)$", Pattern.CASE_INSENSITIVE).matcher(selector+"/").find()) {
							suggestion.add(selector+"/");
						}
					} else {
						// if visibly valid regex, but now to check if flags are valid
						Matcher matcher = Pattern.compile("^(?:\\/)(.*)(?:\\/([^\\/]+)$)", Pattern.CASE_INSENSITIVE).matcher(selector);
						if(matcher.find()) {
							if(matcher.groupCount() == 3) {
								// has flags, check if valid
								String flags = matcher.group(2).toLowerCase();
								if(!flags.matches("^(gi?|ig?)$")) {
									// invalid flags, suggest correction
									String newFlags = "";
									newFlags += flags.contains("g") ? "g" : "";
									newFlags += flags.contains("i") ? "i" : "";
									suggestion.add("/"+matcher.group(1)+"/"+newFlags);
								}
							} else {
								// no flags, suggest flags
								suggestion.add("/"+matcher.group(1)+"/g");
								suggestion.add("/"+matcher.group(1)+"/i");
								suggestion.add("/"+matcher.group(1)+"/gi");
							}
						} // else idk what it could be
					}
				} else {
					List<String> playerNames = new ArrayList<String>();
					if(includeVanished) {
						for(Player player : Bukkit.getOnlinePlayers()) {
							playerNames.add(player.getName());
						}
					} else {
						for(Player player : Bukkit.getOnlinePlayers()) {
							RunicPlayer RP = RunicPlayer.cast(player);
							if(!RP.isVanished()) playerNames.add(player.getName());
						}
					}
					
					String cleanSelector = selector.replaceAll("[^\\w]+", "");
					HashMap<String, Double> results = new HashMap<String, Double>();
					if(playerNames.size() > 0) {
						for(String playerName : playerNames) {
							double distance = Levenshtein.similarity(selector, playerName)*100;
							double secondDistance = Levenshtein.similarity(cleanSelector, playerName)*100;
							
							if(distance >= 80 || secondDistance >= 80) {
								double highestDistance = distance >= secondDistance ? distance : secondDistance;
								results.put(playerName, highestDistance);
							}
						}
					}
					
					if(results.size() > 0) {
						// sorts the results by most similar player name
						entriesSortedByValues(results);
						for(String playerName : results.keySet()) {
							suggestion.add(playerName);
						}
					} else {
						// if no match is higher than 80% just use bukkit's default player selector
						Player p = Bukkit.getPlayer(selector);
						if(p != null) suggestion.add(p.getName());
						
					}
				}
			}
		}
		
		return suggestion;
	}
	
	public static <K,V extends Comparable<? super V>> List<Entry<K, V>> entriesSortedByValues(Map<K,V> map) {
		List<Entry<K,V>> sortedEntries = new ArrayList<Entry<K,V>>(map.entrySet());

		Collections.sort(sortedEntries, 
		    new Comparator<Entry<K,V>>() {
		        @Override
		        public int compare(Entry<K,V> e1, Entry<K,V> e2) {
		            return e2.getValue().compareTo(e1.getValue());
		        }
		    }
		);

		return sortedEntries;
	}
	
}
