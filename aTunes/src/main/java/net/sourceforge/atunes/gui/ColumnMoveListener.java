/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import net.sourceforge.atunes.model.IColumn;

class ColumnMoveListener extends MouseAdapter {

	private AbstractCommonColumnModel model;

	public ColumnMoveListener(
			AbstractCommonColumnModel abstractCommonColumnModel) {
		this.model = abstractCommonColumnModel;
	}

	@Override
	public void mouseReleased(final MouseEvent e) {
		if (columnsMoved(this.model.getColumnBeingMoved(),
				this.model.getColumnMovedTo())) {
			move(this.model.getColumnBeingMoved(),
					this.model.getColumnMovedTo());
		}
		this.model.setColumnBeingMoved(-1);
		this.model.setColumnMovedTo(-1);
	}

	private void move(int columnBeingMoved, int columnMovedTo) {
		// Swap order in model
		// Column moved to right
		if (columnBeingMoved < columnMovedTo) {
			movingToRight(columnBeingMoved, columnMovedTo);
		} // Column moved to left
		else if (columnBeingMoved > columnMovedTo) {
			movingToLeft(columnBeingMoved, columnMovedTo);
		}

		this.model.arrangeColumns(false);
	}

	private boolean columnsMoved(int columnBeingMoved, int columnMovedTo) {
		return columnBeingMoved != -1 && columnMovedTo != -1
				&& columnBeingMoved != columnMovedTo;
	}

	private void movingToLeft(int columnBeingMoved, int columnMovedTo) {
		IColumn<?> destinyColumn = this.model.getColumnObject(columnMovedTo);
		if (destinyColumn != null) {
			int columnDestinyOrder = destinyColumn.getOrder();
			for (int i = columnBeingMoved - 1; i >= columnMovedTo; i--) {
				int order = this.model.getColumnObject(i).getOrder();
				this.model.getColumnObject(i).setOrder(order + 1);
			}
			this.model.getColumnObject(columnBeingMoved).setOrder(
					columnDestinyOrder);
		}
	}

	private void movingToRight(int columnBeingMoved, int columnMovedTo) {
		IColumn<?> destinyColumn = this.model.getColumnObject(columnMovedTo);
		if (destinyColumn != null) {
			int columnDestinyOrder = destinyColumn.getOrder();
			for (int i = columnBeingMoved + 1; i <= columnMovedTo; i++) {
				int order = this.model.getColumnObject(i).getOrder();
				this.model.getColumnObject(i).setOrder(order - 1);
			}
			this.model.getColumnObject(columnBeingMoved).setOrder(
					columnDestinyOrder);
		}
	}
}