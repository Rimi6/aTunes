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

import java.awt.event.ActionEvent;
import java.util.List;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.model.NavigationTableModel;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.I18nUtils;

public class PlayNowAction extends Action {

    private static final long serialVersionUID = -2099290583376403144L;

    PlayNowAction() {
        super(I18nUtils.getString("PLAY_NOW"), Images.getImage(Images.PLAY_MENU));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("PLAY_NOW"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Play now feature plays selected song inmediately. If song is added to play list, then is automatically selected.
        // If not, it's added to play list
        int selectedRow = ControllerProxy.getInstance().getNavigationController().getNavigationTablePanel().getNavigationTable().getSelectedRow();
        AudioObject song = ((NavigationTableModel) ControllerProxy.getInstance().getNavigationController().getNavigationTablePanel().getNavigationTable().getModel())
                .getSongAt(selectedRow);
        PlayListHandler.getInstance().playNow(song);
    }

    @Override
    public boolean isEnabledForNavigationTableSelection(List<AudioObject> selection) {
        return selection.size() == 1;
    }

}
