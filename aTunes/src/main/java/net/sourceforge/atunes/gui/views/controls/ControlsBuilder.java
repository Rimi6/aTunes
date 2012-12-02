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

package net.sourceforge.atunes.gui.views.controls;

import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyleConstants;

import net.sourceforge.atunes.gui.AbstractCommonColumnModel;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IButtonPanel;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.ILocaleBean;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IStateCore;
import net.sourceforge.atunes.utils.ClipboardFacade;

/**
 * Factory to build UI components
 * 
 * @author alex
 * 
 */
public class ControlsBuilder implements IControlsBuilder {

	private static final JTextComponent.KeyBinding[] MAC_OS_BINDINGS = {
			new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_C,
					InputEvent.CTRL_MASK),
					DefaultEditorKit.defaultKeyTypedAction),
			new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_V,
					InputEvent.CTRL_MASK),
					DefaultEditorKit.defaultKeyTypedAction),
			new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_X,
					InputEvent.CTRL_MASK),
					DefaultEditorKit.defaultKeyTypedAction),
			new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_A,
					InputEvent.CTRL_MASK),
					DefaultEditorKit.defaultKeyTypedAction),
			new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(
					KeyEvent.VK_LEFT, InputEvent.CTRL_MASK),
					DefaultEditorKit.defaultKeyTypedAction),
			new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(
					KeyEvent.VK_RIGHT, InputEvent.CTRL_MASK),
					DefaultEditorKit.defaultKeyTypedAction),

			new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_C,
					InputEvent.META_MASK), DefaultEditorKit.copyAction),
			new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_V,
					InputEvent.META_MASK), DefaultEditorKit.pasteAction),
			new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_X,
					InputEvent.META_MASK), DefaultEditorKit.cutAction),
			new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_A,
					InputEvent.META_MASK), DefaultEditorKit.selectAllAction),
			new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(
					KeyEvent.VK_LEFT, InputEvent.META_MASK),
					DefaultEditorKit.beginLineAction),
			new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(
					KeyEvent.VK_RIGHT, InputEvent.META_MASK),
					DefaultEditorKit.endLineAction) };

	private IOSManager osManager;

	private ILookAndFeelManager lookAndFeelManager;

	private ClipboardFacade clipboard;

	private IBeanFactory beanFactory;

	private IDialogFactory dialogFactory;

	/** The component orientation. */
	private ComponentOrientation componentOrientation;

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(final IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param clipboard
	 */
	public void setClipboard(final ClipboardFacade clipboard) {
		this.clipboard = clipboard;
	}

	/**
	 * @param lookAndFeelManager
	 */
	public void setLookAndFeelManager(
			final ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}

	/**
	 * @param osManager
	 */
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	@Override
	public JTextArea createTextArea() {
		JTextArea textArea = new JTextArea();
		new EditionPopUpMenu(textArea, this.clipboard, this.osManager);
		return textArea;
	}

	@Override
	public JTextField createTextField() {
		JTextField textField = new JTextField();
		initializeTextField(textField);
		new EditionPopUpMenu(textField, this.clipboard, this.osManager);
		return textField;
	}

	@Override
	public JTextPane createTextPane(final Integer alignJustified) {
		CustomTextPane textPane = new CustomTextPane(alignJustified, this);
		// Register look and feel change listener
		this.lookAndFeelManager.addLookAndFeelChangeListener(textPane);
		new EditionPopUpMenu(textPane, this.clipboard, this.osManager);
		return textPane;
	}

	@Override
	public JTextPane createReadOnlyTextPane(final String text) {
		JTextPane textPane = createTextPane(null);
		textPane.setEditable(false);
		textPane.setBorder(BorderFactory.createEmptyBorder());
		textPane.setOpaque(false);
		textPane.setText(text);
		return textPane;
	}

	/**
	 * Sets custom properties to text fields
	 * 
	 * @param customTextField
	 */
	private void initializeTextField(final JTextField customTextField) {
		if (this.osManager.isMacOsX()) {
			JTextComponent.loadKeymap(customTextField.getKeymap(),
					MAC_OS_BINDINGS, customTextField.getActions());
		}
	}

	@Override
	public PlayPauseButton createPlayPauseButton() {
		return this.beanFactory.getBean(PlayPauseButton.class);
	}

	@Override
	public NextButton createNextButton() {
		return this.beanFactory.getBean(NextButton.class);
	}

	@Override
	public PreviousButton createPreviousButton() {
		return this.beanFactory.getBean(PreviousButton.class);
	}

	@Override
	public StopButton createStopButton() {
		return this.beanFactory.getBean(StopButton.class);
	}

	@Override
	public void applyComponentOrientation(final Container... containers) {
		if (this.componentOrientation == null) {
			setComponentOrientation();
		}
		for (Container container : containers) {
			container.applyComponentOrientation(this.componentOrientation);
		}
	}

	@Override
	public ComponentOrientation getComponentOrientation() {
		if (this.componentOrientation == null) {
			setComponentOrientation();
		}
		return this.componentOrientation;
	}

	/**
	 * Returns the component orientation as a SwingConstant.
	 * 
	 * @param locale
	 * @return The component orientation as a SwingConstant
	 */
	@Override
	public int getComponentOrientationAsSwingConstant() {
		if (this.componentOrientation == null) {
			setComponentOrientation();
		}
		return this.componentOrientation.isLeftToRight() ? SwingConstants.LEFT
				: SwingConstants.RIGHT;
	}

	/**
	 * Returns the component orientation as a text style constant.
	 * 
	 * @return The component orientation as a SwingConstant
	 */
	@Override
	public int getComponentOrientationAsTextStyleConstant() {
		if (this.componentOrientation == null) {
			setComponentOrientation();
		}
		return this.componentOrientation.isLeftToRight() ? StyleConstants.ALIGN_LEFT
				: StyleConstants.ALIGN_RIGHT;
	}

	/**
	 * Sets the component orientation.
	 * 
	 * @param locale
	 */
	private void setComponentOrientation() {
		this.componentOrientation = ComponentOrientation.LEFT_TO_RIGHT;
		ILocaleBean locale = this.beanFactory.getBean(IStateCore.class)
				.getLocale();
		if (locale != null) {
			if ("ug".equalsIgnoreCase(locale.getLocale().getLanguage())) {
				this.componentOrientation = ComponentOrientation.RIGHT_TO_LEFT;
			} else {
				this.componentOrientation = ComponentOrientation
						.getOrientation(locale.getLocale());
			}
		}
	}

	@Override
	public PopUpButton createPopUpButton(final int menuPosition) {
		return new PopUpButton(menuPosition, this.lookAndFeelManager, this);
	}

	@Override
	public JSplitPane createSplitPane(final int type) {
		return new CustomSplitPane(type, this);
	}

	@Override
	public int getSplitPaneDividerSize() {
		return new CustomSplitPane(JSplitPane.HORIZONTAL_SPLIT, this)
				.getDividerSize();
	}

	@Override
	public ColumnSetPopupMenu createColumnSetPopupMenu(final JTable table,
			final AbstractCommonColumnModel columnModel) {
		return new ColumnSetPopupMenu(table, columnModel, this.dialogFactory,
				this.osManager);
	}

	@Override
	public IButtonPanel createButtonPanel() {
		ToggleButtonFlowPanel panel = this.beanFactory
				.getBean(ToggleButtonFlowPanel.class);
		this.lookAndFeelManager.addLookAndFeelChangeListener(panel);
		return panel;
	}
}
