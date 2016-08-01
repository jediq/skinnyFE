package com.jediq.skinnyfe.enricher;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jediq.skinnyfe.WrappedException;
import com.jediq.skinnyfe.config.Config;
import java.util.Arrays;
import java.util.Optional;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 *
 */
public class DataEnricherTest {

    @Test
    public void testSimpleEnrich() throws Exception {
        DataEnricher dataEnricher = new DataEnricher(new Config());
        JsonNode jsonNode = new ObjectMapper().createObjectNode();
        ForceMethods forceMethods = new ForceMethods();
        JsonNode enriched = dataEnricher.enrich("src/test/resources/enricherTests/simpleEnricher.js", jsonNode, forceMethods);
        assertThat(enriched.has("banana"), is(true));
    }

    @Test
    public void testMultipleEnrichers() throws Exception {
        DataEnricher dataEnricher = new DataEnricher(new Config());
        JsonNode jsonNode = new ObjectMapper().createObjectNode();
        ForceMethods forceMethods = new ForceMethods();
        String enricherFile = "src/test/resources/enricherTests/multipleEnricher.js";
        String enricherSecondFile = "src/test/resources/enricherTests/simpleEnricher.js";
        JsonNode enriched = dataEnricher.enrich(Arrays.asList(enricherFile, enricherSecondFile), jsonNode, forceMethods);
        assertThat(enriched.has("banana"), is(true));
        assertThat(enriched.has("apple"), is(true));
    }

    @Test
    public void testMultipleEnrichersForcingStop() throws Exception {
        DataEnricher dataEnricher = new DataEnricher(new Config());
        JsonNode jsonNode = new ObjectMapper().createObjectNode();
        ForceMethods forceMethods = new ForceMethods();
        String enricherFile = "src/test/resources/enricherTests/multipleEnricherForcingStop.js";
        String enricherSecondFile = "src/test/resources/enricherTests/simpleEnricher.js";
        JsonNode enriched = dataEnricher.enrich(Arrays.asList(enricherFile, enricherSecondFile), jsonNode, forceMethods);
        assertThat(enriched.has("banana"), is(false));
        assertThat(enriched.has("apple"), is(true));
    }

    @Test
    public void testEnrichForcing404() throws Exception {
        DataEnricher dataEnricher = new DataEnricher(new Config());
        JsonNode jsonNode = new ObjectMapper().createObjectNode();
        ForceMethods forceMethods = new ForceMethods();
        dataEnricher.enrich("src/test/resources/enricherTests/enricherForcing404.js", jsonNode, forceMethods);
        assertThat(forceMethods.getResponseCode(), is(Optional.of(404)));
    }

    @Test(expected= WrappedException.class)
    public void testEnrich_NoEnrichFunction() throws Exception {
        DataEnricher dataEnricher = new DataEnricher(new Config());
        JsonNode jsonNode = new ObjectMapper().createObjectNode();
        ForceMethods forceMethods = new ForceMethods();
        dataEnricher.enrich("src/test/resources/enricherTests/enricherWithNoEnrichMethod.js", jsonNode, forceMethods);
    }

    @Test(expected=WrappedException.class)
    public void testEnrich_FolderNotFile() throws Exception {
        DataEnricher dataEnricher = new DataEnricher(new Config());
        JsonNode jsonNode = new ObjectMapper().createObjectNode();
        ForceMethods forceMethods = new ForceMethods();
        dataEnricher.enrich(".", jsonNode, forceMethods);
    }

    @Test(expected=WrappedException.class)
    public void testEnrich_enricherWithBadJS() throws Exception {
        DataEnricher dataEnricher = new DataEnricher(new Config());
        JsonNode jsonNode = new ObjectMapper().createObjectNode();
        ForceMethods forceMethods = new ForceMethods();
        dataEnricher.enrich("src/test/resources/enricherTests/enricherWithBadJS.js", jsonNode, forceMethods);
    }
}