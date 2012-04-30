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

package net.sourceforge.atunes.kernel.modules.webservices.lastfm;

import java.util.List;

import junit.framework.Assert;
import net.sourceforge.atunes.kernel.modules.network.NetworkHandler;
import net.sourceforge.atunes.model.ILovedTrack;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IStateContext;
import net.sourceforge.atunes.model.IStateCore;

import org.junit.Test;
import org.mockito.Mockito;

public class LastFmServiceTest {

    @Test
    public void testLovedTracks() {
    	IStateContext state = Mockito.mock(IStateContext.class);
    	Mockito.when(state.getLastFmUser()).thenReturn("alexaranda");
    	
    	NetworkHandler networkHandler = new NetworkHandler();
    	networkHandler.setStateCore(Mockito.mock(IStateCore.class));
    	
    	LastFmService service = new LastFmService();
    	service.setStateContext(state);
    	service.setOsManager(Mockito.mock(IOSManager.class));
    	service.setNetworkHandler(networkHandler);
    	
        List<ILovedTrack> lovedTracks = service.getLovedTracks();
        
        Assert.assertFalse(lovedTracks.isEmpty());
    }
}
