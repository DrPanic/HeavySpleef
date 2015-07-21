/*
 * This file is part of addons.
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
package de.matzefratze123.joingui;

import org.bukkit.entity.Player;

import de.matzefratze123.heavyspleef.commands.base.proxy.Filter;
import de.matzefratze123.heavyspleef.commands.base.proxy.Proxy;
import de.matzefratze123.heavyspleef.commands.base.proxy.ProxyContext;
import de.matzefratze123.heavyspleef.commands.base.proxy.ProxyPriority;
import de.matzefratze123.heavyspleef.commands.base.proxy.ProxyPriority.Priority;
import de.matzefratze123.heavyspleef.commands.base.proxy.Redirection;
import de.matzefratze123.heavyspleef.core.Permissions;
import de.matzefratze123.heavyspleef.core.i18n.I18N;
import de.matzefratze123.heavyspleef.core.i18n.Messages;

@Filter("spleef/join")
@ProxyPriority(Priority.HIGH)
public class JoinCommandProxy implements Proxy {

	private final I18N i18n;
	private JoinInventory inventory;
	
	public JoinCommandProxy(JoinInventory inventory, JoinGuiAddOn addOn) {
		this.inventory = inventory;
		this.i18n = addOn.getI18n();
	}
	
	@Override
	public void execute(ProxyContext context, Object[] executionArgs) {
		if (!context.getSender().hasPermission(Permissions.PERMISSION_JOIN)) {
			context.getSender().sendMessage(i18n.getString(Messages.Command.NO_PERMISSION));
			return;
		}
		
		//Activate the GUI when the args length is 0
		if (context.argsLength() == 0 && context.getSender() instanceof Player) {
			Player player = context.getSender();
			inventory.open(player);
			
			context.redirect(Redirection.CANCEL);
		}
	}

}