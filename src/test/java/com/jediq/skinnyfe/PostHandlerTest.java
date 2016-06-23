package com.jediq.skinnyfe;

import com.jediq.skinnyfe.config.Config;
import com.jediq.skinnyfe.config.Meta;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import junitx.util.PrivateAccessor;
import static org.junit.Assert.*;
import org.junit.Test;
import static org.mockito.Mockito.*;

/**
 *
 */
public class PostHandlerTest {

    @Test
    public void testDoPostNoTemplate() throws Exception {

        Config config = Config.load("src/test/resources/basic/config.json");
        PostHandler postHandler = new PostHandler(config);
        Request request = new Request();
        request.setUrl("http://localhost/notemplate");
        request.getParams().put("car.registration", "123456");

        HttpServletResponse response = mock(HttpServletResponse.class);
        postHandler.doPost(request, response);

        verify(response).setStatus(404);
    }

    @Test
    public void testDoPostGreenRoute() throws Exception {

        Config config = Config.load("src/test/resources/basic/config.json");
        PostHandler postHandler = new PostHandler(config);
        Request request = new Request();
        request.setUrl("http://localhost/posting");
        request.getParams().put("car.registration", "123456");
        request.getParams().put("car.door", "big red door");

        ResourceInteractor resourceInteractor = mock(ResourceInteractor.class);
        PrivateAccessor.setField(postHandler, "resourceInteractor", resourceInteractor);
        HttpServletResponse response = mock(HttpServletResponse.class);
        postHandler.doPost(request, response);


        Map<Meta, String> metaMap = new HashMap<>();
        Meta key = new Meta();
        key.setProperty("car");
        key.setResource("Vehicle");
        key.setIdentifier("23");
        metaMap.put(key, "{\"door\":\"big red door\",\"registration\":\"123456\"}");

        verify(resourceInteractor).saveResources(metaMap, request);
        verify(response).sendRedirect("http://localhost/posting");
    }

    @Test
    public void testDoPostExceptionFromServer() throws Exception {

        Config config = Config.load("src/test/resources/basic/config.json");
        PostHandler postHandler = new PostHandler(config);
        Request request = new Request();
        request.setUrl("http://localhost/posting");
        request.getParams().put("car.registration", "123456");
        request.getParams().put("car.door", "big red door");

        ResourceInteractor resourceInteractor = mock(ResourceInteractor.class);
        PrivateAccessor.setField(postHandler, "resourceInteractor", resourceInteractor);
        HttpServletResponse response = mock(HttpServletResponse.class);

        Map<Meta, String> metaMap = new HashMap<>();
        Meta key = new Meta();
        key.setProperty("car");
        key.setResource("Vehicle");
        key.setIdentifier("23");
        metaMap.put(key, "{\"door\":\"big red door\",\"registration\":\"123456\"}");

        doThrow(NullPointerException.class).when(resourceInteractor).saveResources(metaMap, request);
        postHandler.doPost(request, response);

        verify(response).setStatus(500);
    }



}