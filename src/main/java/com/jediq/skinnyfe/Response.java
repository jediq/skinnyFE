package com.jediq.skinnyfe;

import java.io.IOException;
import java.io.PrintWriter;

public interface Response {

    void setStatus(int status);

    void setContentType(String contentType);

    void sendRedirect(String location) throws IOException;

    PrintWriter getWriter() throws IOException;
}
