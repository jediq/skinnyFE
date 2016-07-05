package com.jediq.skinnyfe;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;

public class ServletWrappingResponse implements Response {

    private final HttpServletResponse servletResponse;

    public ServletWrappingResponse(HttpServletResponse servletResponse) {
        this.servletResponse = servletResponse;
    }

    @Override
    public void setStatus(int status) {
        servletResponse.setStatus(status);
    }

    @Override
    public void setContentType(String contentType) {
        servletResponse.setContentType(contentType);
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return servletResponse.getWriter();
    }

    @Override
    public void sendRedirect(String location) throws IOException {
        servletResponse.sendRedirect(location);
    }
}
