package com.jediq.skinnyfe.config;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 *
 */
public class Meta {

    private String property;
    private String resource;
    private String identifier;

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public String toString() {
        return property + " : " + resource + " : " + identifier;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(property).append(resource).append(identifier).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return hashCode() == obj.hashCode();
    }
}
