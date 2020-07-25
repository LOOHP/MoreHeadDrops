package com.loohp.moreheaddrops;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.loohp.moreheaddrops.Listeners.Events;
import com.loohp.moreheaddrops.Utils.ChatColorUtils;
import com.loohp.moreheaddrops.Utils.EntityTypeUtils;
import com.loohp.moreheaddrops.Utils.MCVersion;

import net.md_5.bungee.api.ChatColor;

public class MoreHeadDrops extends JavaPlugin {
	
	public static MoreHeadDrops plugin;
	
	public static MCVersion version;
	
	public static Map<LivingEntity, Entity> deathByCreeper = new ConcurrentHashMap<LivingEntity, Entity>();
	public static Map<Entity, Long> usedCreepers = new ConcurrentHashMap<Entity, Long>();
	
	public static Map<EntityType, ItemStack> mobHeads = new HashMap<EntityType, ItemStack>();
	public static Map<Player, ItemStack> playerHeads = new ConcurrentHashMap<Player, ItemStack>();
	
	public static String nopermsmsg = "";
	public static String reloadmsg = "";
	public static String givenmsg = "";
	public static String skulldontexistmsg = "";
	public static String consolemsg = "";
	public static String playernotexistmsg = "";
	public static String notenoughargsmsg = "";
	
	@Override
	public void onEnable() {
		plugin = this;
		
		version = MCVersion.fromPackageName(getServer().getClass().getPackage().getName());

        if (!version.isSupported()) {
	    	getServer().getConsoleSender().sendMessage(ChatColor.RED + "[MoreHeadDrops] This version of minecraft is unsupported! (" + version.toString() + ")");
	    }
		
		getConfig().options().copyDefaults(true);
		saveConfig();
		
		getCommand("moreheaddrops").setExecutor(new Commands());
		
		getServer().getPluginManager().registerEvents(new Events(), this);
		
		EntityTypeUtils.setUpList();
		loadConfig();
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			Bukkit.getScheduler().runTaskAsynchronously(MoreHeadDrops.plugin, () -> {
				MoreHeadDrops.loadPlayerHead(player);
			});
		}
		
		gc();
		
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[MoreHeadDrops] MoreHeadDrops has been enabled!");
	}
	
	@Override
	public void onDisable() {
		getServer().getConsoleSender().sendMessage(ChatColor.RED + "[MoreHeadDrops] MoreHeadDrops has been disabled!");
	}
	
	public static void loadConfig() {
		plugin.reloadConfig();
		for (EntityType type : EntityTypeUtils.getAllowedEntitiesSet()) {
			if (!plugin.getConfig().contains("Entities." + type.name())) {
				plugin.getConfig().set("Entities." + type.name(), "");
			}
		}
		mobHeads.clear();
		playerHeads.clear();
		for (Entry<String, Object> entry : plugin.getConfig().getConfigurationSection("Entities").getValues(false).entrySet()) {
			String value = (String) entry.getValue();
			if (!value.equals("")) {
				try {
					mobHeads.put(EntityType.valueOf(entry.getKey()), getHead(WordUtils.capitalizeFully(entry.getKey().replace("_", " ").toLowerCase()), value));
				} catch (Exception e) {
					Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[MoreHeadDrops] Unknown mob type " + entry.getKey() + "!");
				}
			}
		}
		nopermsmsg = ChatColorUtils.translateAlternateColorCodes('&', plugin.getConfig().getString("Messages.NoPermission"));
		reloadmsg = ChatColorUtils.translateAlternateColorCodes('&', plugin.getConfig().getString("Messages.Relaod"));
		givenmsg = ChatColorUtils.translateAlternateColorCodes('&', plugin.getConfig().getString("Messages.Given"));
		skulldontexistmsg = ChatColorUtils.translateAlternateColorCodes('&', plugin.getConfig().getString("Messages.SkullDontExist"));
		consolemsg = ChatColorUtils.translateAlternateColorCodes('&', plugin.getConfig().getString("Messages.Console"));
		playernotexistmsg = ChatColorUtils.translateAlternateColorCodes('&', plugin.getConfig().getString("Messages.PlayerNotExist"));
		notenoughargsmsg = ChatColorUtils.translateAlternateColorCodes('&', plugin.getConfig().getString("Messages.NotEnoughArgs"));
		plugin.saveConfig();
	}
	
	public static ItemStack loadPlayerHead(Player player) {
		ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
		SkullMeta meta = (SkullMeta) skull.getItemMeta();
		meta.setOwningPlayer(player);
		skull.setItemMeta(meta);
		playerHeads.put(player, skull);
		return skull;
	}
	
	@SuppressWarnings("deprecation")
	public static ItemStack getHead(String name, String value) {
		ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        UUID hashAsId = new UUID(value.hashCode(), value.hashCode());
        return Bukkit.getUnsafe().modifyItemStack(skull, "{SkullOwner:{Name:\"" + name + "\", Id:\"" + hashAsId + "\",Properties:{textures:[{Value:\"" + value + "\"}]}}}");
    }
	
	public static void gc() {
		Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
			Queue<Entity> removeList = new LinkedList<Entity>();
			long unix = System.currentTimeMillis();
			for (Entry<Entity, Long> entry : usedCreepers.entrySet()) {
				if ((entry.getValue() + 10000) < unix) {
					removeList.add(entry.getKey());
				}
			}
			while (!removeList.isEmpty()) {
				usedCreepers.remove(removeList.poll());
			}
		}, 0, 1200);
	}

}
