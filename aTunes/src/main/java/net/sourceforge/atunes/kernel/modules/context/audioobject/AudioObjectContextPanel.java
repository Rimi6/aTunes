/*
 * aTunes 2.0.0-SNAPSHOT
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
package net.sourceforge.atunes.kernel.modules.context.audioobject;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.kernel.modules.context.ContextPanel;
import net.sourceforge.atunes.kernel.modules.context.ContextPanelContent;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.GenericImageSize;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Context panel to show song information
 * 
 * @author alex
 * 
 */
public class AudioObjectContextPanel extends ContextPanel {

    private static final long serialVersionUID = -7910261492394049289L;

    private List<ContextPanelContent> contents;

    @Override
    protected ImageIcon getContextPanelIcon(AudioObject audioObject) {
        if (audioObject != null) {
            return audioObject.getGenericImage(GenericImageSize.SMALL);
        } else {
            return Images.getImage(Images.AUDIO_FILE_LITTLE);
        }
    }

    @Override
    public String getContextPanelName() {
        return "AUDIOOBJECT";
    }

    @Override
    protected String getContextPanelTitle(AudioObject audioObject) {
        if (audioObject instanceof AudioFile || (audioObject instanceof Radio && ((Radio) audioObject).isSongInfoAvailable())) {
            return I18nUtils.getString("SONG");
        } else if (audioObject instanceof Radio) {
            return I18nUtils.getString("RADIO");
        } else if (audioObject instanceof PodcastFeedEntry) {
            return I18nUtils.getString("PODCAST_FEED");
        }

        return I18nUtils.getString("SONG");
    }

    @Override
    protected List<ContextPanelContent> getContents() {
        if (contents == null) {
            contents = new ArrayList<ContextPanelContent>();
            contents.add(new AudioObjectBasicInfoContent());
            contents.add(new LyricsContent());
        }
        return contents;
    }

    @Override
    protected boolean isPanelEnabledForAudioObject(AudioObject audioObject) {
        return audioObject != null;
    }

}
