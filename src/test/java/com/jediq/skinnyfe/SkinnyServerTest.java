package com.jediq.skinnyfe;

import com.jediq.skinnyfe.config.Config;
import junitx.util.PrivateAccessor;
import org.eclipse.jetty.server.Server;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 */
public class SkinnyServerTest {

    @Test(expected=WrappedException.class)
    public void testConstructor_serverThrowsARuntimeException() throws Exception {
        LocalSkinnyServer.localServer.set(null);
        new LocalSkinnyServer();
        fail(); // should never get here
    }

    @Test(expected=WrappedException.class)
    public void testConstructor_startThrowsARuntimeException() throws Exception {
        LocalSkinnyServer.localServer.set(new Server());
        SkinnyServer skinnyServer = new LocalSkinnyServer();
        PrivateAccessor.setField(skinnyServer, "server", null);
        skinnyServer.start();
        fail(); // should never get here
    }

    @Test(expected=WrappedException.class)
    public void testConstructor_stopThrowsARuntimeException() throws Exception {
        LocalSkinnyServer.localServer.set(new Server());
        SkinnyServer skinnyServer = new LocalSkinnyServer();
        PrivateAccessor.setField(skinnyServer, "server", null);
        skinnyServer.stop();
        fail(); // should never get here
    }

    private static class LocalSkinnyServer extends SkinnyServer {

        // Need to use a ThreadLocal here as constructServer can't be modified prior to the super
        // constructor being called.  ThreadLocal allows our tests to run in parallel with
        // deterministic results.
        static ThreadLocal <Server> localServer = new ThreadLocal<>();

        public LocalSkinnyServer() {
            super(8800, new Config());
        }

        @Override
        protected Server constructServer(int port) {
            return localServer.get();
        }
    }

}