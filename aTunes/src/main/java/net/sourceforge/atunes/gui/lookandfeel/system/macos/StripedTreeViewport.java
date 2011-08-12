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
import java.awt.Graphics;

import javax.swing.JTree;
import javax.swing.JViewport;

class StripedTreeViewport extends JViewport {

    /**
	 * 
	 */
	private static final long serialVersionUID = 7217178968532613985L;
	
	private final JTree tree;

    public StripedTreeViewport(JTree tree) {
        this.tree = tree;
        setOpaque(false);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        paintStripedBackground(g);
        super.paintComponent(g);
    }

    private void paintStripedBackground(Graphics g) {
        // get the row index at the top of the clip bounds (the first row
        // to paint).
        int rowAtPoint = tree.getRowForLocation(g.getClipBounds().getLocation().x, g.getClipBounds().getLocation().y);
        // get the y coordinate of the first row to paint. if there are no
        // rows in the tree, start painting at the top of the supplied
        // clipping bounds.
        int topY = rowAtPoint < 0
                ? g.getClipBounds().y : tree.getRowBounds(rowAtPoint).y;

        // create a counter variable to hold the current row. if there are no
        // rows in the tree, start the counter at 0.
        int currentRow = rowAtPoint < 0 ? 0 : rowAtPoint;
        while (topY < g.getClipBounds().y + g.getClipBounds().height) {
            int bottomY = topY + tree.getRowHeight();
            g.setColor(getRowColor(currentRow, rowAtPoint));
            g.fillRect(g.getClipBounds().x, topY, g.getClipBounds().width, bottomY);
            topY = bottomY;
            currentRow ++;
        }
    }

    private Color getRowColor(int row, int rowAtPoint) {
        return row % 2 == 0 ? MacOSColors.EVEN_ROW_COLOR : getBackground();
    }
}
