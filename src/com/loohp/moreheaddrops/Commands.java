package com.loohp.moreheaddrops;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.loohp.moreheaddrops.Utils.ChatColorUtils;

import net.md_5.bungee.api.ChatColor;

public class Commands implements CommandExecutor, TabCompleter {

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!label.equalsIgnoreCase("moreheaddrops") && !label.equalsIgnoreCase("mhd") && !label.equalsIgnoreCase("heads")) {
			return true;
		}
		
		if (args.length < 1) {
			sender.sendMessage(ChatColor.AQUA + "[MoreHeadDrops] MoreHeadDrops written by LOOHP!");
			sender.sendMessage(ChatColor.GOLD + "[MoreHeadDrops] You are running MoreHeadDrops version: " + MoreHeadDrops.plugin.getDescription().getVersion());
			return true;
		}
		
		if (args[0].equalsIgnoreCase("reload")) {
			if (sender.hasPermission("moreheaddrops.reload")) {
				MoreHeadDrops.loadConfig();
				for (Player player : Bukkit.getOnlinePlayers()) {
					Bukkit.getScheduler().runTaskAsynchronously(MoreHeadDrops.plugin, () -> {
						MoreHeadDrops.loadPlayerHead(player);
					});
				}
				sender.sendMessage(MoreHeadDrops.reloadmsg);
			} else {
				sender.sendMessage(MoreHeadDrops.nopermsmsg);
			}
			return true;
		}
		
		if (args[0].equalsIgnoreCase("give")) {
			if (sender.hasPermission("moreheaddrops.give.player") || sender.hasPermission("moreheaddrops.give.mob")) {
				switch (args.length) {
				case 1:
				case 2:
				case 3:
					sender.sendMessage(MoreHeadDrops.notenoughargsmsg.replace("%s", "/moreheaddrops give <player> <type> <skull> [amount]"));
					break;
				default:
					Player getter = Bukkit.getPlayer(args[1]);
					if (getter == null) {
						sender.sendMessage(MoreHeadDrops.playernotexistmsg);
						break;
					}
					int amount = 1;
					if (args.length > 4) {
						try {
							int num = Integer.parseInt(args[4]);
							if (num > 0) {
								amount = num;
							}
						} catch (Exception ignore) {}
					}
					if (args[2].equalsIgnoreCase("player")) {
						if (sender.hasPermission("moreheaddrops.give.player")) {
							Player player = Bukkit.getPlayer(args[3]);
							if (player != null) {
								ItemStack skull = MoreHeadDrops.playerHeads.get(player);
								if (skull == null) {
									skull = MoreHeadDrops.loadPlayerHead(player);
								}
								skull = skull.clone();
								skull.setAmount(amount);
								getter.getWorld().dropItem(getter.getEyeLocation(), skull);
								sender.sendMessage(MoreHeadDrops.givenmsg.replace("%s", getter.getName()));
							} else {
								OfflinePlayer offlineplayer = Bukkit.getOfflinePlayer(args[3]);
								if (offlineplayer != null) {
									ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
									SkullMeta meta = (SkullMeta) skull.getItemMeta();
									meta.setOwningPlayer(offlineplayer);
									skull.setItemMeta(meta);
									skull.setAmount(amount);
									getter.getWorld().dropItem(getter.getEyeLocation(), skull);
									sender.sendMessage(MoreHeadDrops.givenmsg.replace("%s", getter.getName()));
								} else {
									sender.sendMessage(MoreHeadDrops.skulldontexistmsg);
								}
							}
						} else {
							sender.sendMessage(MoreHeadDrops.nopermsmsg);
						}
					} else if (args[2].equalsIgnoreCase("mob")) {
						if (sender.hasPermission("moreheaddrops.give.mob")) {
							try {
								ItemStack skull = MoreHeadDrops.mobHeads.get(EntityType.valueOf(args[3].toUpperCase()));
								if (skull != null) {
									skull = skull.clone();
									skull.setAmount(amount);
									getter.getWorld().dropItem(getter.getEyeLocation(), skull);
									sender.sendMessage(MoreHeadDrops.givenmsg.replace("%s", getter.getName()));
								}
							} catch (Exception e) {
								sender.sendMessage(MoreHeadDrops.skulldontexistmsg);
							}
						} else {
							sender.sendMessage(MoreHeadDrops.nopermsmsg);
						}
					} else {
						sender.sendMessage(MoreHeadDrops.skulldontexistmsg);
					}
				}
			} else {
				sender.sendMessage(MoreHeadDrops.nopermsmsg);
			}
			return true;
		}
		
		sender.sendMessage(ChatColorUtils.translateAlternateColorCodes('&', Bukkit.spigot().getConfig().getString("messages.unknown-command")));
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> tab = new ArrayList<String>();
		if (!label.equalsIgnoreCase("moreheaddrops") && !label.equalsIgnoreCase("mhd") && !label.equalsIgnoreCase("heads")) {
			return tab;
		}
		
		switch (args.length) {
		case 0:
			if (sender.hasPermission("moreheaddrops.reload")) {
				tab.add("reload");
			}
			if (sender.hasPermission("moreheaddrops.give.player") || sender.hasPermission("moreheaddrops.give.mob")) {
				tab.add("give");
			}
			return tab;
		case 1:
			if (sender.hasPermission("moreheaddrops.reload")) {
				if ("reload".startsWith(args[0].toLowerCase())) {
					tab.add("reload");
				}
			}
			if (sender.hasPermission("moreheaddrops.give.player") || sender.hasPermission("moreheaddrops.give.mob")) {
				if ("give".startsWith(args[0].toLowerCase())) {
					tab.add("give");
				}
			}
			return tab;
		case 2:
			if (args[0].equalsIgnoreCase("give")) {
				if (sender.hasPermission("moreheaddrops.give.player") || sender.hasPermission("moreheaddrops.give.mob")) {
					for (Player player : Bukkit.getOnlinePlayers()) {
						if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
							tab.add(player.getName());
						}
					}
				}
			}
			return tab;
		case 3:
			if (args[0].equalsIgnoreCase("give")) {
				if (sender.hasPermission("moreheaddrops.give.player")) {
					if ("player".startsWith(args[2].toLowerCase())) {
						tab.add("player");
					}
				}
				if (sender.hasPermission("moreheaddrops.give.mob")) {
					if ("mob".startsWith(args[2].toLowerCase())) {
						tab.add("mob");
					}
				}
			}
			return tab;
		case 4:
			if (args[0].equalsIgnoreCase("give")) {
				if (sender.hasPermission("moreheaddrops.give.player") && args[2].equalsIgnoreCase("player")) {
					for (Player player : Bukkit.getOnlinePlayers()) {
						if (player.getName().toLowerCase().startsWith(args[3].toLowerCase())) {
							tab.add(player.getName());
						}
					}
				}
				if (sender.hasPermission("moreheaddrops.give.mob") && args[2].equalsIgnoreCase("mob")) {
					for (EntityType type : MoreHeadDrops.mobHeads.keySet()) {
						if (type.name().toLowerCase().startsWith(args[3].toLowerCase())) {
							tab.add(type.name());
						}
					}
				}
			}
			return tab;
		case 5:
			if (args[0].equalsIgnoreCase("give")) {
				if (sender.hasPermission("moreheaddrops.give.player") || sender.hasPermission("moreheaddrops.give.mob")) {
					if (args[4].equalsIgnoreCase("")) {
						for (int i = 1; i < 65; i++) {
							tab.add(i + "");
						}
					}
				}
			}
		default:
			return tab;
		}
	}

}
