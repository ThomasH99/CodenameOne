package com.todocatalyst.todocatalyst;

/*
 * Copyright 2008-2009 Daniel Cachapa <cachapa@gmail.com>
 *
 * This program is distributed under the terms of the GNU General Public Licence Version 3
 * The licence can be read in its entirety in the LICENSE.txt file accompaning this source code,
 * or at: http://www.gnu.org/copyleft/gpl.html
 *
 * This file is part of WeightWatch.
 *
 * WeightWatch is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * WeightWatch is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the WeightWatch source code. If not, see
 * <http://www.gnu.org/licenses/>.
 */

//package net.cachapa.weightwatch.util;

import com.codename1.ui.plaf.UIManager;

public class Localize {
        public static String localize(String defaultValue) {
                return UIManager.getInstance().localize(defaultValue, defaultValue);
        }
        public static String l(String key, String defaultValue) {
                return UIManager.getInstance().localize(key, defaultValue);
        }
}
