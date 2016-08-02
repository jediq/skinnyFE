package com.jediq.skinnyfe;

import com.codahale.metrics.MetricRegistry;
import com.jediq.skinnyfe.config.Config;

public class Handler {

    protected final HandlebarsCompiler handlebarsCompiler;
    protected final TemplateResolver templateResolver;
    protected final ResourceInteractor resourceInteractor;
    protected final Config config;

    public Handler(Config config, MetricRegistry metrics) {
        this.config = config;
        handlebarsCompiler = new HandlebarsCompiler();
        templateResolver = new TemplateResolver(config);
        resourceInteractor = new ResourceInteractor(config, metrics);
    }

}
