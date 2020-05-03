package aw.rmjtromp.RunicCore.core.features.essentials;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import aw.rmjtromp.RunicCore.core.Core;
import aw.rmjtromp.RunicCore.core.features.RunicFeature;
import aw.rmjtromp.RunicCore.core.other.extensions.RunicPlayer;
import aw.rmjtromp.RunicCore.utilities.PlayerSelector;
import aw.rmjtromp.RunicCore.utilities.RunicCommand;
import aw.rmjtromp.RunicCore.utilities.configs.MessageConfig.MESSAGE;
import aw.rmjtromp.RunicCore.utilities.placeholders.Placeholder;

public final class Experience extends RunicFeature implements CommandExecutor, TabCompleter {

    @Override
    public String getName() {
        return "Experience";
    }

    private static enum PERMISSION {
        EXPERIENCE_SELF("runic.experience"),
        EXPERIENCE_OTHERS("runic.experience.others"),
        EXPERIENCE_MODIFY("runic.experience.modify");

        private String permission;
        private PERMISSION(String permission) {
            this.permission = permission;
        }

        @Override
        public String toString() {
            return permission;
        }
    }

    @Override
    public void onEnable() {
        registerCommand(new RunicCommand("experience")
            .setDescription("Give, set or look at a players exp")
            .setAliases(Arrays.asList("xp", "exp"))
            .setPermission(PERMISSION.EXPERIENCE_SELF.toString())
            .setUsage("/experience [give|set|show] [player [amount]]")
            .setExecutor(this)
            .setTabCompleter(this));
    }

    private String target_not_found, incorrect_usage, no_permission, experience_show, experience_show_others, not_enough_arguments, experience_invalid_amount, experience_level_add, experience_level_remove, experience_experience_add, experience_experience_remove, experience_level_add_target, experience_level_remove_target, experience_experience_add_target, experience_experience_remove_target, experience_level_set, experience_experience_set, experience_level_set_target, experience_experience_set_target;
    @Override
    public void loadConfigurations() {
        target_not_found = Core.getMessages().getMessage(MESSAGE.TARGET_NOT_FOUND);
        incorrect_usage = Core.getMessages().getMessage(MESSAGE.INCORRECT_USAGE);
        no_permission = Core.getMessages().getMessage(MESSAGE.NO_PERMISSION);
        not_enough_arguments = Core.getMessages().getMessage(MESSAGE.NOT_ENOUGH_ARGUMENTS);

        experience_show = Core.getMessages().getString("features.experience.show", "&7You have &e{EXP} &7experience (level &e{LEVEL}&7)");
        experience_show_others = Core.getMessages().getString("features.experience.show-other", "&7{TARGET} has &e{EXP} &7experience (level &e{LEVEL}&7)");
        experience_invalid_amount = Core.getMessages().getString("features.experience.invalid-amount", "&7Invalid amount provided.");
        experience_level_add = Core.getMessages().getString("features.experience.level-add", "&e{LEVEL}&7 level was added to your total experience.");
        experience_level_remove = Core.getMessages().getString("features.experience.level-remove", "&e{LEVEL}&7 level was removed from your total experience.");
        experience_level_set = Core.getMessages().getString("features.experience.level-set", "&7Your level was set to &e{LEVEL}&7.");
        experience_experience_add = Core.getMessages().getString("features.experience.experience-add", "&e{EXP}&7 experience was added to your total experience.");
        experience_experience_remove = Core.getMessages().getString("features.experience.experience-remove", "&e{EXP}&7 experience was removed from your total experience.");
        experience_experience_set = Core.getMessages().getString("features.experience.experience-set", "&7Your experience was set to &e{EXP}&7.");
        experience_level_add_target = Core.getMessages().getString("features.experience.level-add-target", "&e{LEVEL}&7 level was added to {TARGET}'s total experience.");
        experience_level_remove_target = Core.getMessages().getString("features.experience.level-remove-target", "&e{LEVEL}&7 level was removed from {TARGET}'s total experience.");
        experience_level_set_target = Core.getMessages().getString("features.experience.level-set-target", "&7{TARGET}'s level was set to &e{LEVEL}&7.");
        experience_experience_add_target = Core.getMessages().getString("features.experience.experience-add-target", "&e{EXP}&7 experience was added to {TARGET}'s total experience.");
        experience_experience_remove_target = Core.getMessages().getString("features.experience.experience-remove-target", "&e{EXP}&7 experience was removed from {TARGET}'s total experience.");
        experience_experience_set_target = Core.getMessages().getString("features.experience.experience-set-target", "&7{TARGET}'s experience was set to &e{EXP}&7.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player && sender.hasPermission(PERMISSION.EXPERIENCE_SELF.toString()) && (args.length == 0 || args.length == 1 && args[0].equalsIgnoreCase("show") || (args.length == 2 && args[0].equalsIgnoreCase("show") && args[1].equalsIgnoreCase(sender.getName())))) {
            RunicPlayer player = RunicPlayer.cast(sender);
            player.sendMessage(Placeholder.parse(experience_show).set("{EXP}", player.getPlayerExp()).set("{LEVEL}", player.getLevel()).getString());
        } else if(args.length == 1) {
            if(sender.hasPermission(PERMISSION.EXPERIENCE_OTHERS.toString()) && sender.hasPermission(PERMISSION.EXPERIENCE_MODIFY.toString())) sender.sendMessage(Placeholder.parse(not_enough_arguments, sender).set("{COMMAND}", label.toLowerCase() + " [show|give|set] [player [amount]]").getString());
            else if(sender.hasPermission(PERMISSION.EXPERIENCE_OTHERS.toString())) sender.sendMessage(Placeholder.parse(not_enough_arguments, sender).set("{COMMAND}", label.toLowerCase() + " [show <player>]").getString());
            else if(sender.hasPermission(PERMISSION.EXPERIENCE_SELF.toString()) && sender.hasPermission(PERMISSION.EXPERIENCE_MODIFY.toString())) sender.sendMessage(Placeholder.parse(not_enough_arguments, sender).set("{COMMAND}", label.toLowerCase() + " [give|set] [amount]").getString());
            else if(sender.hasPermission(PERMISSION.EXPERIENCE_SELF.toString())) sender.sendMessage(Placeholder.parse(not_enough_arguments, sender).set("{COMMAND}", label.toLowerCase()).getString());
            else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
        } else if(args.length == 2) {
            if(args[0].equalsIgnoreCase("show") && sender.hasPermission(PERMISSION.EXPERIENCE_SELF.toString())) {
                List<RunicPlayer> targets = PlayerSelector.select(args[1], sender.isOp());
                if(targets.size() > 0) {
                    if(sender instanceof Player && targets.size() == 1 && targets.contains(RunicPlayer.cast(sender))) {
                    	RunicPlayer target = RunicPlayer.cast(sender);
                        sender.sendMessage(Placeholder.parse(experience_show, sender).set("{EXP}", target.getPlayerExp()).set("{LEVEL}", target.getLevel()).getString());
                    } else if(sender.hasPermission(PERMISSION.EXPERIENCE_OTHERS.toString())) {
                        for(RunicPlayer target: targets) {
                            if(!target.equals(sender)) sender.sendMessage(Placeholder.parse(experience_show_others, sender).set("{TARGET}", target.getName()).set("{EXP}", target.getPlayerExp()).set("{LEVEL}", target.getLevel()).getString());
                            else sender.sendMessage(Placeholder.parse(experience_show, sender).set("{EXP}", target.getPlayerExp()).set("{LEVEL}", target.getLevel()).getString());
                        }
                    } else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
                } else sender.sendMessage(Placeholder.parse(target_not_found, sender).set("{TARGET}", args[1]).getString());
            } else if(args[0].equalsIgnoreCase("give") && sender.hasPermission(PERMISSION.EXPERIENCE_SELF.toString()) && sender.hasPermission(PERMISSION.EXPERIENCE_MODIFY.toString())) {
                int amount = 0;
                boolean level = false;
                if(args[1].toLowerCase().matches("^-?[0-9]+l$")) {
                    level = true;
                    try {
                        amount = Integer.parseInt(args[1].substring(0, args[1].length() - 1));
                    } catch (Exception localException) {}
                } else if(NumberUtils.isNumber(args[1])) amount = Integer.parseInt(args[1]);
                else {
                    sender.sendMessage(Placeholder.parse(experience_invalid_amount, sender).getString());
                    return true;
                }

                RunicPlayer player = RunicPlayer.cast(sender);
                if(level) player.giveExpLevels(amount);
                else player.giveExperience(amount);

                if(amount < 0) {
                    if(level) player.sendMessage(Placeholder.parse(experience_level_remove, player).set("{LEVEL}", amount).getString());
                    else player.sendMessage(Placeholder.parse(experience_experience_remove, player).set("{EXP}", amount).getString());
                } else if(level) player.sendMessage(Placeholder.parse(experience_level_add, player).set("{LEVEL}", amount).getString());
                else player.sendMessage(Placeholder.parse(experience_experience_add, player).set("{EXP}", amount).getString());
            } else if(args[0].equalsIgnoreCase("set") && sender.hasPermission(PERMISSION.EXPERIENCE_SELF.toString()) && sender.hasPermission(PERMISSION.EXPERIENCE_MODIFY.toString())) {
                int amount = 0;
                boolean level = false;
                if(args[1].toLowerCase().matches("^[0-9]+l$")) {
                    level = true;
                    try {
                        amount = Integer.parseInt(args[1].substring(0, args[1].length() - 1));
                    } catch (Exception localException1) {}
                } else if(NumberUtils.isNumber(args[1])) amount = Integer.parseInt(args[1]);
                else {
                    sender.sendMessage(Placeholder.parse(experience_invalid_amount, sender).getString());
                    return true;
                }

                RunicPlayer player = RunicPlayer.cast(sender);
                player.setExp(0.0F);
                player.setLevel(0);

                if(level) {
                    player.giveExpLevels(amount);
                    player.sendMessage(Placeholder.parse(experience_level_set, player).set("{LEVEL}", amount).getString());
                } else {
                    player.giveExperience(amount);
                    player.sendMessage(Placeholder.parse(experience_experience_set, player).set("{EXP}", amount).getString());
                }
            } else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
        } else if(args.length == 3) {
            if(args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("set")) {
                if((sender.hasPermission(PERMISSION.EXPERIENCE_SELF.toString())) && (sender.hasPermission(PERMISSION.EXPERIENCE_MODIFY.toString()))) {
                    List<RunicPlayer> targets = PlayerSelector.select(args[1], sender.isOp());
                    if(targets.size() > 0) {
                        int amount = 0;
                        boolean level = false;
                        if(args[2].toLowerCase().matches("^[0-9]+l$")) {
                            level = true;
                            try {
                                amount = Integer.parseInt(args[2].substring(0, args[2].length() - 1));
                            } catch (Exception localException2) {}
                        } else if(NumberUtils.isNumber(args[2])) amount = Integer.parseInt(args[2]);
                        else {
                            sender.sendMessage(Placeholder.parse(experience_invalid_amount, sender).getString());
                            return true;
                        }
                        if(sender instanceof Player && targets.size() == 1 && targets.contains(RunicPlayer.cast(sender))) {
                        	RunicPlayer target = RunicPlayer.cast(sender);
                            if(args[0].equalsIgnoreCase("give")) {
                                if(level) target.giveExpLevels(amount);
                                else target.giveExperience(amount);

                                if(amount < 0) {
                                    if(level) target.sendMessage(Placeholder.parse(experience_level_remove, target).set("{LEVEL}", amount).getString());
                                    else target.sendMessage(Placeholder.parse(experience_experience_remove, target).set("{EXP}", amount).getString());
                                } else if(level) target.sendMessage(Placeholder.parse(experience_level_add, target).set("{LEVEL}", amount).getString());
                                else target.sendMessage(Placeholder.parse(experience_experience_add, target).set("{EXP}", amount).getString());
                            } else if(args[0].equalsIgnoreCase("set")) {
                                target.setExp(0.0F);
                                target.setLevel(0);
                                
                                if(level) {
                                    target.giveExpLevels(amount);
                                    target.sendMessage(Placeholder.parse(experience_level_set, target).set("{LEVEL}", amount).getString());
                                } else {
                                    target.giveExperience(amount);
                                    target.sendMessage(Placeholder.parse(experience_experience_set, target).set("{EXP}", amount).getString());
                                }
                            }
                        } else if(sender.hasPermission(PERMISSION.EXPERIENCE_OTHERS.toString())) {
                            for(RunicPlayer target: targets) {
                                if(args[0].equalsIgnoreCase("give")) {
                                    if(level) target.giveExpLevels(amount);
                                    else target.giveExperience(amount);

                                    if(amount < 0) {
                                        if(level) {
                                            target.sendMessage(Placeholder.parse(experience_level_remove, target).set("{LEVEL}", amount).getString());
                                            if(!target.equals(sender)) sender.sendMessage(Placeholder.parse(experience_level_remove_target, sender).set("{LEVEL}", amount).set("{TARGET}", target.getName()).getString());
                                        } else {
                                            target.sendMessage(Placeholder.parse(experience_experience_remove, target).set("{EXP}", amount).getString());
                                            if(!target.equals(sender)) sender.sendMessage(Placeholder.parse(experience_experience_remove_target, sender).set("{EXP}", amount).set("{TARGET}", target.getName()).getString());
                                        }
                                    } else if(level) {
                                        target.sendMessage(Placeholder.parse(experience_level_add, target).set("{LEVEL}", amount).getString());
                                        if(!target.equals(sender)) sender.sendMessage(Placeholder.parse(experience_level_add_target, sender).set("{LEVEL}", amount).set("{TARGET}", target.getName()).getString());
                                    } else {
                                        target.sendMessage(Placeholder.parse(experience_experience_add, target).set("{EXP}", amount).getString());
                                        if(!target.equals(sender)) sender.sendMessage(Placeholder.parse(experience_experience_add_target, sender).set("{EXP}", amount).set("{TARGET}", target.getName()).getString());
                                    }
                                } else if(args[0].equalsIgnoreCase("set")) {
                                    target.setExp(0.0F);
                                    target.setLevel(0);
                                    
                                    if(level) {
                                        target.giveExpLevels(amount);
                                        target.sendMessage(Placeholder.parse(experience_level_set, target).set("{LEVEL}", amount).getString());
                                        if(!target.equals(sender)) sender.sendMessage(Placeholder.parse(experience_level_set_target, sender).set("{LEVEL}", amount).set("{TARGET}", target.getName()).getString());
                                    } else {
                                        target.giveExperience(amount);
                                        target.sendMessage(Placeholder.parse(experience_experience_set, target).set("{EXP}", amount).getString());
                                        if(!target.equals(sender)) sender.sendMessage(Placeholder.parse(experience_experience_set_target, sender).set("{EXP}", amount).set("{TARGET}", target.getName()).getString());
                                    }
                                }
                            }
                        } else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
                    } else sender.sendMessage(Placeholder.parse(target_not_found, sender).set("{TARGET}", args[1]).getString());
                } else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
            } else {
            	if(sender.hasPermission(PERMISSION.EXPERIENCE_OTHERS.toString()) && sender.hasPermission(PERMISSION.EXPERIENCE_MODIFY.toString())) sender.sendMessage(Placeholder.parse(incorrect_usage, sender).set("{COMMAND}", label.toLowerCase() + " [show|give|set] [player [amount]]").getString());
                else if(sender.hasPermission(PERMISSION.EXPERIENCE_OTHERS.toString())) sender.sendMessage(Placeholder.parse(incorrect_usage, sender).set("{COMMAND}", label.toLowerCase() + " [show <player>]").getString());
                else if(sender.hasPermission(PERMISSION.EXPERIENCE_SELF.toString()) && sender.hasPermission(PERMISSION.EXPERIENCE_MODIFY.toString())) sender.sendMessage(Placeholder.parse(incorrect_usage, sender).set("{COMMAND}", label.toLowerCase() + " [give|set] [amount]").getString());
                else if(sender.hasPermission(PERMISSION.EXPERIENCE_SELF.toString())) sender.sendMessage(Placeholder.parse(incorrect_usage, sender).set("{COMMAND}", label.toLowerCase()).getString());
                else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
            }
        } else {
        	if(sender.hasPermission(PERMISSION.EXPERIENCE_OTHERS.toString()) && sender.hasPermission(PERMISSION.EXPERIENCE_MODIFY.toString())) sender.sendMessage(Placeholder.parse(incorrect_usage, sender).set("{COMMAND}", label.toLowerCase() + " [show|give|set] [player [amount]]").getString());
            else if(sender.hasPermission(PERMISSION.EXPERIENCE_OTHERS.toString())) sender.sendMessage(Placeholder.parse(incorrect_usage, sender).set("{COMMAND}", label.toLowerCase() + " [show <player>]").getString());
            else if(sender.hasPermission(PERMISSION.EXPERIENCE_SELF.toString()) && sender.hasPermission(PERMISSION.EXPERIENCE_MODIFY.toString())) sender.sendMessage(Placeholder.parse(incorrect_usage, sender).set("{COMMAND}", label.toLowerCase() + " [give|set] [amount]").getString());
            else if(sender.hasPermission(PERMISSION.EXPERIENCE_SELF.toString())) sender.sendMessage(Placeholder.parse(incorrect_usage, sender).set("{COMMAND}", label.toLowerCase()).getString());
            else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> suggestion = new ArrayList<>();
        if(args.length == 1) {
            if(args[0].isEmpty()) {
                if((sender.hasPermission(PERMISSION.EXPERIENCE_OTHERS.toString())) || (sender.hasPermission(PERMISSION.EXPERIENCE_SELF.toString()))) {
                    suggestion.add("show");
                    if(sender.hasPermission(PERMISSION.EXPERIENCE_MODIFY.toString())) suggestion.addAll(Arrays.asList("give", "set"));
                }
            } else if(sender.hasPermission(PERMISSION.EXPERIENCE_OTHERS.toString()) || sender.hasPermission(PERMISSION.EXPERIENCE_SELF.toString())) {
                if(sender.hasPermission(PERMISSION.EXPERIENCE_MODIFY.toString())) {
                    if("give".startsWith(args[0].toLowerCase())) suggestion.add("give");
                    if("set".startsWith(args[0].toLowerCase())) suggestion.add("set");
                }
                if("show".startsWith(args[0].toLowerCase())) suggestion.add("show");
            }
        } else if(args.length == 2) {
            if(args[1].isEmpty()) {
                if(sender.hasPermission(PERMISSION.EXPERIENCE_MODIFY.toString())) {
                    if(sender.hasPermission(PERMISSION.EXPERIENCE_OTHERS.toString())) {
                        for(String player: PlayerSelector.suggest(args[0], sender.isOp())) {
                            suggestion.add(player);
                        }
                    } else if(sender.hasPermission(PERMISSION.EXPERIENCE_SELF.toString())) suggestion.add(sender.getName());
                }
            } else if(sender.hasPermission(PERMISSION.EXPERIENCE_MODIFY.toString())) {
                if(sender.hasPermission(PERMISSION.EXPERIENCE_OTHERS.toString())) {
                    for(String player: PlayerSelector.suggest(args[0], sender.isOp())) {
                        suggestion.add(player);
                    }
                } else if(sender.hasPermission(PERMISSION.EXPERIENCE_SELF.toString()) && sender.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                    suggestion.add(sender.getName());
                }
            }
        }
        return suggestion;
    }

}