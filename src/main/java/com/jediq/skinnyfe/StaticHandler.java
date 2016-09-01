package com.jediq.skinnyfe;

import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

public class StaticHandler {


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private String path;
    private String folder;

    public boolean handle(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        if (path == null) {
            return false;
        }

        String requestPath = servletRequest.getPathInfo();
        logger.info("request for {}", requestPath);
        if (!requestPath.startsWith(path)) {
            return false;
        }

        String relativePath = requestPath.substring(path.length() + 1);
        String filePath = (folder + relativePath).replaceAll("//", "/");
        logger.info("Serving file from {}", filePath);

        File file = new File(filePath);
        if (file.exists()) {
            try {
                Files.copy(file, servletResponse.getOutputStream());
                servletResponse.setStatus(200);
            } catch (IOException e) {
                logger.info("Could not serve file from: " + file.getAbsolutePath(), e);
                servletResponse.setStatus(404);
            }
        } else {
            logger.info("Could not find file from: " + file.getAbsolutePath());
            servletResponse.setStatus(404);
        }
        return true;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }
}
