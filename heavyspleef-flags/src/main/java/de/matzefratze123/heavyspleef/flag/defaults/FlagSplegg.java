/*
 * This file is part of HeavySpleef.
 * Copyright (c) 2014-2015 matzefratze123
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.matzefratze123.heavyspleef.flag.defaults;

import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.BlockIterator;

import com.google.common.collect.Lists;

import de.matzefratze123.heavyspleef.core.HeavySpleef;
import de.matzefratze123.heavyspleef.core.Unregister;
import de.matzefratze123.heavyspleef.core.event.GameStartEvent;
import de.matzefratze123.heavyspleef.core.event.PlayerInteractGameEvent;
import de.matzefratze123.heavyspleef.core.event.Subscribe;
import de.matzefratze123.heavyspleef.core.event.Subscribe.Priority;
import de.matzefratze123.heavyspleef.core.flag.BukkitListener;
import de.matzefratze123.heavyspleef.core.flag.Flag;
import de.matzefratze123.heavyspleef.core.flag.FlagInit;
import de.matzefratze123.heavyspleef.core.flag.Inject;
import de.matzefratze123.heavyspleef.core.game.Game;
import de.matzefratze123.heavyspleef.core.game.GameManager;
import de.matzefratze123.heavyspleef.core.game.GameProperty;
import de.matzefratze123.heavyspleef.core.game.GameState;
import de.matzefratze123.heavyspleef.core.player.SpleefPlayer;
import de.matzefratze123.heavyspleef.flag.defaults.FlagScoreboard.GetScoreboardDisplayNameEvent;
import de.matzefratze123.heavyspleef.flag.presets.BaseFlag;

@Flag(name = "splegg", hasGameProperties = true)
@BukkitListener
public class FlagSplegg extends BaseFlag {

	private static final String TNT_METADATA_KEY = "heavyspleef_tnt";
	private static final String SPLEGG_LAUNCHER_DISPLAYNAME = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Splegg Launcher";
	private static final List<String> SPLEGG_LAUNCHER_LORE = Lists.newArrayList(ChatColor.GRAY + "Right-Click to launch an egg");
	private static final ItemStack SPLEGG_LAUNCHER_ITEMSTACK;
	private static Listener listener;
	
	@Inject
	private Game game;
	
	static {
		SPLEGG_LAUNCHER_ITEMSTACK = new ItemStack(Material.IRON_SPADE);
		
		ItemMeta meta = SPLEGG_LAUNCHER_ITEMSTACK.getItemMeta();
		meta.setDisplayName(SPLEGG_LAUNCHER_DISPLAYNAME);
		meta.setLore(SPLEGG_LAUNCHER_LORE);
		
		SPLEGG_LAUNCHER_ITEMSTACK.setItemMeta(meta);
	}
	
	@FlagInit
	public static void initListener(HeavySpleef heavySpleef) {
		listener = new GlobalFlagListener(heavySpleef);
		Bukkit.getPluginManager().registerEvents(listener, heavySpleef.getPlugin());
	}
	
	@Unregister
	public static void unregisterListener() {
		HandlerList.unregisterAll(listener);
	}
	
	@Override
	public void defineGameProperties(Map<GameProperty, Object> properties) {
		properties.put(GameProperty.INSTANT_BREAK, false);
		properties.put(GameProperty.DISABLE_FLOOR_BREAK, true);
	}

	@Override
	public void getDescription(List<String> description) {
		description.add("Enables the Splegg gamemode in spleef games.");
	}
	
	@Subscribe
	public void onGameStart(GameStartEvent event) {
		Game game = event.getGame();
		
		for (SpleefPlayer player : game.getPlayers()) {
			Inventory inv = player.getBukkitPlayer().getInventory();
			inv.addItem(SPLEGG_LAUNCHER_ITEMSTACK);
			
			player.getBukkitPlayer().updateInventory();
		}
	}
	
	@Subscribe
	public void onPlayerInteractGame(PlayerInteractGameEvent event) {
		HeavySpleef heavySpleef = getHeavySpleef();
		SpleefPlayer player = event.getPlayer();
		
		Action action = event.getAction();
		if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		
		GameManager manager = heavySpleef.getGameManager();
		Game game = manager.getGame(player);
		
		if (game == null || game.getGameState() != GameState.INGAME) { 
			return;
		}
		
		Player bukkitPlayer = player.getBukkitPlayer();
		ItemStack inHand = bukkitPlayer.getItemInHand();
		if (inHand.getType() != SPLEGG_LAUNCHER_ITEMSTACK.getType()) {
			return;
		}
		
		bukkitPlayer.launchProjectile(Egg.class);
		bukkitPlayer.playSound(bukkitPlayer.getLocation(), Sound.GHAST_FIREBALL, 0.4f, 2f);
	}
	
	@Subscribe(priority = Priority.HIGH)
	public void onGetScoreboardDisplayName(GetScoreboardDisplayNameEvent event) {
		event.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Splegg");
	}
	
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		Projectile projectile = event.getEntity();
		if (!(projectile instanceof Egg)) {
			return;
		}
		
		ProjectileSource source = projectile.getShooter();
		
		if (!(source instanceof Player)) {
			return;
		}
		
		SpleefPlayer shooter = getHeavySpleef().getSpleefPlayer(source);
		Game game = getHeavySpleef().getGameManager().getGame(shooter);
		if (game != this.game) {
			return;
		}
		
		projectile.remove();
		
		if (game == null || game.getGameState() != GameState.INGAME) {
			return;
		}
		
		// Use a BlockIterator to determine where the arrow has hit the ground
		BlockIterator blockIter = new BlockIterator(projectile.getWorld(), 
				projectile.getLocation().toVector(), 
				projectile.getVelocity().normalize(), 
				0, 4);
		
		Block blockHit = null;
		
		while (blockIter.hasNext()) {
			blockHit = blockIter.next();
			
			if (blockHit.getType() != Material.AIR) {
				break;
			}
		}
		
		if (!game.canSpleef(blockHit)) {
			//Cannot remove this block
			return;
		}
		
		game.addBlockBroken(shooter, blockHit);
		Material type = blockHit.getType();
		blockHit.setType(Material.AIR);
		
		World world = blockHit.getWorld();
		
		if (type == Material.TNT) {
			Location spawnLocation = blockHit.getLocation().add(0.5, 0, 0.5);
			TNTPrimed tnt = (TNTPrimed) world.spawnEntity(spawnLocation, EntityType.PRIMED_TNT);
			tnt.setMetadata(TNT_METADATA_KEY, new FixedMetadataValue(getHeavySpleef().getPlugin(), game));
			tnt.setYield(3);
			tnt.setFuseTicks(0);
		} else {
			projectile.getWorld().playSound(blockHit.getLocation(), Sound.CHICKEN_EGG_POP, 1.0f, 0.7f);
		}
	}
	
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		Entity entity = event.getEntity();
		
		Game game = null;
		List<MetadataValue> metadatas = entity.getMetadata(TNT_METADATA_KEY);
		for (MetadataValue value : metadatas) {
			if (value.getOwningPlugin() != getHeavySpleef().getPlugin()) {
				continue;
			}
			
			game = (Game) value.value();
		}
		
		if (game != null) {
			List<Block> blocks = event.blockList();
			for (Block block : blocks) {
				if (!game.canSpleef(block)) {
					continue;
				}
				
				block.setType(Material.AIR);
			}
			
			blocks.clear();
		}
	}
	
	private static class GlobalFlagListener implements Listener {
		
		private HeavySpleef heavySpleef;
		
		public GlobalFlagListener(HeavySpleef heavySpleef) {
			this.heavySpleef = heavySpleef;
		}
		
		@EventHandler
		public void onPlayerEggThrow(PlayerEggThrowEvent event) {
			SpleefPlayer player = heavySpleef.getSpleefPlayer(event.getPlayer());
			GameManager manager = heavySpleef.getGameManager();
			
			Game game = manager.getGame(player);
			if (game == null || !game.isFlagPresent(FlagSplegg.class)) {
				return;
			}
			
			event.setHatching(false);
		}
		
	}
	
}
