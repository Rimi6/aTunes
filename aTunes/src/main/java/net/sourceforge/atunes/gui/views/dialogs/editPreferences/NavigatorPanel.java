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

package net.sourceforge.atunes.gui.views.dialogs.editPreferences;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.tags.IncompleteTagsChecker;
import net.sourceforge.atunes.kernel.modules.tags.TagAttribute;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.I18nUtils;

public final class NavigatorPanel extends AbstractPreferencesPanel {

    private static final long serialVersionUID = -4315748284461119970L;

    private JCheckBox showFavorites;
    private JCheckBox showExtendedToolTip;
    private JComboBox extendedToolTipDelay;
    private JCheckBox useSmartTagViewSorting;
    private JCheckBox useArtistNamesSorting;

    /**
     * Check box to highlight elements with incomplete tags (selected) or not
     * (unselected)
     */
    private JCheckBox highlightElementsWithIncompleteBasicTags;

    /**
     * Table to select which tag attributes are used to highlight incomplete tag
     * folders
     */
    private JTable highlighTagAttributesTable;

    /**
     * Scroll pane fot tag attributes
     */
    private JScrollPane highlightTagAttributesScrollPane;

    /**
     * Table model to select tag attributes
     */
    private TagAttributesTableModel tagAttributesTableModel;

    /**
     * The Class TagAttributesTableModel.
     */
    private static class TagAttributesTableModel implements TableModel {

        private static final long serialVersionUID = 5251001708812824836L;

        /** The tag attributes hash map. */
        private Map<TagAttribute, Boolean> tagAttributes;

        /** The listeners. */
        private List<TableModelListener> listeners = new ArrayList<TableModelListener>();

        /**
         * Instantiates a new tag attributes table model
         */
        TagAttributesTableModel() {
            // Nothing to do
        }

        @Override
        public void addTableModelListener(TableModelListener l) {
            listeners.add(l);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnIndex == 0 ? Boolean.class : String.class;
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public String getColumnName(int column) {
            return "";
        }

        @Override
        public int getRowCount() {
            if (this.tagAttributes != null) {
                return this.tagAttributes.size();
            }
            return 0;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                return tagAttributes.get(TagAttribute.values()[rowIndex]);
            }
            return I18nUtils.getString(TagAttribute.values()[rowIndex].name());
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 0;
        }

        @Override
        public void removeTableModelListener(TableModelListener l) {
            listeners.remove(l);
        }

        /**
         * Sets the columns.
         * 
         * @param columns
         *            the new columns
         */
        public void setTagAttributes(Map<TagAttribute, Boolean> attrs) {
            this.tagAttributes = attrs;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                tagAttributes.put(TagAttribute.values()[rowIndex], (Boolean) aValue);
            }
        }

        /**
         * @return the tagAttributes selected by user
         */
        public List<TagAttribute> getSelectedTagAttributes() {
            List<TagAttribute> result = new ArrayList<TagAttribute>();
            for (TagAttribute attr : tagAttributes.keySet()) {
                if (tagAttributes.get(attr)) {
                    result.add(attr);
                }
            }
            return result;
        }
    }

    /**
     * Instantiates a new navigator panel.
     */
    public NavigatorPanel() {
        super(I18nUtils.getString("NAVIGATOR"));
        showFavorites = new JCheckBox(I18nUtils.getString("SHOW_FAVORITES"));
        showExtendedToolTip = new JCheckBox(I18nUtils.getString("SHOW_EXTENDED_TOOLTIP"));
        final JLabel label = new JLabel(I18nUtils.getString("EXTENDED_TOOLTIP_DELAY"));
        extendedToolTipDelay = new JComboBox(new Integer[] { 1, 2, 3, 4, 5 });
        showExtendedToolTip.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                label.setEnabled(showExtendedToolTip.isSelected());
                extendedToolTipDelay.setEnabled(showExtendedToolTip.isSelected());
            }
        });
        useSmartTagViewSorting = new JCheckBox(I18nUtils.getString("USE_SMART_TAG_VIEW_SORTING"));
        useArtistNamesSorting = new JCheckBox(I18nUtils.getString("USE_PERSON_NAMES_ARTIST_TAG_VIEW_SORTING"));
        highlightElementsWithIncompleteBasicTags = new JCheckBox(I18nUtils.getString("HIGHLIGHT_INCOMPLETE_TAG_ELEMENTS"));
        highlightElementsWithIncompleteBasicTags.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        highlightTagAttributesScrollPane.setEnabled(highlightElementsWithIncompleteBasicTags.isSelected());
                        highlighTagAttributesTable.setEnabled(highlightElementsWithIncompleteBasicTags.isSelected());
                    }
                });
            }
        });

        tagAttributesTableModel = new TagAttributesTableModel();
        tagAttributesTableModel.setTagAttributes(IncompleteTagsChecker.getAllTagAttributes());
        highlighTagAttributesTable = LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getTable();
        highlighTagAttributesTable.setModel(tagAttributesTableModel);
        highlighTagAttributesTable.setTableHeader(null);
        highlighTagAttributesTable.getColumnModel().getColumn(0).setMaxWidth(20);
        highlighTagAttributesTable.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JCheckBox()));
        highlighTagAttributesTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        highlighTagAttributesTable.setDefaultRenderer(String.class, LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getTableCellRenderer(
                GuiUtils.getComponentOrientationTableCellRendererCode()));

        highlightTagAttributesScrollPane = LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getTableScrollPane(highlighTagAttributesTable);
        highlightTagAttributesScrollPane.setMinimumSize(new Dimension(300, 150));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        add(showFavorites, c);
        c.gridy = 1;
        add(showExtendedToolTip, c);
        c.gridy = 2;
        c.insets = new Insets(0, 10, 0, 0);
        add(label, c);
        c.gridx = 1;
        c.weightx = 1;
        add(extendedToolTipDelay, c);
        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 3;
        c.insets = new Insets(0, 0, 0, 0);
        c.gridwidth = 2;
        add(useSmartTagViewSorting, c);
        c.gridy = 4;
        add(useArtistNamesSorting, c);
        c.gridy = 5;
        add(highlightElementsWithIncompleteBasicTags, c);
        c.gridy = 6;
        c.weighty = 1;
        c.weightx = 0;
        c.anchor = GuiUtils.getComponentOrientation().isLeftToRight() ? GridBagConstraints.NORTHWEST : GridBagConstraints.NORTHEAST;
        c.insets = new Insets(10, 20, 10, 10);
        add(highlightTagAttributesScrollPane, c);
    }

    @Override
    public boolean applyPreferences(ApplicationState state) {
        state.setShowFavoritesInNavigator(showFavorites.isSelected());
        state.setShowExtendedTooltip(showExtendedToolTip.isSelected());
        state.setExtendedTooltipDelay((Integer) extendedToolTipDelay.getSelectedItem());
        state.setUseSmartTagViewSorting(useSmartTagViewSorting.isSelected());
        state.setHighlightIncompleteTagElements(highlightElementsWithIncompleteBasicTags.isSelected());
        state.setHighlightIncompleteTagFoldersAttributes(tagAttributesTableModel.getSelectedTagAttributes());
        state.setUsePersonNamesArtistTagViewSorting(useArtistNamesSorting.isSelected());

        return false;
    }

    /**
     * Sets the album tool tip delay.
     * 
     * @param time
     *            the new album tool tip delay
     */

    public void setAlbumToolTipDelay(int time) {
        extendedToolTipDelay.setSelectedItem(time);
    }

    /**
     * Sets the show album tool tip.
     * 
     * @param show
     *            the new show album tool tip
     */
    public void setShowAlbumToolTip(boolean show) {
        showExtendedToolTip.setSelected(show);
    }

    /**
     * Sets the show favorites.
     * 
     * @param show
     *            the new show favorites
     */
    private void setShowFavorites(boolean show) {
        showFavorites.setSelected(show);
    }

    /**
     * Sets the use smart tag view sorting.
     * 
     * @param use
     *            the new use smart tag view sorting
     */
    private void setUseSmartTagViewSorting(boolean use) {
        useSmartTagViewSorting.setSelected(use);
    }

    /**
     * Sets the use person names artist tag view sorting.
     * 
     * @param use
     *            the new se person names artist tag view sorting
     */
    private void setUsePersonNamesArtistTagViewSorting(boolean use) {
        useArtistNamesSorting.setSelected(use);
    }

    /**
     * Sets property to highlight folder with incomplete tags
     * 
     * @param highlightFoldersWithIncompleteBasicTags
     *            the highlightFoldersWithIncompleteBasicTags to set
     */
    private void setHighlightFoldersWithIncompleteBasicTags(boolean highlightFoldersWithIncompleteBasicTags) {
        this.highlightElementsWithIncompleteBasicTags.setSelected(highlightFoldersWithIncompleteBasicTags);
        highlightTagAttributesScrollPane.setEnabled(highlightFoldersWithIncompleteBasicTags);
        highlighTagAttributesTable.setEnabled(highlightFoldersWithIncompleteBasicTags);
    }

    @Override
    public void updatePanel(ApplicationState state) {
        setShowFavorites(state.isShowFavoritesInNavigator());
        setShowAlbumToolTip(state.isShowExtendedTooltip());
        setAlbumToolTipDelay(state.getExtendedTooltipDelay());
        setUseSmartTagViewSorting(state.isUseSmartTagViewSorting());
        setHighlightFoldersWithIncompleteBasicTags(state.isHighlightIncompleteTagElements());
        setUsePersonNamesArtistTagViewSorting(state.isUsePersonNamesArtistTagViewSorting());
    }

    @Override
    public void validatePanel() throws PreferencesValidationException {
    }

    @Override
    public void dialogVisibilityChanged(boolean visible) {
        // Do nothing
    }

    @Override
    public void resetImmediateChanges(ApplicationState state) {
        // Do nothing
    }

}
