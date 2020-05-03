package aw.rmjtromp.RunicCore.utilities.configs;

import aw.rmjtromp.RunicCore.core.Core;

public final class MessageConfig extends Config {

	private MessageConfig() {
		super("messages");
		load();
	}
	
	@Override
	public String getName() {
		return "MessageConfig";
	}
	
	public static MessageConfig init() {
		return new MessageConfig();
	}
	
	public enum MESSAGE {
		/** @message You don't have enough permission to use this command. */
		NO_PERMISSION("no-permission", "&cYou don't have enough permission to use this command."),
		/** @message Player '{TARGET}' is not online. */
		@Deprecated
		TARGET_NOT_ONLINE("target-not-online", "&cPlayer '&7{TARGET}&c' is not online."),
		/** @message Player '{TARGET}' not found. */
		TARGET_NOT_FOUND("target-not-found", "&cPlayer '&7{TARGET}&c' not found."),
		/** @message This command does not support the selection of multiple players. */
		TARGET_SELECTION_TOO_BIG("target-selection-too-big", "&cThis command does not support the selection of multiple players."),
		/** @message Player '{TARGET}' doesn't have enough permission to use {FEATURE}. */
		TARGET_DOESNT_HAVE_ENOUGH_PERMISSIONS("target-not-enough-permission", "&cPlayer '&7{TARGET}&c' doesn't have enough permission to use {FEATURE}."),
		/** @message Incorrect Usage. Try "/{COMMAND}". */
		INCORRECT_USAGE("incorrect-usage", "&cIncorrect Usage. Try \"/{COMMAND}\"."),
		/** @message Only players can use this command. */
		SENDER_NOT_A_PLAYER("sender-not-a-player", "&cOnly players can use this command."),
		/** @message {FEATURE} is currently disabled. */
		FEATURE_DISABLED("feature-disabled", "&c{FEATURE} is currently disabled."),
		/** @message This command is currently disabled. */
		COMMAND_DISABLED("command-disabled", "&cThis command is currently disabled."),
		/** @message Not Enough Arguments. Try "/{COMMAND}" instead. */
		NOT_ENOUGH_ARGUMENTS("not-enough-arguments", "&cNot Enough Arguments. Try \"/{COMMAND}\" instead."),
		/** @message You must wait {TIME} before you can use this command again. */
		COMMAND_COOLDOWN("command-cooldown", "&7You must wait &e{TIME} &7before you can use this command again."),
		/** @message You must wait {TIME} before you can use this again. */
		FEATURE_COOLDOWN("feature-cooldown", "&7You must wait &e{TIME} &7before you can use this again."),
		/** @message Invalid Argument. Try "/{COMMAND}" for help. */
		INVALID_ARGUMENT("invalid-argument", "&cInvalid Argument. Try \"/{COMMAND}\" for help."),
		/** @message Request denied. Reason: "{REASON}". */
		REQUEST_DENIED("request-denied", "&cRequest denied. Reason: \"&7{REASON}&c\".");
		
		private String path;
		private String defaultMessage;
		private String message;
		private Long last_check = System.currentTimeMillis();
		
		MESSAGE(String path, String defaultMessage) {
			this.path = path;
			this.defaultMessage = defaultMessage;
			message = Core.getMessages().getString(path, defaultMessage);
		}
		
		public String getMessage() {
			if(System.currentTimeMillis() - last_check > 2500) {
				last_check = System.currentTimeMillis();
				return message = Core.getMessages().getString(path, defaultMessage);
			}
			return message;
		}
	}
	
	public String getMessage(MESSAGE message) {
		return message.getMessage();
	}

}
