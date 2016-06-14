package com.jediq.skinnyfe;

import com.github.jknack.handlebars.Handlebars;
import com.jediq.skinnyfe.config.Config;

public class Handler {

    protected final Handlebars handlebars;
    protected final TemplateResolver templateResolver;
    protected final ResourceInteractor resourceInteractor;
    protected final TemplatePopulater templatePopulater;

    public Handler(Config config) {
        handlebars = new Handlebars();
        templatePopulater = new TemplatePopulater();
        templateResolver = new TemplateResolver(config.getDefaultTemplates());
        resourceInteractor = new ResourceInteractor(config);
    }

}
