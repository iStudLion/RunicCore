package aw.rmjtromp.RunicCore.core.other.extensions;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import net.minecraft.server.v1_8_R3.EntityLiving;

public class RunicEntity extends BukkitEntity {
	
	protected RunicEntity(Entity entity) {
		super(entity);
	}
	
	public static RunicEntity create(Location arg0, EntityType arg1) {
		return new RunicEntity(arg0.getWorld().spawnEntity(arg0, arg1));
	}
	
	public static RunicEntity cast(Object arg0) {
		if(arg0 != null) {
			if(arg0 instanceof RunicEntity) return (RunicEntity) arg0;
			if(arg0 instanceof Entity) return new RunicEntity((Entity) arg0);
		}
		return null;
	}
	
	public void setCustomName(String arg0) {
		super.setCustomName(ChatColor.translateAlternateColorCodes('&', arg0));
	}
	
	public boolean hasMetadata(String arg0, Plugin arg1) {
		if(super.hasMetadata(arg0)) {
			for(MetadataValue mdv : super.getMetadata(arg0)) {
				if(mdv.getOwningPlugin().equals(arg1)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public MetadataValue getMetadata(String arg0, Plugin arg1) {
		if(super.hasMetadata(arg0)) {
			for(MetadataValue mdv : super.getMetadata(arg0)) {
				if(mdv.getOwningPlugin().equals(plugin)) {
					return mdv;
				}
			}
		}
		return null;
	}
	
	public void setAI(boolean arg0) {
		if(getEntity() instanceof LivingEntity) {
			EntityLiving handle = ((CraftLivingEntity) ((LivingEntity) getEntity())).getHandle();
			handle.getDataWatcher().watch(15, (byte) (arg0 ? 0 : 1));
		}
	}
	
	public boolean hasAI() {
		if(getEntity() instanceof LivingEntity) {
			EntityLiving handle = ((CraftLivingEntity) ((LivingEntity) getEntity())).getHandle();
			byte b = handle.getDataWatcher().getByte(15);
			if(b == (byte)1) return false;
			return true;
		}
		return false;
	}
	
}
