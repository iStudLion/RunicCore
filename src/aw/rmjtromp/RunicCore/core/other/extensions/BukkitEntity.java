package aw.rmjtromp.RunicCore.core.other.extensions;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import aw.rmjtromp.RunicCore.RunicCore;

public class BukkitEntity implements Entity {
	
	protected RunicCore plugin = RunicCore.getInstance();
	
	private Entity entity;
	
	protected BukkitEntity(Entity entity) {
		this.entity = entity;
	}
	
	public Entity getEntity() {
		return entity;
	}

	@Override
	public List<MetadataValue> getMetadata(String arg0) {
		return entity.getMetadata(arg0);
	}

	@Override
	public boolean hasMetadata(String arg0) {
		return entity.hasMetadata(arg0);
	}

	@Override
	public void removeMetadata(String arg0, Plugin arg1) {
		entity.removeMetadata(arg0, arg1);
	}

	@Override
	public void setMetadata(String arg0, MetadataValue arg1) {
		entity.setMetadata(arg0, arg1);
	}

	@Override
	public String getName() {
		return entity.getName();
	}

	@Override
	public void sendMessage(String arg0) {
		entity.sendMessage(arg0);
	}

	@Override
	public void sendMessage(String[] arg0) {
		entity.sendMessage(arg0);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin arg0) {
		return entity.addAttachment(arg0);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin arg0, int arg1) {
		return entity.addAttachment(arg0, arg1);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin arg0, String arg1, boolean arg2) {
		return entity.addAttachment(arg0, arg1, arg2);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin arg0, String arg1, boolean arg2, int arg3) {
		return entity.addAttachment(arg0, arg1, arg2, arg3);
	}

	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		return entity.getEffectivePermissions();
	}

	@Override
	public boolean hasPermission(String arg0) {
		return entity.hasPermission(arg0);
	}

	@Override
	public boolean hasPermission(Permission arg0) {
		return entity.hasPermission(arg0);
	}

	@Override
	public boolean isPermissionSet(String arg0) {
		return entity.isPermissionSet(arg0);
	}

	@Override
	public boolean isPermissionSet(Permission arg0) {
		return entity.isPermissionSet(arg0);
	}

	@Override
	public void recalculatePermissions() {
		entity.recalculatePermissions();
	}

	@Override
	public void removeAttachment(PermissionAttachment arg0) {
		entity.removeAttachment(arg0);
	}

	@Override
	public boolean isOp() {
		return entity.isOp();
	}

	@Override
	public void setOp(boolean arg0) {
		entity.setOp(arg0);
	}

	@Override
	public boolean eject() {
		return entity.eject();
	}

	@Override
	public String getCustomName() {
		return entity.getCustomName();
	}

	@Override
	public int getEntityId() {
		return entity.getEntityId();
	}

	@Override
	public float getFallDistance() {
		return entity.getFallDistance();
	}

	@Override
	public int getFireTicks() {
		return entity.getFireTicks();
	}

	@Override
	public EntityDamageEvent getLastDamageCause() {
		return entity.getLastDamageCause();
	}

	@Override
	public Location getLocation() {
		return entity.getLocation();
	}

	@Override
	public Location getLocation(Location arg0) {
		return entity.getLocation(arg0);
	}

	@Override
	public int getMaxFireTicks() {
		return entity.getMaxFireTicks();
	}

	@Override
	public List<Entity> getNearbyEntities(double arg0, double arg1, double arg2) {
		return entity.getNearbyEntities(arg0, arg1, arg2);
	}

	@Override
	public Entity getPassenger() {
		return entity.getPassenger();
	}

	@Override
	public Server getServer() {
		return entity.getServer();
	}

	@Override
	public int getTicksLived() {
		return entity.getTicksLived();
	}

	@Override
	public EntityType getType() {
		return entity.getType();
	}

	@Override
	public UUID getUniqueId() {
		return entity.getUniqueId();
	}

	@Override
	public Entity getVehicle() {
		return entity.getVehicle();
	}

	@Override
	public Vector getVelocity() {
		return entity.getVelocity();
	}

	@Override
	public World getWorld() {
		return entity.getWorld();
	}

	@Override
	public boolean isCustomNameVisible() {
		return entity.isCustomNameVisible();
	}

	@Override
	public boolean isDead() {
		return entity.isDead();
	}

	@Override
	public boolean isEmpty() {
		return entity.isEmpty();
	}

	@Override
	public boolean isInsideVehicle() {
		return entity.isInsideVehicle();
	}

	@Override
	public boolean isOnGround() {
		return entity.isOnGround();
	}

	@Override
	public boolean isValid() {
		return entity.isValid();
	}

	@Override
	public boolean leaveVehicle() {
		return entity.leaveVehicle();
	}

	@Override
	public void playEffect(EntityEffect arg0) {
		entity.playEffect(arg0);
	}

	@Override
	public void remove() {
		entity.remove();
	}

	@Override
	public void setCustomName(String arg0) {
		entity.setCustomName(arg0);
	}

	@Override
	public void setCustomNameVisible(boolean arg0) {
		entity.setCustomNameVisible(arg0);
	}

	@Override
	public void setFallDistance(float arg0) {
		entity.setFallDistance(arg0);
	}

	@Override
	public void setFireTicks(int arg0) {
		entity.setFireTicks(arg0);
	}

	@Override
	public void setLastDamageCause(EntityDamageEvent arg0) {
		entity.setLastDamageCause(arg0);
	}

	@Override
	public boolean setPassenger(Entity arg0) {
		return entity.setPassenger(arg0);
	}

	@Override
	public void setTicksLived(int arg0) {
		entity.setTicksLived(arg0);
	}

	@Override
	public void setVelocity(Vector arg0) {
		entity.setVelocity(arg0);
	}

	@Override
	public Spigot spigot() {
		return entity.spigot();
	}

	@Override
	public boolean teleport(Location arg0) {
		return entity.teleport(arg0);
	}

	@Override
	public boolean teleport(Entity arg0) {
		return entity.teleport(arg0);
	}

	@Override
	public boolean teleport(Location arg0, TeleportCause arg1) {
		return entity.teleport(arg0, arg1);
	}

	@Override
	public boolean teleport(Entity arg0, TeleportCause arg1) {
		return entity.teleport(arg0, arg1);
	}
	
	@Override
	public boolean equals(Object object) {
		if(object == null) return false;
		if(object instanceof BukkitEntity) {
			return getEntity().equals(((BukkitEntity) object).getEntity());
		} else {
			return getEntity().equals(object);
		}
	}
	
	@Override
	public String toString() {
		return entity.toString();
	}
	
}
