/**
 * Bias - POJO Configuration.
 * Copyright (C) 2007 Sven Meier
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package bias.util.converter;

import java.lang.reflect.Type;


public class CharacterConverter implements Converter {
	public Object fromString(String string, Type type) {
		if (string.length() == 6 && string.startsWith("\\u")) {
			int code = Integer.parseInt(string.substring(2), 16);		
			return Character.valueOf((char) code);
		}
		return string.charAt(0);
	}

	public String toString(Object object, Type type) {
		return ((Character)object).toString();
	}
}