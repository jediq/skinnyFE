package com.jediq.skinnyfe;

import org.junit.AssumptionViolatedException;

import java.io.IOException;
import java.net.InetAddress;

/**
 *
 */
public class TestUtil {

    public static void assumePing(String host) {
        try {
            if (!InetAddress.getByName(host).isReachable(200)) {
                throw new AssumptionViolatedException("host ["  + host + "] is unreachable");
            }
        } catch (IOException e) {
            throw new AssumptionViolatedException("host ["  + host + "] throw exception : " + e.getMessage());
        }
    }
}
