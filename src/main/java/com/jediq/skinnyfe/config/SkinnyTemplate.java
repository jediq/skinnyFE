package com.jediq.skinnyfe.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 */
public class SkinnyTemplate {

    private String regex;
    private String file;
    private String enricher;
    private String contentType = "text/html";
    private String content;
    private List<Meta> metaList = new ArrayList<>();
    private Pattern pattern;

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
        this.pattern = Pattern.compile(regex);
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getEnricher() {
        return enricher;
    }

    public String getContentType() {
        return contentType;
    }

    public String getContent() {
        return content;
    }

    public List<Meta> getMetaList() {
        return metaList;
    }

    public boolean matches(String url) {
        return pattern.matcher(url).matches();
    }

    public void loadContent() throws IOException {
        if (this.content == null) {
            this.content = new String(Files.readAllBytes(Paths.get(getFile())));
        }
    }
}
