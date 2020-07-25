package com.loohp.moreheaddrops.Utils;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.EntityType;

public class EntityTypeUtils {
	
	private static Set<EntityType> MobTypesSet = new HashSet<EntityType>();
    
    public static Set<EntityType> getAllowedEntitiesSet() {
    	return MobTypesSet;
    }
    
    public static void setUpList() {
    	MobTypesSet.clear();
    	for (EntityType each : EntityType.values()) {
    		if (each.equals(EntityType.PLAYER) || each.equals(EntityType.ARMOR_STAND) || each.equals(EntityType.UNKNOWN)) {
    			continue;
    		}
    		Set<Class<?>> clazzList = ClassUtils.getAllExtendedOrImplementedTypesRecursively(each.getEntityClass());
    		if (clazzList.contains(org.bukkit.entity.LivingEntity.class)) {
    			MobTypesSet.add(each);
    		}
    	}
    }
	
}
