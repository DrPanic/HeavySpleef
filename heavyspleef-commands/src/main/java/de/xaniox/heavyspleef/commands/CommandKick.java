/*
 * This file is part of HeavySpleef.
 * Copyright (c) 2014-2016 Matthias Werning
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
package de.xaniox.heavyspleef.commands;

import de.xaniox.heavyspleef.commands.base.*;
import de.xaniox.heavyspleef.core.HeavySpleef;
import de.xaniox.heavyspleef.core.Permissions;
import de.xaniox.heavyspleef.core.game.Game;
import de.xaniox.heavyspleef.core.game.GameManager;
import de.xaniox.heavyspleef.core.i18n.I18N;
import de.xaniox.heavyspleef.core.i18n.I18NManager;
import de.xaniox.heavyspleef.core.i18n.Messages;
import de.xaniox.heavyspleef.core.player.SpleefPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandKick {
	
	private final I18N i18n = I18NManager.getGlobal();
	
	@Command(name = "kick", minArgs = 1, usage = "/spleef kick <player>",
			descref = Messages.Help.Description.KICK,
			permission = Permissions.PERMISSION_KICK)
	public void onKickCommand(CommandContext context, HeavySpleef heavySpleef) throws CommandException {
		CommandSender sender = context.getSender();
		if (sender instanceof Player) {
			sender = heavySpleef.getSpleefPlayer(sender);
		}
		
		Player target = context.getPlayer(0);
		String targetName = context.getString(0);
		
		CommandValidate.notNull(target, i18n.getVarString(Messages.Command.PLAYER_NOT_FOUND)
				.setVariable("player", targetName)
				.toString());
		GameManager manager = heavySpleef.getGameManager();
		SpleefPlayer targetSpleefPlayer = heavySpleef.getSpleefPlayer(target);
		
		Game game = manager.getGame(targetSpleefPlayer);
		CommandValidate.notNull(game, i18n.getVarString(Messages.Command.PLAYER_NOT_IN_GAME)
				.setVariable("player", target.getName())
				.toString());
		
		String message = null;
		final int argsLength = context.argsLength();
		
		if (argsLength > 1) {
			StringBuilder builder = new StringBuilder();
			
			for (int i = 1; i < argsLength; i++) {
				builder.append(context.getString(i));
				
				if (i + 1 < argsLength) {
					// Append whitespace
					builder.append(' ');
				}
			}
			
			message = builder.toString();
		}
		
		game.kickPlayer(targetSpleefPlayer, message, sender);
		sender.sendMessage(i18n.getVarString(Messages.Command.PLAYER_KICKED)
				.setVariable("player", target.getName())
				.toString());
	}
	
	@TabComplete("kick")
	public void onKickTabComplete(CommandContext context, List<String> list, HeavySpleef heavySpleef) {
		GameManager manager = heavySpleef.getGameManager();
		
		if (context.argsLength() == 1) {
			for (Game game : manager.getGames()) {
				for (SpleefPlayer player : game.getPlayers()) {
					list.add(player.getName());
				}
			}
		}
	}
	
}