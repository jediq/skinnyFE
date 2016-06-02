package com.jediq.skinnyfe;

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
}
