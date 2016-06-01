package com.jediq.skinnyfe;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SkinnyServlet extends HttpServlet {

        private Handlebars handlebars = new Handlebars();
        private TemplateResolver templateResolver;

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {

            response.setContentType("text/html");

            String url = request.getRequestURL().toString();
            SkinnyTemplate skinnyTemplate = templateResolver.resolveTemplate(url);

            Template template = handlebars.compileInline(skinnyTemplate.content);
            String rendered = template.apply("my text");

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println(rendered);
        }

        public void setTemplateResolver(TemplateResolver templateResolver) {
            this.templateResolver = templateResolver;
        }
    }