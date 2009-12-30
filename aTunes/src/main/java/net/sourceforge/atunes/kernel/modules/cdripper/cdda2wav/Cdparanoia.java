/*
 * aTunes 2.0.0-SNAPSHOT
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
package net.sourceforge.atunes.kernel.modules.cdripper.cdda2wav;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.kernel.modules.cdripper.cdda2wav.model.CDInfo;
import net.sourceforge.atunes.misc.SystemProperties;
import net.sourceforge.atunes.misc.SystemProperties.OperatingSystem;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Detects CD drives, gathers CD information and rip CD's all using cdparanoia
 */
public class Cdparanoia extends CdToWavConverter {

    // Define cdparanoia command strings
    private static final String CDPARANOIA_COMMAND_STRING = "cdparanoia";
    private static final String VERSION = "-V";
    private static final String QUERY = "-Q";
    private static final String WAVFORMAT = "-w";

    Logger logger = new Logger();

    /**
     * Tests if cdparanoia is present
     * 
     * @return true if cdparanoia was found, false otherwise.
     */
    static boolean pTestTool() {
        // Cdparanoia doesn't work for windows yet.
        if (SystemProperties.OS != OperatingSystem.WINDOWS) {
            BufferedReader stdInput = null;
            try {
                ProcessBuilder pb = new ProcessBuilder(CDPARANOIA_COMMAND_STRING, VERSION);
                Process p = pb.start();
                stdInput = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                while (stdInput.readLine() != null) {
                    // do nothing
                }
                int code = p.waitFor();
                if (code != 0) {
                    return false;
                }
                return true;
            } catch (Exception e) {
                return false;
            } finally {
                ClosingUtils.close(stdInput);
            }
        } else {
            return false;
        }
    }

    /*
     * Override methods
     */
    @Override
    public boolean cdda2wav(int track, File file) {
        logger.info(LogCategories.CDPARANOIA, StringUtils.getString("Writing wav file for track ", track, " in file ", file.getName()));
        try {
            // fileName = new File(fileName.getName());
            file.getParentFile().mkdirs();

            List<String> command = new ArrayList<String>();
            command.add(CDPARANOIA_COMMAND_STRING);
            // command.add(VERBOSE);
            command.add(WAVFORMAT);
            command.add(String.valueOf(track));
            command.add(file.getAbsolutePath());

            logger.debugMethodCall(LogCategories.CDPARANOIA, command.toArray(new String[command.size()]));
            process = new ProcessBuilder(command).start();

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    listener.notifyProgress(-1);
                }
            });

            int code = process.waitFor();
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    listener.notifyProgress(100);
                }
            });

            if (code != 0) {
                logger.error(LogCategories.CDPARANOIA, StringUtils.getString("Process returned code ", code));
                return false;
            }

            logger.info(LogCategories.CDPARANOIA, "Wav file ok!!");
            return true;
        } catch (Exception e) {
            logger.error(LogCategories.CDPARANOIA, StringUtils.getString("Process execution caused exception ", e));
            return false;
        }
    }

    /**
     * Nothing needs to be done as we already use Paranoia
     */
    @Override
    public boolean cdda2wav(int track, File fileName, boolean useParanoia) {
        return cdda2wav(track, fileName);
    }

    @Override
    public CDInfo getCDInfo() {
        logger.info(LogCategories.CDPARANOIA, "Getting cd information...");

        try {
            // Prepare cdparanoia commands and execute
            List<String> command = new ArrayList<String>();
            command.add(CDPARANOIA_COMMAND_STRING);
            command.add(QUERY);

            logger.debugMethodCall(LogCategories.CDPARANOIA, command.toArray(new String[command.size()]));

            process = new ProcessBuilder(command).start();

            BufferedReader stdInput = null;
            boolean cdLoaded = false;
            try {
                stdInput = new BufferedReader(new InputStreamReader(process.getErrorStream(), "ISO8859_1"));
                logger.info(LogCategories.CDPARANOIA, "Trying to read cdparanoia stream");

                String s = null;
                int tracks = 0;
                String totalDuration = null;
                String id = null;
                //String album = null;
                //String artist = null;
                //String title = null;
                List<String> durations = new ArrayList<String>();
                List<String> titles = new ArrayList<String>();
                List<String> artists = new ArrayList<String>();
                List<String> composers = new ArrayList<String>();

                // read the output from the command
                int count = 0;
                while ((s = stdInput.readLine()) != null) {
                    logger.info(LogCategories.CDPARANOIA, StringUtils.getString("While loop: ", s));
                    if (s.startsWith("TOTAL")) {

                        break;
                    }

                    // CD is inserted, We can start reading the TOC after two
                    // readLines
                    if (s.contains("Table of contents")) {
                        cdLoaded = true;
                    }
                    if (cdLoaded) {
                        count++;
                    }

                    // Get album info (only if connection to cddb could be
                    // established)
                    if (count > 3) {
                        tracks++;
                        StringTokenizer stringTokenizer = new java.util.StringTokenizer(s, " ");
                        // The first part is not interesting, we look for the
                        // second token, thus the next line
                        List<String> tokens = new ArrayList<String>(8);
                        while (stringTokenizer.hasMoreTokens()) {
                            tokens.add(stringTokenizer.nextToken());
                        }
                        String duration = tokens.get(1);
                        duration = duration.replace("[", "").replace("[", "").replace(".", ":");
                        durations.add(tokens.get(1));
                        titles.add("");
                        artists.add("");
                        composers.add("");
                    }
                }

                cd.setTracks(tracks);
                cd.setDurations(durations);
                cd.setDuration(totalDuration);
                cd.setId(id);
                //if (album != null && !album.equals(""))
                //	cd.setAlbum(album);
                //
                //if (artist != null && !artist.equals(""))
                //	cd.setArtist(artist);
                cd.setTitles(titles);
                cd.setArtists(artists);
                cd.setComposers(composers);

            } catch (Exception e) {
                logger.error(LogCategories.CDDA2WAV, e);
            } finally {
                ClosingUtils.close(stdInput);
            }

            logger.info(LogCategories.CDPARANOIA, StringUtils.getString("CD info: ", cd));
            return cd;

        } catch (Exception e) {
            logger.error(LogCategories.CDPARANOIA, e);
            return null;
        }
    }

}
