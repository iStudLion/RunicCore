package aw.rmjtromp.RunicCore.core.features.main.other;

import org.bukkit.event.Listener;

import aw.rmjtromp.RunicCore.core.features.RunicFeature;

public final class Test extends RunicFeature implements /*CommandExecutor,*/ Listener {
	
	public Test() {
//		super(false); // disabled
	}

	@Override
	public String getName() {
		return "Test";
	}
	
//	@Override
//	public void onEnable() {
//		registerCommand(new RunicCommand("test")
//				.setDescription("RunicCore test command for testing")
//				.setUsage("/test")
//				.setExecutor(this));
//				.setTabCompleter(this));
//	}

//	@Override
//	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
//		
//		
//		
//		return true;
//		ProtocolManager protocolManager = DependencyManager.getProtocolManager();
//		if(protocolManager != null) {
//			// Read tab completer
//			protocolManager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, new PacketType[]{ PacketType.Play.Client.TAB_COMPLETE }){
//				@EventHandler(priority = EventPriority.HIGHEST)
//				public void onPacketReceiving(PacketEvent e){
//					if (e.getPacketType() == PacketType.Play.Client.TAB_COMPLETE){
//						try{
//							PacketContainer packet = e.getPacket();
//							String message = ((String) packet.getSpecificModifier(String.class).read(0)).toLowerCase();
//							System.out.print(message);
//						} catch (Exception e2) {
//							e2.printStackTrace();
//						}
//					}
//				}
//			});
			
			
//			// removes command from tab completer
//			protocolManager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, new PacketType[] { PacketType.Play.Server.TAB_COMPLETE }) {
//				@EventHandler(priority = EventPriority.HIGHEST)
//				public void onPacketSending(PacketEvent e) {
//					if(e.getPacketType() == PacketType.Play.Server.TAB_COMPLETE) {
//						try {
//							for(int x = 0; x < e.getPacket().getStringArrays().getValues().size(); x++) {
//								List<String> newCompleter = new ArrayList<String>();
//								for(int y = 0; y < e.getPacket().getStringArrays().getValues().get(x).length; y++) {
//									if(!e.getPacket().getStringArrays().getValues().get(x)[y].contains(":") && !newCompleter.contains(e.getPacket().getStringArrays().getValues().get(x)[y])) {
//										newCompleter.add(e.getPacket().getStringArrays().getValues().get(x)[y]);
//									}
//								}
//								
//								String[] newComp = new String[newCompleter.size()];
//								for(int z = 0; z < newCompleter.size(); z++) {
//									newComp[z] = newCompleter.get(z);
//								}
//								e.getPacket().getStringArrays().write(x, newComp);
//							}
//						} catch (Exception e2) {
//							e2.printStackTrace();
//						}
//					}
//				}
//			});
			
			
//		}
//		
//		if(sender.isOp()) {
////			// fake a player gamemode change
//			if(sender instanceof Player) {
//				ProtocolManager pm = DependencyManager.getProtocolManager();
//				if(pm != null) {
//					try {
//						Player player = (Player) sender;
//						PacketContainer packet = pm.createPacket(PacketType.Play.Server.GAME_STATE_CHANGE);
//						// https://wiki.vg/Protocol#Change_Game_State
//						// 3 = reason = gamemodechange
//						// 0 = gamemmode = survival
//						// /test <gamemode int>
//						packet.getIntegers().write(0, Byte.toUnsignedInt((byte) 3));
//						packet.getFloat().write(0, (float) 3);
//						pm.sendServerPacket(player, packet);
//						sender.sendMessage("gamemode changed");
//						if(!player.getAllowFlight()) player.setAllowFlight(true);
//						if(!player.isFlying()) player.setFlying(true);
//						
////						PacketContainer fly = pm.createPacket(PacketType.Play.Server.ABILITIES);
////						fly.getBytes().write(0, (byte) 2);
////						fly.getFloat().write(0, player.getFlySpeed());
////						fly.getFloat().write(1, player.getWalkSpeed());
////						pm.sendServerPacket(player, fly);
////						sender.sendMessage("you should be flying?");
//					} catch (InvocationTargetException e) {
//						e.printStackTrace();
//						sender.sendMessage("error changing gamemode or toggling flight");
//					}
//				}
//			}
//			return true;
//		}
//		return false;
//	}
}
