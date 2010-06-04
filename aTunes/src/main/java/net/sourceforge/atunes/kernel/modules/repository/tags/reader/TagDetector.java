/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

package net.sourceforge.atunes.kernel.modules.repository.tags.reader;

import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.tags.tag.DefaultTag;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;

import org.jaudiotagger.audio.AudioHeader;

/**
 * Reads the tag of an audio file using JAudiotagger.
 * 
 * @author fleax
 */
public final class TagDetector {

    /** The logger. */
    private static Logger logger;

    private TagDetector() {

    }

    /**
     * Calls appropriate tag reader and sends tag to AudioFile class.
     * 
     * @param file
     *            File to be read
     * @return reference to jaudiotagger file
     */
    public static void readInformation(AudioFile file, boolean readAudioProperties) {
        getLogger().debug(LogCategories.FILE_READ, file);

        try {
        	org.jaudiotagger.audio.AudioFile f = org.jaudiotagger.audio.AudioFileIO.read(file.getFile());
            org.jaudiotagger.tag.Tag tag = f.getTag();

            if (tag != null) {
                file.setTag(new DefaultTag(tag));
            }
            
            if (readAudioProperties) {
                AudioHeader header = f.getAudioHeader();
                if (header != null) {
                	file.setDuration(header.getTrackLength());
                	file.setBitrate(header.getBitRateAsNumber());
                	file.setFrequency(header.getSampleRateAsNumber());
                }
            }

        } catch (Exception e) {
            getLogger().error(LogCategories.FILE_READ, e.getMessage());
        }
    }

    /**
     * Getter for logger
     * 
     * @return
     */
    private static Logger getLogger() {
        if (logger == null) {
            logger = new Logger();
        }
        return logger;
    }

}
