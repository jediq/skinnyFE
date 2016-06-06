package com.jediq.skinnyfe;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class SkinnyTemplate {

    private String contentType = "text/html";
    private String content;
    private List<Meta> metaList = new ArrayList<>();

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
}
