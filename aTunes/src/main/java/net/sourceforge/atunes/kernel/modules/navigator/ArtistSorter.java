/*
 * aTunes 2.2.0-SNAPSHOT
 * Copyright (C) 2006-2011 Alex Aranda, Sylvain Gaudard and contributors
 *
 * See http://www.atunes.org/wiki/index.php?title=Contributing for information about contributors
 *
 * http://www.atunes.org
 * http://sourceforge.net/projects/atunes
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package net.sourceforge.atunes.kernel.modules.navigator;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.sourceforge.atunes.model.INavigationViewSorter;
import net.sourceforge.atunes.model.IStateNavigation;

public class ArtistSorter implements INavigationViewSorter {

	private Collator collator;
	
	private IStateNavigation stateNavigation;
	
	/**
	 * @param stateNavigation
	 */
	public void setStateNavigation(IStateNavigation stateNavigation) {
		this.stateNavigation = stateNavigation;
	}
	
	/**
	 * @param collator
	 */
	public void setCollator(Collator collator) {
		this.collator = collator;
	}
	
	@Override
	public void sort(List<String> list) {
		Comparator<String> comparator = null;
        if (stateNavigation.isUseSmartTagViewSorting() && !stateNavigation.isUsePersonNamesArtistTagViewSorting()) {
        	comparator = new SmartComparator(collator);
        } else if (stateNavigation.isUsePersonNamesArtistTagViewSorting()) {
            comparator = new ArtistNamesComparator(collator);
        } else {
            comparator = new DefaultComparator(collator);
        }
        Collections.sort(list, comparator);
	}
}
