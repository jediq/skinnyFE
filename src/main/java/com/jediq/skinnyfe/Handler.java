package com.jediq.skinnyfe;

import com.codahale.metrics.MetricRegistry;
import com.jediq.skinnyfe.config.Config;
import com.jediq.skinnyfe.resource.ResourceReader;
import com.jediq.skinnyfe.resource.ResourceWriter;

public class Handler {

    protected final HandlebarsCompiler handlebarsCompiler;
    protected final TemplateResolver templateResolver;
    protected final ResourceReader resourceReader;
    protected final ResourceWriter resourceWriter;
    protected final Config config;

    public Handler(Config config, MetricRegistry metrics) {
        this.config = config;
        handlebarsCompiler = new HandlebarsCompiler();
        templateResolver = new TemplateResolver(config);
        resourceReader = new ResourceReader(config, metrics);
        resourceWriter = new ResourceWriter(config, metrics);
    }

}
