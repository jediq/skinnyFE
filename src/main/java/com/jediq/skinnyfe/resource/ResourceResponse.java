package com.jediq.skinnyfe.resource;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class ResourceResponse {

    public int code;
    public String reason;
    public String content;
    public String url;
    Map<String, String> headers = new HashMap<>();
}
