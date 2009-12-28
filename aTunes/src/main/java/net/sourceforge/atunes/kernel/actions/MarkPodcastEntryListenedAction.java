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
package net.sourceforge.atunes.kernel.actions;

import java.util.List;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.I18nUtils;

public class MarkPodcastEntryListenedAction extends ActionOverSelectedObjects<PodcastFeedEntry> {

    private static final long serialVersionUID = 1563803489549692850L;

    MarkPodcastEntryListenedAction() {
        super(I18nUtils.getString("MARK_PODCAST_ENTRY_AS_LISTENED"), Images.getImage(Images.MARK_RSS_AS_READ), PodcastFeedEntry.class);
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("MARK_PODCAST_ENTRY_AS_LISTENED"));
    }

    @Override
    protected void performAction(List<PodcastFeedEntry> objects) {
        for (PodcastFeedEntry pfe : objects) {
            pfe.setListened(true);
        }
        ControllerProxy.getInstance().getNavigationController().refreshTable();
    }

    @Override
    public boolean isEnabledForNavigationTableSelection(List<AudioObject> selection) {
        for (AudioObject ao : selection) {
            if (!(ao instanceof PodcastFeedEntry) || ((PodcastFeedEntry) ao).isListened()) {
                return false;
            }
        }
        return true;
    }

}
