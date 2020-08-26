package me.freakzboy.main;

import java.util.Collection;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.google.common.collect.Sets;

import de.inventivegames.particle.ParticleEffect;

public class SCP extends JavaPlugin implements Listener {

	public static boolean scp = false;
	public static Player runner;
	
	public static HashMap<String, Integer> stunned = new HashMap<String, Integer>();
	public static HashMap<String, ArmorStand> stunnedAS = new HashMap<String, ArmorStand>();
	
	@Override
	public void onEnable() {
		
		Bukkit.getPluginManager().registerEvents(this, this);
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			
			@Override
			public void run() {
				
				if(scp == true) {
		            final World w = runner.getWorld();
		            final Location currentLocation = runner.getEyeLocation();
		            final Location location = runner.getTargetBlock(Sets.newHashSet(Material.AIR, Material.LONG_GRASS, Material.WEB, Material.STATIONARY_WATER, Material.WATER) , 10).getLocation();
		            final double distance = Math.floor(currentLocation.distance(location));
		            final Vector line = location.subtract(currentLocation).toVector();
		            for (double i = 0; i < distance; i += 0.1) {
		                final Vector activeline = line.clone().multiply(i);
		                currentLocation.add(activeline);
		                if (currentLocation.getBlock().getType() != Material.AIR && currentLocation.getBlock().getType() != Material.LONG_GRASS)break;
		                final Collection<Entity> damaged = w.getNearbyEntities(currentLocation, 1, 2, 1);
		                for (final Entity ent : damaged) {
		                    if (ent instanceof Player && ent != runner) {
	
		                        final Player scp = (Player) ent;
		                        
		                        
		                        if(stunned.containsKey(scp.getName())) {
		                        	stunned.remove(scp.getName());
		                        } else {
		                        	Location loc = new Location(scp.getWorld(), scp.getLocation().getX(), scp.getLocation().getY() - 1.0, scp.getLocation().getZ());
		                        	
		                            ArmorStand stand = scp.getWorld().spawn(loc, ArmorStand.class);
		                            
		                            stunnedAS.put(scp.getName(), stand);
		                            
		                            stand.setGravity(false);
		                            stand.setSmall(true);
		                            stand.setVisible(false);
		                            stand.setPassenger(scp);
		                        }
		                        
		                        stunned.put(scp.getName(), 5);
		                        
		                        
		                        try {
									ParticleEffect.FIREWORKS_SPARK.sendToPlayers(Bukkit.getOnlinePlayers(), scp.getLocation(), 0.5F, 0.5F, 0.5F, 0.5F, 1);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
		                        
		                        break;
		                    }
		                }
		                currentLocation.subtract(activeline);
		            }
		            
		            for(String pname : stunned.keySet()) {
		            	if(stunned.get(pname) <= 1) {
		            		stunned.remove(pname);
		            		
		            		stunnedAS.get(pname).remove();
		            		stunnedAS.remove(pname);
		            		
		            	} else {
		            		
		            		stunnedAS.get(pname).setPassenger(Bukkit.getPlayer(pname));
		            		
		            		int oldi = stunned.get(pname);
		            		stunned.remove(pname);
		            		int newi = oldi - 1;
		            		stunned.put(pname, newi);
		            	}
		            }
		            
				}
				
			}
		}, 0L, 1L);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(sender instanceof Player) {
			if(((Player) sender).isOp()) {
				if(cmd.getName().equalsIgnoreCase("scpon")) {
					runner = (Player) sender;
					scp = true;
					sender.sendMessage("§4SCP started");
				}
				
				if(cmd.getName().equalsIgnoreCase("scpoff")) {
					runner = null;
					scp = false;
					sender.sendMessage("§4SCP stopped");
				}
			}
		}
		
		return false;
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof Player) {
			if(e.getDamager() instanceof Player) {
				Player r = (Player) e.getEntity();
				Player scp = (Player) e.getDamager();
				
				if(this.scp == true) {
					if(r == runner) {
						r.damage(100.0);
					}
				}
				
			}
		}
	}
	
	
}
