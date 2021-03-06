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
package de.xaniox.heavyspleef.core.event;

import java.lang.reflect.Method;

public class IllegalGameListenerMethodException extends RuntimeException {

	private static final long serialVersionUID = 6647290224229665855L;
	
	private Method illegalMethod;
	
	public IllegalGameListenerMethodException(Method illegalMethod, String message) {
		super(message);
		
		this.illegalMethod = illegalMethod;
	}
	
	public Method getIllegalMethod() {
		return illegalMethod;
	}

}