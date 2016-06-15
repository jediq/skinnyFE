package com.jediq.skinnyfe;


import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ServletWrappingResponse implements Response {

    private final HttpServletResponse servletResponse;

    public ServletWrappingResponse(HttpServletResponse servletResponse) {
        this.servletResponse = servletResponse;
    }

    @Override
    public int getStatus() {
        return servletResponse.getStatus();
    }

    @Override
    public void setStatus(int status) {
        servletResponse.setStatus(status);
    }

    @Override
    public String getContentType() {
        return servletResponse.getContentType();
    }

    @Override
    public void setContentType(String contentType) {
        servletResponse.setContentType(contentType);
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return servletResponse.getWriter();
    }
}
