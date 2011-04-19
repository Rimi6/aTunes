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

package net.sourceforge.atunes.gui.views.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;

import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.gui.views.controls.playerControls.KaraokeButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.MuteButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.NextButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.NormalizationButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.PlayPauseButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.PreviousButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.ProgressSlider;
import net.sourceforge.atunes.gui.views.controls.playerControls.RepeatButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.SecondaryControl;
import net.sourceforge.atunes.gui.views.controls.playerControls.ShuffleButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.StopButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.VolumeLevel;
import net.sourceforge.atunes.gui.views.controls.playerControls.VolumeSlider;
import net.sourceforge.atunes.utils.GuiUtils;

/**
 * The player controls panel.
 */
public final class PlayerControlsPanel extends JPanel {

    private static final long serialVersionUID = -8647737014195638177L;

    /************************************************ PANEL CONSTANTS ******************************************************/

    /**
     * Size of main controls by standard layout (not Substance)
     */
    public static final Dimension DEFAULT_BUTTONS_SIZE = new Dimension(35, 35);

    /** Size of play / pause button */
    public static final Dimension PLAY_BUTTON_SIZE = LookAndFeelSelector.getInstance().getCurrentLookAndFeel().isCustomPlayerControlsSupported() ? new Dimension(45, 45)
            : DEFAULT_BUTTONS_SIZE;

    /** Size of previous and next buttons */
    public static final Dimension PREVIOUS_NEXT_BUTTONS_SIZE = LookAndFeelSelector.getInstance().getCurrentLookAndFeel().isCustomPlayerControlsSupported() ? new Dimension(62, 30)
            : DEFAULT_BUTTONS_SIZE;

    /** Size of stop and mute buttons */
    public static final Dimension STOP_MUTE_BUTTONS_SIZE = LookAndFeelSelector.getInstance().getCurrentLookAndFeel().isCustomPlayerControlsSupported() ? new Dimension(30, 26)
            : DEFAULT_BUTTONS_SIZE;

    /** Size of shuffle, repeat, ... buttons */
    public static final Dimension OTHER_BUTTONS_SIZE = new Dimension(25, 23);

    /** Height of progress bar when has no ticks */
    private static final int PROGRESS_BAR_NO_TICKS_HEIGHT = 26;

    /** Height of progress bar when has ticks */
    private static final int PROGRESS_BAR_TICKS_HEIGHT = 40;

    /** Horizontal margin of player controls */
    private static final int PLAYER_CONTROLS_HORIZONTAL_MARGIN = 25;

    /** Vertical margin of player controls */
    private static final int PLAYER_CONTROLS_VERTICAL_MARGIN = 10;

    /************************************************ PANEL CONSTANTS ******************************************************/

    private ShuffleButton shuffleButton;
    private RepeatButton repeatButton;
    private KaraokeButton karaokeButton;
    private NormalizationButton normalizeButton;
    private PreviousButton previousButton;
    private PlayPauseButton playButton;
    private StopButton stopButton;
    private NextButton nextButton;
    private MuteButton volumeButton;
    private VolumeSlider volumeSlider;
    private VolumeLevel volumeLevel;
    private boolean playing;
    private ProgressSlider progressSlider;
    private JPanel secondaryControls;

    /**
     * Instantiates a new player controls panel.
     */
    public PlayerControlsPanel() {
        super(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder());
        addContent();
        GuiUtils.applyComponentOrientation(this);
    }

    /**
     * Adds the content.
     */
    private void addContent() {
        progressSlider = new ProgressSlider();

        JPanel sliderContainer = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 0, 3, PLAYER_CONTROLS_HORIZONTAL_MARGIN);
        sliderContainer.add(progressSlider, c);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setMinimumSize(new Dimension(10, PLAY_BUTTON_SIZE.height + PLAYER_CONTROLS_VERTICAL_MARGIN * 2));
        JPanel mainControlsPanel = getMainControlsPanel();
        bottomPanel.add(mainControlsPanel, BorderLayout.WEST);
        bottomPanel.add(sliderContainer, BorderLayout.CENTER);
        bottomPanel.add(getSecondaryControls(), BorderLayout.EAST);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 0;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(PLAYER_CONTROLS_VERTICAL_MARGIN, PLAYER_CONTROLS_HORIZONTAL_MARGIN, PLAYER_CONTROLS_VERTICAL_MARGIN, PLAYER_CONTROLS_HORIZONTAL_MARGIN);
        add(bottomPanel, c);
        
        setMinimumSize(new Dimension(10, PLAY_BUTTON_SIZE.height + PLAYER_CONTROLS_VERTICAL_MARGIN * 2));
    }

    public ProgressSlider getProgressSlider() {
        return progressSlider;
    }

    public void setProgress(long time, long remainingTime) {
        progressSlider.setProgress(time, remainingTime);
    }

    public JToggleButton getVolumeButton() {
        return volumeButton;
    }

    public JSlider getVolumeSlider() {
        return volumeSlider;
    }

    public JLabel getVolumeLevel() {
        return volumeLevel;
    }

    public NormalizationButton getNormalizeButton() {
        return normalizeButton;
    }

    public boolean isPlaying() {
        return playing;
    }

    protected static void setButton(JPanel panel, JComponent b, GridBagConstraints c) {
        panel.add(b, c);
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
        playButton.setPlaying(playing);
    }

    public void setShowTicksAndLabels(boolean showTicks) {
        getProgressSlider().setPaintLabels(showTicks);
        getProgressSlider().setPaintTicks(showTicks);
        getProgressSlider().setPreferredSize(new Dimension(getProgressSlider().getPreferredSize().width, showTicks ? PROGRESS_BAR_TICKS_HEIGHT : PROGRESS_BAR_NO_TICKS_HEIGHT));
    }

    private JPanel getMainControlsPanel() {
        previousButton = new PreviousButton(PREVIOUS_NEXT_BUTTONS_SIZE);
        playButton = new PlayPauseButton(PLAY_BUTTON_SIZE);
        stopButton = new StopButton(STOP_MUTE_BUTTONS_SIZE);
        nextButton = new NextButton(PREVIOUS_NEXT_BUTTONS_SIZE);
        volumeButton = new MuteButton(STOP_MUTE_BUTTONS_SIZE);
        volumeButton.setText("");
        volumeSlider = new VolumeSlider();
        volumeLevel = new VolumeLevel();
        return getPanelWithPlayerControls(stopButton, previousButton, playButton, nextButton, volumeButton, volumeSlider, volumeLevel);
    }

    /**
     * Return a panel with all player buttons. This method is shared with full
     * screen window
     * 
     * @param stopButton
     * @param previousButton
     * @param playButton
     * @param nextButton
     * @param volumeButton
     * @param volumeSlider
     * @param volumeLevel
     * @return
     */
    public static JPanel getPanelWithPlayerControls(StopButton stopButton, PreviousButton previousButton, PlayPauseButton playButton, NextButton nextButton, MuteButton volumeButton, JSlider volumeSlider, JLabel volumeLevel) {
        return LookAndFeelSelector.getInstance().getCurrentLookAndFeel().isCustomPlayerControlsSupported() ? getCustomPlayerControls(stopButton, previousButton, playButton,
                nextButton, volumeButton, volumeSlider, volumeLevel) : getStandardPlayerControls(stopButton, previousButton, playButton, nextButton, volumeButton, volumeSlider,
                volumeLevel);
    }

    /**
     * Returns custom panel with controls (used by Substance LAF)
     * 
     * @param stopButton
     * @param previousButton
     * @param playButton
     * @param nextButton
     * @param volumeButton
     * @param volumeSlider
     * @param volumeLevel
     * @return
     */
    private static JPanel getCustomPlayerControls(StopButton stopButton, PreviousButton previousButton, PlayPauseButton playButton, NextButton nextButton, MuteButton volumeButton, JSlider volumeSlider, JLabel volumeLevel) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(0, 0, 0, 0);
        setButton(panel, stopButton, c);
        c.gridx = 1;
        c.insets = new Insets(0, -6, 0, 0);
        setButton(panel, previousButton, c);
        c.gridx = 2;
        c.insets = new Insets(-1, -16, 0, 0);
        setButton(panel, playButton, c);
        c.gridx = 3;
        c.insets = new Insets(0, -16, 0, 0);
        setButton(panel, nextButton, c);
        if (volumeButton != null && volumeSlider != null && volumeLevel != null) {
            c.gridx = 4;
            c.insets = new Insets(0, -7, 0, 0);
            panel.add(volumeButton, c);
            c.gridx = 5;
            c.weightx = 0;
            c.fill = GridBagConstraints.NONE;
            c.insets = new Insets(0, 0, 3, 0);
            panel.add(volumeSlider, c);
            c.gridx = 6;
            panel.add(volumeLevel, c);
        }
        return panel;
    }

    /**
     * Returns standard panel with controls
     * 
     * @param stopButton
     * @param previousButton
     * @param playButton
     * @param nextButton
     * @param volumeButton
     * @param volumeSlider
     * @param volumeLevel
     * @return
     */
    private static JPanel getStandardPlayerControls(StopButton stopButton, PreviousButton previousButton, PlayPauseButton playButton, NextButton nextButton, MuteButton volumeButton, JSlider volumeSlider, JLabel volumeLevel) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        setButton(panel, stopButton, c);
        c.gridx = 1;
        setButton(panel, previousButton, c);
        c.gridx = 2;
        setButton(panel, playButton, c);
        c.gridx = 3;
        setButton(panel, nextButton, c);
        if (volumeButton != null && volumeSlider != null && volumeLevel != null) {
            c.gridx = 4;
            panel.add(volumeButton, c);
            c.gridx = 5;
            c.weightx = 0;
            c.fill = GridBagConstraints.NONE;
            c.insets = new Insets(0, 10, 0, 0);
            volumeSlider.setMinimumSize(new Dimension(50, 20));
            panel.add(volumeSlider, c);
            c.gridx = 6;
            panel.add(volumeLevel, c);
        }
        return panel;
    }

    private JPanel getSecondaryControls() {
        if (secondaryControls == null) {
            shuffleButton = new ShuffleButton();
            repeatButton = new RepeatButton();
            karaokeButton = new KaraokeButton();
            normalizeButton = new NormalizationButton();

            secondaryControls = new JPanel(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;
            c.insets = new Insets(0, 5, 0, 0);
            secondaryControls.add(shuffleButton, c);
            c.gridx = 1;
            c.insets = new Insets(0, 1, 0, 0);
            secondaryControls.add(repeatButton, c);
            c.gridx = 2;
            secondaryControls.add(karaokeButton, c);
            c.gridx = 3;
            secondaryControls.add(normalizeButton, c);
        }
        return secondaryControls;
    }

    /**
     * Adds a secondary control
     * 
     * @param button
     */
    public void addSecondaryControl(Action action) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = getSecondaryControls().getComponentCount();
        c.gridy = 0;
        c.insets = new Insets(0, 1, 0, 0);
        JToggleButton button = new SecondaryControl(action);
        getSecondaryControls().add(button, c);
    }

}