package aw.rmjtromp.RunicCore.core.features.main.moderation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import aw.rmjtromp.RunicCore.core.features.RunicFeature;

public final class AntiSwear extends RunicFeature {

	private static final String[] profane_words = {"(?:(?:j(?:a|4)ck)|dumb|(?=^)|\\s|b(?:i|!|1)(?:t|\\+|7)ch)(?:a|4)(?:s|5|\\$){2}(?:(?!\\S)|(?:h(?:o|0)l(?:3|e)(?:s|5|$)?))", "f(?:u|v)(?:c|k){2}", "b(?:i|1|!)(?:a|4)?(?:t|7|\\+)ch", "r(?:e|3)(?:t|7|\\+)(?:a|4)rd", "(?:a|4)r(?:s|5|$)(?:e|3)", "(?:a|4)n(?:a|4)l", "(?:5|s)(?:3|e)(?:x|ks)(?!y)", "(?:a|4)nu(?:s|$|5)", "c(?:o|0)ck(?:(?:s|$|5)uck(?:3|e)rs?)?", "cum(?:(?:s|5|$)h(?:o|0)(?:t|7|\\+)(?:s|5|$)?)?", "n(?:i|!|1)(?:9|g){1,2}(?:(?:e|3)r?|(?:4|a)h?)", "b(?:a|4)(?:s|$|5)(?:t|7|\\+)(?:a|4)rd(?:s|5|\\$|z)?", "b(?:o|0){2,}b(?:(?:i|!|1)(?:e|3)(?:s|5|\\$)?|(?:s|5|\\$)?)", "b(?:o|0)n(?:3|e)(?:d|r)(?:s|\\$|5)?", "d(?:i|!|1)ck(?:s|\\$|5)?", "p(?:e|3)n(?:i|!|1)(?:s|\\$|5)", "cl(?:i|!|1)(?:7|t)(?:(?:s|\\$|5)|(?:o|0)r(?:o|0)u(?:s|\\$|5))?", "pu(?:5|s|\\$){2}(?:y|(?:i|!|1)(?:e|3)(?:s|\\$|5)?)", "(?<=\\s)bu(?:t|7){2}(?:(?!\\S)|h(?:0|o)l(?:e|3)(?:s|5|\\$)?|fu(?:c|k){1,2}|h(?:e|3)(?:4|a)d(?:\\$|5|s)?)", "cun(?:t|7|\\+)(?:s|5|\\$)?", "d(?:i|!|1)ld(?:o|0)(?:s|\\$|5)?", "cr(?:e|3)(?:a|4)mp(?:i|!|1)(?:e|3)", "r(?:a|4)p(?:(?:e|3)r?|(?:!|1|i)(?:(?:s|\\$|5)t|ng))", "d(?:e|3){2}p\\s?(?:7|t|\\+)hr(?:0|o)(?:a|4)(?:7|t|\\+)h", "c(?:o|0)c(?:4|a)(?:!|1|i)n(?:3|e)", "h(?:e|3)r(?:o|0)(?:!|1|i)n", "m(?:a|4)r(?:i|1|!)(?:\\s|-)?(?:h|j)u(?:a|4)n(?:a|4)", "w(?:e|3){2}d", "x(?:t|7|\\+)c", "(?:3|e)j(?:a|4)cul(?:a|4)t(?:(?:e|3)|(?:i|!|1)(?:o|0)n)", "(?:b(?:e|3)(?:a|4)(?:t|7|\\+)(?:u(?:s|5|$))?|j(?:e|3)rk|w(?:a|4)nk)(?:\\s?(?:o|0)ff|\\smy\\sm(?:e|3)(?:a|4)(?:t|7|\\+)(?:u(?:s|5|\\$))?)", "m(?:a|4)(?:s|5|\\$)t(?:e|3|u)rb(?:a|4)(?:i|1|!)?(?:t|7|\\+)(?:3|e)?", "f(?:1|!|i)n(?:g|9)(?:e|3)r\\s?(?:y(?:o|0)ur|o?ur|my)(?:s|\\$|5)(?:e|3)l(?:f|v(?:3|e)(?:s|5|\\$))", "(?:bl(?:o|0)w|r(?:!|i|1)m|h(?:a|4)nd)\\s?j(?:o|0)b", "(?:e|3)r(?:e|3)c(?:7|t|\\+)", "v(?:!|i|1)(?:a|4)gr(?:a|4)", "f(?:a|4)(?:g|9){1,2}(?:(?:o|0)(?:t|7|\\+))?", "f(?:0|o)r(?:e|3)(?:s|5|\\$)k(?:i|!|1)n", "fu(?:g|9)ly", "(?:g|9)(?:4|a)n(?:g|9)b(?:4|a)n(?:g|9)", "h(?:i|!|1)(?:t|7|\\+)l(?:3|e)r", "v(?:!|i|1)br(?:a|4)t(?:o|0)r", "m(?:e|3)n(?:\\$|s|5)(?:7|t|\\+)ru(?:a|4)(?:t|7|\\+)(?:(?:e|3)|(?:i|!|1)(?:o|0)n)", "(?<=[^a-zA-Z]|^)m(?:3|e)(?:t|7)h(?:amphetamine)?(?![a-zA-Z])", "m(?:o|0)r(?:o|0)n", "m(?:i|!|1)d(?:g|9)(?:e|3)(?:t|7|\\+)", "(?<=[^a-zA-Z]|^)n(?:a|4)z(?:i|!|1)(?![a-zA-Z])", "(?:o|0)r(?:g|9)(?:a|4)(?:\\$|s|5)m", "p(?:o|0)rn(?:(?:o|0)(?:s|\\$|5)?)?", "p(?:a|4)?(?:e|3)d(?:o|0)(?:ph(?:!|i|1)l(?:e|3))?", "p(?:e|3)rv(?:(?:e|3)r(?:t|7|\\+))?", "(?:o|0)rg(?:y|(?:a|4)(?:s|5|\\$)m)", "p(?:e|3){1,}nu(?:s|5|\\$)", "p(?:e|3)n(?:3|e)(?:t|7|\\+)r(?:a|4)(?:t|7|\\+)(?:(?:e|3)|(?:i|1|!)(?:0|o)n)", "wh(?:o|0)r(?:e|3)", "pr(?:o|0)(?:s|5|\\$)(?:t|7|\\+)(?:1|!|i)(?:t|7|\\+)u(?:t|7|\\+)(?:(?:e|3)|(?:i|!|1)(?:o|0)n)", "pu(?:t|7|\\+)(?:a|4)", "v(?:e|3)r(?:g|9)(?:a|4)", "m(?:i|!|1)(?:e|3)rd(?:a|4)", "(?:c(?:a|4)){2}", "n(?:e|3)uk(?:3|e)n", "(?:s|5|\\$)hl(?:0|o)n(?:g|9)", "(?:s|5|\\$)c(?:i|!|1)(?:s|5|\\$){2}(?:o|0)r(?:i|!|1)n(?:g|9)", "(?:s|5|\\$)cr(?:e|3)w\\s?(?:u|y(?:o|0)u)", "(?:s|5|\\$)lu(?:t|7|\\+)", "(?:s|5|\\$)(?:m(?:e|3)(?:g|9)m(?:a|4)|p(?:e|3)rm(?:a|4))", "(?:s|5|\\$)qu(?:i|1|!)r(?:t|7|\\+)", "w(?:a|4)nk", "(?:t|7|\\\\+)w(?:a|4)(?:t|7|\\\\+)"};
	@Override
	public String getName() {
		return "AntiSwear";
	}
	
//	@Override
//	public void onEnable() {
//		if(Dependency.PROTOCOLLIB.isRegistered()) {
//			DependencyManager.getProtocolManager().addPacketListener(new PacketAdapter( plugin, new PacketType[] { PacketType.Play.Server.CHAT }) {
//				public void onPacketSending(PacketEvent event) {
//	                try {
//	                    try {
//	                    	IChatBaseComponent a = ChatSerializer.a(event.getPacket().getChatComponents().getValues().get(0).getJson());
////	                    	System.out.print(a.getChatModifier().);
////	                    	JSONObject json = (JSONObject) new JSONParser().parse(event.getPacket().getChatComponents().getValues().get(0).getJson());
////	                        if(json.containsKey("extra")) {
////	                        	JSONArray message = (JSONArray) json.get("extra");
////	                        	String finalMessage = "";
////	                        	for(int i = 0; i < message.size(); i++) {
////	                        		JSONObject messageChunk = (JSONObject) message.get(i);
////	                        		if(messageChunk.containsKey("text")) {
////		                        		String msg = messageChunk.get("text").toString();
////		                        		finalMessage += msg;
////	                        		}
////	                        	}
////	                        	
////	                        	if(finalMessage.contains("»") || finalMessage.contains("ABCDEFG")) {
////	                        		System.out.print(finalMessage);
////	                        	}
////	                        }
//	                    } catch (Throwable ignore) {}
//	                } catch(Exception e) {
//	                    e.printStackTrace();
//	                }
//				}
//			});
//		}
//	}
	
	@EventHandler
	public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
		e.setMessage(steralize(e.getMessage()));
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent e) {
		for(int i = 0; i < e.getLines().length; i++) e.setLine(i, steralize(e.getLine(i)));
	}
	
	public static boolean containsSwear(String message) {
		for(String regex : profane_words) if(message.matches(regex)) return true;
		return false;
	}
	
	public static String steralize(String message) {
		for(String regex : profane_words) {
			Pattern pattern = Pattern.compile("("+regex+")", Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(message);
			
			// TODO isnt this supposed to be "while"?
			while(matcher.find()) {
			    for (int i = 1; i <= matcher.groupCount(); i++) {
			        message = message.replaceAll(matcher.group(i), matcher.group(i).replaceAll("[^\\s]", "*"));
			    }
			}
		}
		return message;
	}
	
}
