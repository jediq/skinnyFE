package com.jediq.skinnyfe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This is implemented as POST/REDIRECT/GET.
 *
 * Any request sent to a page with a POST method will hit this, we pull out the form
 * data and send that onto the Resources they reference before doing a redirect.
 *
 * @see https://en.wikipedia.org/wiki/Post/Redirect/Get
 *
 *
 */
public class PostHandler extends Handler {

    private final transient Logger logger = LoggerFactory.getLogger(this.getClass());

    public PostHandler(Config config) {
        super(config);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) {

        try {

            SkinnyTemplate skinnyTemplate;
            try {
                String url = request.getRequestURL().toString();
                skinnyTemplate = templateResolver.resolveTemplate(url);
            } catch (IllegalStateException e) {
                // we could not find the template
                logger.debug("Could not find template for : " + request.getRequestURL(), e);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }



            //resourceInteractor.saveResources();



            logger.info("POSTed, about to redirect to GET");
            response.sendRedirect(request.getRequestURL().toString());
        } catch (IOException | NullPointerException e) {
            logger.info("Exception trying to redirect to : " + request.getRequestURL(), e);
            response.setStatus(500);
        }
    }
}
