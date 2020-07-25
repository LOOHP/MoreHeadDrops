package com.loohp.moreheaddrops.Listeners;

import java.util.Iterator;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.loohp.moreheaddrops.MoreHeadDrops;

public class Events implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Bukkit.getScheduler().runTaskAsynchronously(MoreHeadDrops.plugin, () -> {
			MoreHeadDrops.loadPlayerHead(player);
		});
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		MoreHeadDrops.playerHeads.remove(event.getPlayer());
	}
	
	@EventHandler
	public void onEntityDamaged(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof LivingEntity)) {
			return;
		}
		LivingEntity entity = (LivingEntity) event.getEntity();
		if (event.getDamage() >= entity.getHealth()) {
			Entity damager = event.getDamager();
			if (damager.getType().equals(EntityType.CREEPER)) {
				Creeper creeper = (Creeper) damager;
				if (creeper.isPowered()) {
					MoreHeadDrops.deathByCreeper.put(entity, creeper);
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if (player.getUniqueId().equals(UUID.fromString("d26f544b-392a-4a41-891f-fa470f5e6b40"))) {
				event.getDrops().add(new ItemStack(Material.APPLE, 1));
			} else if (player.getUniqueId().equals(UUID.fromString("1112981e-9aaa-4ff9-b7d1-c8fd0747cf4a"))) {
				event.getDrops().add(new ItemStack(Material.BONE, 1));
			}
		}
		
		if (!(event.getEntity() instanceof LivingEntity)) {
			return;
		}
		LivingEntity entity = (LivingEntity) event.getEntity();
		Entity creeper = MoreHeadDrops.deathByCreeper.remove(entity);
		if (creeper == null) {
			return;
		}
		
		switch (entity.getType()) {
		case ZOMBIE:
		case ZOMBIE_VILLAGER:
		case HUSK:
			Iterator<ItemStack> itr0 = event.getDrops().iterator();
			while (itr0.hasNext()) {
				ItemStack item = itr0.next();
				if (item.getType().equals(Material.ZOMBIE_HEAD)) {
					itr0.remove();
					break;
				}
			}
			break;
		case SKELETON:
		case STRAY:
			Iterator<ItemStack> itr1 = event.getDrops().iterator();
			while (itr1.hasNext()) {
				ItemStack item = itr1.next();
				if (item.getType().equals(Material.SKELETON_SKULL)) {
					itr1.remove();
					break;
				}
			}
			break;
		case CREEPER:
			Iterator<ItemStack> itr2 = event.getDrops().iterator();
			while (itr2.hasNext()) {
				ItemStack item = itr2.next();
				if (item.getType().equals(Material.CREEPER_HEAD)) {
					itr2.remove();
					break;
				}
			}
			break;
		case WITHER_SKELETON:
			Iterator<ItemStack> itr3 = event.getDrops().iterator();
			while (itr3.hasNext()) {
				ItemStack item = itr3.next();
				if (item.getType().equals(Material.WITHER_SKELETON_SKULL)) {
					itr3.remove();
					break;
				}
			}
			break;
		default:
			break;
		}
		
		if (MoreHeadDrops.usedCreepers.containsKey(creeper)) {
			return;
		}
		
		if (entity instanceof Player) {
			Player player = (Player) entity;
			ItemStack skull = MoreHeadDrops.playerHeads.get(player);
			if (skull != null) {
				event.getDrops().add(skull.clone());
			} else {
				event.getDrops().add(MoreHeadDrops.loadPlayerHead(player).clone());
			}
			return;
		}
		
		switch (entity.getType()) {
		case ZOMBIE:
			event.getDrops().add(new ItemStack(Material.ZOMBIE_HEAD));
			break;
		case SKELETON:
			event.getDrops().add(new ItemStack(Material.SKELETON_SKULL));
			break;
		case CREEPER:
			event.getDrops().add(new ItemStack(Material.CREEPER_HEAD));
			break;
		case WITHER_SKELETON:
			event.getDrops().add(new ItemStack(Material.WITHER_SKELETON_SKULL));
			break;
		default:
			ItemStack skull = MoreHeadDrops.mobHeads.get(entity.getType());
			if (skull != null) {
				event.getDrops().add(skull.clone());
			}
			break;
		}
		MoreHeadDrops.usedCreepers.put(creeper, System.currentTimeMillis());
	}
	
}
