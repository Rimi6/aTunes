/*
 * aTunes 1.14.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

package net.sourceforge.atunes.kernel.modules.context.lastfm.runnables;

import java.awt.Image;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import net.sourceforge.atunes.kernel.modules.context.AlbumInfo;
import net.sourceforge.atunes.kernel.modules.context.AlbumListInfo;
import net.sourceforge.atunes.kernel.modules.context.ContextListener;
import net.sourceforge.atunes.kernel.modules.context.lastfm.LastFmService;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.model.Artist;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.AudioObject;

public class LastFmAlbumsRunnable implements Runnable {

    private static Logger logger = new Logger();

    ContextListener listener;
    private LastFmService service;
    AudioObject audioObject;
    private volatile boolean interrupted;
    /** The retrieve artist info. */
    private boolean retrieveArtistInfo = true;
    long id;

    /**
     * Instantiates a new audio scrobbler albums runnable.
     * 
     * @param listener
     *            the listener
     * @param service
     *            the service
     * @param audioObject
     *            the audio object
     * @param id
     *            the id
     */
    public LastFmAlbumsRunnable(ContextListener listener, LastFmService service, AudioObject audioObject, long id) {
        this.listener = listener;
        this.service = service;
        this.audioObject = audioObject;
        this.id = id;
    }

    /**
     * Interrupt.
     */
    public void interrupt() {
        interrupted = true;
    }

    @Override
    public void run() {
        if (!interrupted) {
            listener.setLastAlbumRetrieved(null, id);

            if (retrieveArtistInfo) {
                listener.setLastArtistRetrieved(null, id);
            }
        }

        // Get wiki start for artist
        final String wikiText = service.getWikiText(audioObject.getArtist());
        final String wikiURL = service.getWikiURL(audioObject.getArtist());
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                listener.notifyWikiInfoRetrieved(wikiText, wikiURL, id);
            }
        });

        Image image = null;
        AlbumInfo album = null;
        List<AlbumInfo> albums = null;
        if (!interrupted) {
            // If possible use album artist
            String artist = audioObject.getAlbumArtist().isEmpty() ? audioObject.getArtist() : audioObject.getAlbumArtist();
            album = service.getAlbum(artist, audioObject.getAlbum());
            final AlbumInfo albumHelp = album;

            listener.setAlbum(albumHelp, id);
            if (audioObject instanceof AudioFile) {
                if (album != null) {
                    image = service.getImage(album);
                } else {
                    try {
                        ImageIcon img = audioObject.getCustomImage(-1, -1);
                        image = img != null ? img.getImage() : null;
                    } catch (Exception e) {
                        logger.error(LogCategories.SERVICE, e);
                    }
                }
            }
            listener.setImage(image, audioObject, id);
        }
        if (image != null && !interrupted) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    listener.notifyAlbumRetrieved(audioObject, id);
                }
            });
        }

        try {
            Thread.sleep(1000); // Wait a second to prevent IP banning
        } catch (InterruptedException e) {
            logger.debug(LogCategories.SERVICE, "albums runnable interrupted");
        }

        // If we have to retrieve artist info do it. If not, get previous retrieved albums list
        if (retrieveArtistInfo) {
            if (!interrupted) {
                if (!Artist.isUnknownArtist(audioObject.getArtist())) {
                    AlbumListInfo albumList = service.getAlbumList(audioObject.getArtist(), ApplicationState.getInstance().isHideVariousArtistsAlbums(), ApplicationState
                            .getInstance().getMinimumSongNumberPerAlbum());
                    if (albumList != null) {
                        albums = albumList.getAlbums();
                    }
                }
                if (albums == null) {
                    interrupted = true;
                }
                listener.setAlbums(albums, id);
            }
        } else {
            albums = listener.getAlbums();
        }

        if (album == null && albums != null && !interrupted) {
            // Try to find an album which fits 
            AlbumInfo auxAlbum = null;
            int i = 0;
            while (!interrupted && auxAlbum == null && i < albums.size()) {
                AlbumInfo a = albums.get(i);
                StringTokenizer st = new StringTokenizer(a.getTitle(), " ");
                boolean matches = true;
                int tokensAnalyzed = 0;
                while (st.hasMoreTokens() && matches) {
                    String t = st.nextToken();
                    if (forbiddenToken(t)) { // Ignore album if contains forbidden chars
                        matches = false;
                        break;
                    }
                    if (!validToken(t)) { // Ignore tokens without alphanumerics
                        if (tokensAnalyzed == 0 && !st.hasMoreTokens()) {
                            matches = false;
                        } else {
                            continue;
                        }
                    }
                    if (!audioObject.getAlbum().toLowerCase().contains(t.toLowerCase())) {
                        matches = false;
                    }
                    tokensAnalyzed++;
                }
                if (matches) {
                    auxAlbum = a;
                }
                i++;
            }
            if (!interrupted && auxAlbum != null) {
                auxAlbum = service.getAlbum(auxAlbum.getArtist(), auxAlbum.getTitle());
                if (auxAlbum != null) {
                    listener.setAlbum(auxAlbum, id);
                    image = service.getImage(auxAlbum);
                    listener.setImage(image, audioObject, id);
                }
            }
            if (!interrupted && auxAlbum != null) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        listener.notifyAlbumRetrieved(audioObject, id);
                    }
                });
            }
        }

    }

    /**
     * Sets the retrieve artist info.
     * 
     * @param retrieveArtistInfo
     *            the new retrieve artist info
     */
    public void setRetrieveArtistInfo(boolean retrieveArtistInfo) {
        this.retrieveArtistInfo = retrieveArtistInfo;
    }

    /**
     * Valid token.
     * 
     * @param t
     *            the t
     * 
     * @return true, if successful
     */
    private boolean validToken(String t) {
        return t.matches("[A-Za-z]+");
        //t.contains("(") || t.contains(")")
    }

    /**
     * Forbidden token.
     * 
     * @param t
     *            the t
     * 
     * @return true, if successful
     */
    private boolean forbiddenToken(String t) {
        return t.contains("/");
    }

}
