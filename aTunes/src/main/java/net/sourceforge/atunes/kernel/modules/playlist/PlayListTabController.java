/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.kernel.modules.playlist;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.gui.lookandfeel.TabCloseListener;
import net.sourceforge.atunes.gui.views.controls.playList.PlayListTable;
import net.sourceforge.atunes.gui.views.panels.ButtonTabComponent;
import net.sourceforge.atunes.gui.views.panels.PlayListTabPanel;
import net.sourceforge.atunes.kernel.AbstractSimpleController;
import net.sourceforge.atunes.misc.log.LogCategories;

final class PlayListTabController extends AbstractSimpleController<PlayListTabPanel> {

	private PlayListTable table;
	
    /**
     * Instantiates a new play list tab controller.
     * 
     * @param panel
     *            the panel
     */
    PlayListTabController(PlayListTabPanel panel, PlayListTable table) {
        super(panel);
        this.table = table;
        addBindings();
        addStateBindings();
    }

    @Override
    protected void addBindings() {
    	new TabReorderer(this, getComponentControlled().getPlayListTabbedPane()).enableReordering();
        PlayListTabListener listener = new PlayListTabListener(this, getComponentControlled(), table);
        getComponentControlled().getArrangeColumnsMenuItem().addActionListener(listener);
        getComponentControlled().getPlayListTabbedPane().addChangeListener(listener);
        getComponentControlled().getPlayListTabbedPane().addMouseListener(listener);
        getComponentControlled().getPlayListsPopUpButton().addActionListener(listener);
        
        if (LookAndFeelSelector.getInstance().getCurrentLookAndFeel().isTabCloseButtonsSupported()) {
        	LookAndFeelSelector.getInstance().getCurrentLookAndFeel().addTabCloseButtons(getComponentControlled().getPlayListTabbedPane(), new TabCloseListener() {

        		private int tabIndex = -1;
        		
				@Override
				public void tabClosed(JTabbedPane tabbedPane, Component c) {
				}

				@Override
				public void tabClosing(JTabbedPane tabbedPane, Component c) {
					// Get tab index
		            tabIndex = tabbedPane.indexOfComponent(c);
		            if (tabIndex != -1) {
		            	getLogger().debug(LogCategories.CONTROLLER, "Closing playlist with index", tabIndex);
		            	// Remove play list (look and feel will remove tab so we explicitly set removeTab argument to false
		                PlayListHandler.getInstance().removePlayList(tabIndex, false);
		            }
				}

				@Override
				public boolean vetoTabClosing(JTabbedPane tabbedPane, Component c) {
					// Veto if there is only one tab
					return tabbedPane.getTabCount() == 1;
				}
        		
        	});
        }

    }

    @Override
    protected void addStateBindings() {
        // Nothing to do
    }

    /**
     * Delete play list.
     * 
     * @param index
     *            the index
     */
    void deletePlayList(int index) {
   		getComponentControlled().getPlayListTabbedPane().removeTabAt(index);
    }

    /**
     * Force switch to.
     * 
     * @param index
     *            the index
     */
    void forceSwitchTo(int index) {
        getComponentControlled().getPlayListTabbedPane().setSelectedIndex(index);
    }

    /**
     * New play list.
     * 
     * @param name
     *            the name
     */
    void newPlayList(String name) {
        JPanel emptyPanel = new JPanel();
        emptyPanel.setPreferredSize(new Dimension(0, 0));
        emptyPanel.setSize(0, 0);
        getComponentControlled().getPlayListTabbedPane().addTab(name, emptyPanel);
        
        // Use custom tab component if close buttons are not supported by look and feel
        if (!LookAndFeelSelector.getInstance().getCurrentLookAndFeel().isTabCloseButtonsSupported()) {        
        	getComponentControlled().getPlayListTabbedPane().setTabComponentAt(getComponentControlled().getPlayListTabbedPane().indexOfComponent(emptyPanel),
        			new ButtonTabComponent(name, getComponentControlled().getPlayListTabbedPane()));
        }
    }

    @Override
    protected void notifyReload() {
        // Nothing to do
    }

    /**
     * Rename play list.
     * 
     * @param index
     *            the index
     * @param newName
     *            the new name
     */
    void renamePlayList(int index, String newName) {
        getComponentControlled().getPlayListTabbedPane().setTitleAt(index, newName);
        ((ButtonTabComponent) getComponentControlled().getPlayListTabbedPane().getTabComponentAt(index)).getLabel().setText(newName);
    }

    /**
     * Return names of play lists.
     * 
     * @return the names of play lists
     */
    List<String> getNamesOfPlayLists() {
        List<String> result = new ArrayList<String>();
        for (int i = 0; i < getComponentControlled().getPlayListTabbedPane().getTabCount(); i++) {
            result.add(getComponentControlled().getPlayListTabbedPane().getTitleAt(i));
        }
        return result;
    }

    /**
     * Returns selected play list index
     * 
     * @return
     */
    int getSelectedTabIndex() {
        return getComponentControlled().getPlayListTabbedPane().getSelectedIndex();
    }

    /**
     * Returns name of play list at given position
     * 
     * @param index
     * @return
     */
    String getPlayListName(int index) {
        return getComponentControlled().getPlayListTabbedPane().getTitleAt(index);
    }

    void switchPlayListTabs(int draggedTabIndex, int targetTabIndex) {
        JTabbedPane tabPane = getComponentControlled().getPlayListTabbedPane();
        boolean isForwardDrag = targetTabIndex > draggedTabIndex;
        int index = isForwardDrag ? targetTabIndex + 1 : targetTabIndex;
        String titleAt = tabPane.getTitleAt(draggedTabIndex);
        tabPane.insertTab(titleAt, tabPane.getIconAt(draggedTabIndex), tabPane.getComponentAt(draggedTabIndex), tabPane.getToolTipTextAt(draggedTabIndex), index);
        tabPane.setTabComponentAt(targetTabIndex, new ButtonTabComponent(titleAt, tabPane));
    }
}
