package com.jediq.skinnyfe;

import java.io.IOException;
import junitx.util.PrivateAccessor;
    import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 */
public class SkinnyServerTest {

    @Ignore("Strange issue when trying to mock a Jetty server, it throws an NPE as soon as it hits doThrow()")
    @Test
    public void testStart_serverThrowsAnException() throws Exception {

        Server server = mock(Server.class);
        SkinnyServer skinnyServer = new SkinnyServer(8800, null, null);
        doThrow(IOException.class).when(server).start();


    }

}