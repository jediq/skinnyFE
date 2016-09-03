package com.jediq.skinnyfe.resource;

import com.jediq.skinnyfe.config.Config;

public class InternalHostProtection {

    private final Config config;

    public InternalHostProtection(Config config) {
        this.config = config;
    }

    public boolean isProtected(String host) {
        if (config.getProtectedHostsRegex() != null) {
            return (host.matches(config.getProtectedHostsRegex()));
        }
        return false;
    }
}
