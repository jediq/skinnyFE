package com.jediq.skinnyfe;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class ResourceResponse {

    int code;
    String reason;
    String content;
    Map<String, String> headers = new HashMap<>();
}
