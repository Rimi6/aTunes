/*
 * aTunes 2.2.0-SNAPSHOT
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

package net.sourceforge.atunes.model;

import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import net.sourceforge.atunes.gui.views.controls.NextButton;
import net.sourceforge.atunes.gui.views.controls.PlayPauseButton;
import net.sourceforge.atunes.gui.views.controls.PreviousButton;
import net.sourceforge.atunes.gui.views.controls.StopButton;

/**
 * Interface to build UI components
 * 
 * @author alex
 * 
 */
public interface IControlsBuilder {

	/**
	 * @return text area
	 */
	JTextArea createTextArea();

	/**
	 * @return text field
	 */
	JTextField createTextField();

	/**
	 * @param alignJustified
	 * @return text pane
	 */
	JTextPane createTextPane(Integer alignJustified);

	/**
	 * @param text
	 * @return text pane with no edition capabilities
	 */
	JTextPane createReadOnlyTextPane(String text);

	/**
	 * @return play button
	 */
	PlayPauseButton createPlayPauseButton();

	/**
	 * @return previous button
	 */
	PreviousButton createPreviousButton();

	/**
	 * @return next button
	 */
	NextButton createNextButton();

	/**
	 * @return stop button
	 */
	StopButton createStopButton();

}
