/*
 * aTunes 2.1.0-SNAPSHOT
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

package net.sourceforge.atunes.gui.lookandfeel.system.macos;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Paint;

import javax.swing.BorderFactory;
import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.gui.lookandfeel.system.SystemLookAndFeel;

public class MacOSXLookAndFeel extends SystemLookAndFeel {

    private static final CellRendererPane CELL_RENDER_PANE = new CellRendererPane();

	@Override
	public Dimension getPopUpButtonSize() {
		return new Dimension(40, 10);
	}
	
	@Override
	public void initializeLookAndFeel() {
		super.initializeLookAndFeel();
		UIManager.put("TableHeader.cellBorder", BorderFactory.createLineBorder(MacOSColors.TABLE_HEADER_COLOR));
		UIManager.put("TableHeader.background", UIManager.get("Panel.background"));
	}
	
	@Override
	public void initializeFonts(Font baseFont) {
		// Don't do any font initialization, keep original fonts
	}
	
	/**
	 * Returns paint to be used with certain controls (player controls)
	 * @return
	 */
	public Paint getPaintForSpecialControls() {
		return new Color(20, 20, 20, 180); 
	}
	
	@Override
	public int getSplitPaneDividerSize() {
		return 1; // Must be enough size to move easily with mouse
	}
	
	@Override
	public JScrollPane getTableScrollPane(JTable table) {
		table.setOpaque(false);
		table.setBorder(BorderFactory.createEmptyBorder());
        JScrollPane scrollPane =  getScrollPane(table);
        scrollPane.setViewport(new StripedTableViewport(table));
        scrollPane.getViewport().setView(table);
        scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, createCornerComponent(table));
        return scrollPane;
	}
	
	@Override
	public JScrollPane getTreeScrollPane(final JTree tree) {
		tree.setOpaque(false);
		tree.setBorder(BorderFactory.createEmptyBorder());
        JScrollPane scrollPane =  getScrollPane(tree);
        scrollPane.setViewport(new StripedTreeViewport(tree));
        scrollPane.getViewport().setView(tree);
        return scrollPane;
	}
	
    @Override
    public JScrollPane getListScrollPane(JList list) {
    	list.setOpaque(false);
    	list.setBorder(BorderFactory.createEmptyBorder());
        JScrollPane scrollPane =  getScrollPane(list);
        scrollPane.setViewport(new StripedListViewport(list));
        scrollPane.getViewport().setView(list);
    	return scrollPane;
    }
    
    @Override
    public JScrollPane getScrollPane(Component component) {    	
    	JScrollPane scrollPane = new JScrollPane(component);
    	scrollPane.setBorder(BorderFactory.createLineBorder(MacOSColors.SEPARATOR_COLOR));
    	return scrollPane;
    }
    

	
    /**
     * Creates a component that paints the header background for use in a
     * JScrollPane corner.
     */
    private static JComponent createCornerComponent(final JTable table) {
        return new JComponent() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            protected void paintComponent(Graphics g) {
                paintHeader(g, table, 0, getWidth());
            }
        };
    }

    /**
     * Paints the given JTable's table default header background at given
     * x for the given width.
     */
    private static void paintHeader(Graphics g, JTable table, int x, int width) {
        TableCellRenderer renderer = table.getTableHeader().getDefaultRenderer();
        Component component = renderer.getTableCellRendererComponent(
                table, "", false, false, -1, 2);

        component.setBounds(0,0,width, table.getTableHeader().getHeight());

        ((JComponent)component).setOpaque(false);
        CELL_RENDER_PANE.paintComponent(g, component, null, x, 0,
                width, table.getTableHeader().getHeight(), true);
    }

    @Override
    public JTable getTable() {
    	JTable table = new JTable();
        table.setDefaultRenderer(Object.class, getTableCellRenderer(null));
        table.setShowGrid(false);
        return table;
    }    

    @Override
    public JList getList() {
    	JList list = new JList();
        list.setCellRenderer(LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getListCellRenderer(null));
    	return list;
    }
    
    @Override
    public Font getPlayListFont() {
    	return null;
    }
    
    @Override
    public Font getPlayListSelectedItemFont() {
    	return null;
    }


}
