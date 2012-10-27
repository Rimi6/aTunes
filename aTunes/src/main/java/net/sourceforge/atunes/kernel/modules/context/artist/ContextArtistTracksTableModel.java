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

package net.sourceforge.atunes.kernel.modules.context.artist;

import javax.swing.table.DefaultTableModel;

import net.sourceforge.atunes.kernel.modules.context.ITrackTableModel;
import net.sourceforge.atunes.model.IArtistTopTracks;
import net.sourceforge.atunes.model.ITrackInfo;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

class ContextArtistTracksTableModel extends DefaultTableModel implements
	ITrackTableModel {

    /**
	 * 
	 */
    private static final long serialVersionUID = 2018166595041397084L;

    private IArtistTopTracks topTracks;

    /**
     * @param topTracks
     */
    public void setTopTracks(final IArtistTopTracks topTracks) {
	this.topTracks = topTracks;
	fireTableDataChanged();
    }

    @Override
    public Class<?> getColumnClass(final int columnIndex) {
	switch (columnIndex) {
	case 0:
	    return Integer.class;
	case 1:
	    return ITrackInfo.class;
	case 2:
	    return String.class;
	}
	return null;
    }

    @Override
    public int getColumnCount() {
	return 3;
    }

    @Override
    public String getColumnName(final int columnIndex) {
	return columnIndex != 0 ? I18nUtils.getString("SONGS") : "";
    }

    @Override
    public int getRowCount() {
	return topTracks != null ? topTracks.getTracks().size() : 0;
    }

    /**
     * Gets the track.
     * 
     * @param index
     *            the index
     * 
     * @return the track
     */
    @Override
    public ITrackInfo getTrack(final int index) {
	return topTracks != null ? topTracks.getTracks().get(index) : null;
    }

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
	switch (columnIndex) {
	case 0:
	    return StringUtils.getString(rowIndex + 1, ".");
	case 1:
	    return topTracks != null ? topTracks.getTracks().get(rowIndex) : "";
	case 2:
	    return topTracks != null ? topTracks.getTracks().get(rowIndex)
		    .getAlbum() : "";
	}
	return null;
    }

    @Override
    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
	return false;
    }
}
