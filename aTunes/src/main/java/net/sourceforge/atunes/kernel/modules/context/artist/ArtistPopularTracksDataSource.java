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

package net.sourceforge.atunes.kernel.modules.context.artist;

import net.sourceforge.atunes.model.IArtistTopTracks;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IContextInformationSource;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.model.IWebServicesHandler;

/**
 * Data Source for artist popular tracks
 * 
 * @author alex
 * 
 */
public class ArtistPopularTracksDataSource implements IContextInformationSource {

	private IWebServicesHandler webServicesHandler;

	private IArtistTopTracks topTracks;

	private IUnknownObjectChecker unknownObjectChecker;

	private IRepositoryHandler repositoryHandler;

	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(final IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	@Override
	public void getData(final IAudioObject audioObject) {
		this.topTracks = getTopTracksData(audioObject);
		repositoryHandler.checkAvailability(audioObject.getArtist(unknownObjectChecker), this.topTracks.getTracks());
	}

	/**
	 * @return
	 */
	public IArtistTopTracks getTopTracks() {
		return topTracks;
	}

	private IArtistTopTracks getTopTracksData(final IAudioObject audioObject) {
		if (!unknownObjectChecker.isUnknownArtist(audioObject.getArtist(unknownObjectChecker))) {
			return webServicesHandler.getTopTracks(audioObject.getArtist(unknownObjectChecker));
		}
		return null;
	}

	/**
	 * @param webServicesHandler
	 */
	public final void setWebServicesHandler(final IWebServicesHandler webServicesHandler) {
		this.webServicesHandler = webServicesHandler;
	}

	@Override
	public void cancel() {
	}

}
