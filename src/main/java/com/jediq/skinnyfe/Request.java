package com.jediq.skinnyfe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class Request {

    public String url;
    public List<String> path = new ArrayList<>();
    public Map<String, String> cookies = new HashMap<>();
    public Map<String, String> headers = new HashMap<>();
    public Map<String, String> params = new HashMap<>();


}
