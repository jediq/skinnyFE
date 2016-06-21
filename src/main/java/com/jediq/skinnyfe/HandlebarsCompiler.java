package com.jediq.skinnyfe;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;

import java.io.IOException;
import java.util.Map;

public class HandlebarsCompiler {

    private Handlebars handlebars = new Handlebars();

    public String compile(String templateString, Map<String, Object> values, int numberOfIterations) {
        String templateToChange = templateString;
        for (int i=0; i<numberOfIterations; i++) {
            templateToChange = compile(templateToChange, values);
        }
        return templateToChange;
    }

    public String compile(String templateString, Context context, int numberOfIterations) {
        String templateToChange = templateString;
        for (int i=0; i<numberOfIterations; i++) {
            templateToChange = compile(templateToChange, context);
        }
        return templateToChange;
    }

    public String compile(String templateString, Map<String, Object> values) {
        try {
            Template template = handlebars.compileInline(templateString);
            return template.apply(values);
        } catch (IOException e) {
            throw new WrappedException(templateString, e);
        }
    }

    public String compile(String templateString, Context context) {
        try {
            Template template = handlebars.compileInline(templateString);
            return template.apply(context);
        } catch (IOException e) {
            throw new WrappedException(templateString, e);
        }
    }
}
