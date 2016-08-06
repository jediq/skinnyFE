package com.jediq.skinnyfe.config;

import com.jediq.skinnyfe.WrappedException;
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

    private String file;
    private String contentType = "text/html";
    private String content;
    private List<Meta> metaList = new ArrayList<>();
    private List<String> enrichers = new ArrayList<>();
    private Pattern pattern;

    public void setRegex(String regex) {
        this.pattern = Pattern.compile(regex);
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public List<String> getEnrichers() {
        return enrichers;
    }

    public String getContentType() {
        return contentType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Meta> getMetaList() {
        return metaList;
    }

    public boolean matches(String url) {
        return pattern.matcher(url).matches();
    }

    public void loadContent() {
        if (file == null) {
            throw new IllegalStateException("File cannot be null");
        }
        if (this.content == null) {
            try {
                this.content = new String(Files.readAllBytes(Paths.get(getFile())));
            } catch (IOException e) {
                throw new WrappedException("Couldn't read content from file " + getFile(), e);
            }
        }
    }
}
