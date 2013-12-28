/**
 *   HeavySpleef - Advanced spleef plugin for bukkit
 *   
 *   Copyright (C) 2013 matzefratze123
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package de.matzefratze123.heavyspleef.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import de.matzefratze123.heavyspleef.HeavySpleef;
import de.matzefratze123.heavyspleef.core.Game;
import de.matzefratze123.heavyspleef.core.GameState;
import de.matzefratze123.heavyspleef.objects.SimpleBlockData;
import de.matzefratze123.heavyspleef.objects.SpleefPlayer;
import de.matzefratze123.heavyspleef.util.I18N;
import de.matzefratze123.heavyspleef.util.Util;

public class ReadyListener implements Listener {

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		SpleefPlayer player = HeavySpleef.getInstance().getSpleefPlayer(e.getPlayer());
		Block block = e.getClickedBlock();
		
		if (player == null)
			return;
		if (block == null)
			return;
		if (!player.isActive())
			return;
		
		Game game = player.getGame();
		if (game.getGameState() != GameState.LOBBY)
			return;
		
		SimpleBlockData readyBlock = Util.parseMaterial(HeavySpleef.getSystemConfig().getString("general.ready-block"), false);
		if (readyBlock == null)
			return;
		
		Material mat = readyBlock.getMaterial();
		byte data = readyBlock.getData();
		
		if (mat != block.getType())
			return;
		if (data != block.getData())
			return;
		
		if (player.isReady()) {
			player.sendMessage(I18N._("alreadyVoted"));
		} else {
			player.setReady(true);
			player.sendMessage(I18N._("taggedAsReady"));
		}
	}
	
}
