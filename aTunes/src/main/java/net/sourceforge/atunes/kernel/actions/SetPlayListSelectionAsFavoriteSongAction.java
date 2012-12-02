/*
 * aTunes 3.0.0
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

package net.sourceforge.atunes.kernel.actions;

import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.KeyStroke;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IFavoritesHandler;
import net.sourceforge.atunes.model.ILocalAudioObjectFilter;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * This action adds selected rows in play list to favorite songs
 * 
 * @author fleax
 * 
 */
public class SetPlayListSelectionAsFavoriteSongAction extends CustomAbstractAction {

	private static final long serialVersionUID = 1185876110005394694L;

	private IFavoritesHandler favoritesHandler;

	private IPlayListHandler playListHandler;

	private ILocalAudioObjectFilter localAudioObjectFilter;

	/**
	 * @param localAudioObjectFilter
	 */
	public void setLocalAudioObjectFilter(
			final ILocalAudioObjectFilter localAudioObjectFilter) {
		this.localAudioObjectFilter = localAudioObjectFilter;
	}

	/**
	 * @param favoritesHandler
	 */
	public void setFavoritesHandler(final IFavoritesHandler favoritesHandler) {
		this.favoritesHandler = favoritesHandler;
	}

	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(final IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}

	/**
	 * Default constructor
	 */
	public SetPlayListSelectionAsFavoriteSongAction() {
		super(I18nUtils.getString("SET_FAVORITE_SONG"));
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
		setEnabled(false);
	}

	@Override
	protected void executeAction() {
		favoritesHandler.toggleFavoriteSongs(
				localAudioObjectFilter.getLocalAudioObjects(playListHandler.getSelectedAudioObjects()));
	}

	@Override
	public boolean isEnabledForPlayListSelection(final List<IAudioObject> selection) {
		if (selection.isEmpty()) {
			return false;
		}

		for (IAudioObject ao : selection) {
			if (ao instanceof IRadio || ao instanceof IPodcastFeedEntry) {
				return false;
			}
		}
		return true;
	}
}
