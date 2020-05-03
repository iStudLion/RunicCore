package aw.rmjtromp.RunicCore.utilities;

import org.bukkit.Bukkit;

import aw.rmjtromp.RunicCore.RunicCore;

/**
 * @author LazyLemons
 * @website https://bukkit.org/threads/get-server-tps.143410/
 */
public class Lag implements Runnable {
	
	public static int TICK_COUNT= 0;
	public static long[] TICKS= new long[600];
	public static long LAST_TICK= 0L;
	
	private static final RunicCore RunicCore = (RunicCore) Bukkit.getPluginManager().getPlugin("RunicCore");
	
	public void register() {
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(RunicCore, this, 100L, 1L);
	}
	
	public static double getTPS() {
		return getTPS(100);
	}
	
	public static double getTPS(int ticks) {
		if (TICK_COUNT< ticks) {
			return 20.0D;
		}
		int target = (TICK_COUNT- 1 - ticks) % TICKS.length;
		long elapsed = System.currentTimeMillis() - TICKS[target];
		
		return ticks / (elapsed / 1000.0D);
	}
	
	public static long getElapsed(int tickID) {
	  if (TICK_COUNT- tickID >= TICKS.length) {}
	
	  long time = TICKS[(tickID % TICKS.length)];
	  return System.currentTimeMillis() - time;
	}
	
	public void run() {
	  TICKS[(TICK_COUNT% TICKS.length)] = System.currentTimeMillis();
	
	  TICK_COUNT+= 1;
	}
}
