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

package net.sourceforge.atunes.kernel.modules.tags;

import java.io.File;
import java.io.IOException;

import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectReader;
import net.sourceforge.atunes.utils.Logger;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

/**
 * Reads the tag of an audio file using JAudiotagger.
 * 
 * @author fleax
 */
public final class TagReader implements ILocalAudioObjectReader {

	private Genres genresHelper;

	private RatingsToStars ratingsToStars;

	/**
	 * @param ratingsToStars
	 */
	public void setRatingsToStars(final RatingsToStars ratingsToStars) {
		this.ratingsToStars = ratingsToStars;
	}

	/**
	 * @param genresHelper
	 */
	public void setGenresHelper(final Genres genresHelper) {
		this.genresHelper = genresHelper;
	}

	@Override
	public void readAudioObject(final ILocalAudioObject ao,
			final boolean readAudioProperties) {
		AudioFile f = getAudioFile(ao.getFile());
		if (f != null) {
			// Set audio properties
			setAudioProperties(ao, readAudioProperties, f);
			// Set tag
			setTag(ao, f);
		}
	}

	/**
	 * @param ao
	 * @param f
	 */
	private void setTag(final ILocalAudioObject ao, final AudioFile f) {
		Tag tag = f.getTag();
		if (tag != null) {
			ao.setTag(new DefaultTag(tag, this.genresHelper,
					this.ratingsToStars));
		}
	}

	/**
	 * Reads audio properties
	 * 
	 * @param ao
	 * @param readAudioProperties
	 * @param f
	 */
	private void setAudioProperties(final ILocalAudioObject ao,
			final boolean readAudioProperties, final AudioFile f) {
		if (readAudioProperties) {
			AudioHeader header = f.getAudioHeader();
			if (header != null) {
				ao.setDuration(header.getTrackLength());
				ao.setBitrate(header.getBitRateAsNumber());
				ao.setFrequency(header.getSampleRateAsNumber());
			}
		}
	}

	/**
	 * Reads file with jaudiotagger
	 * 
	 * @param file
	 * @return
	 */
	private AudioFile getAudioFile(final File file) {
		AudioFile audioFile = null;
		try {
			audioFile = AudioFileIO.read(file);
		} catch (NegativeArraySizeException e) {
			// Reading wrong tags can happen this
			// java.lang.NegativeArraySizeException: null
			// org.jaudiotagger.tag.id3.ID3Compression.uncompress(ID3Compression.java:38)
			logExceptionReadingTag(file, e);
		} catch (NumberFormatException e) {
			// With fields with very big numbers this exception can be thrown
			logExceptionReadingTag(file, e);
		} catch (IllegalArgumentException e) {
			// Reading wrong tags can happen this
			// java.lang.IllegalArgumentException: null
			// java.nio.Buffer.position(Unknown Source)
			// org.jaudiotagger.tag.id3.ID3v23Frame.read(ID3v23Frame.java:460)
			logExceptionReadingTag(file, e);
		} catch (CannotReadException e) {
			logExceptionReadingTag(file, e);
		} catch (IOException e) {
			logExceptionReadingTag(file, e);
		} catch (TagException e) {
			logExceptionReadingTag(file, e);
		} catch (ReadOnlyFileException e) {
			logExceptionReadingTag(file, e);
		} catch (InvalidAudioFrameException e) {
			logExceptionReadingTag(file, e);
		}
		return audioFile;
	}

	private void logExceptionReadingTag(final File file, Exception e) {
		Logger.error(net.sourceforge.atunes.utils.FileUtils.getPath(file));
		Logger.error(e.getMessage());
	}
}
