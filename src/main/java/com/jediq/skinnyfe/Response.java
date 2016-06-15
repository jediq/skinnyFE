package com.jediq.skinnyfe;

import java.io.IOException;
import java.io.PrintWriter;

public interface Response {

    int getStatus();

    void setStatus(int status);

    String getContentType();

    void setContentType(String contentType);

    PrintWriter getWriter() throws IOException;
}
