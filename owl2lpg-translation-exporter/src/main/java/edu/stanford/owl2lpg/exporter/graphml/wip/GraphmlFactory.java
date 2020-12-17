package edu.stanford.owl2lpg.exporter.graphml.wip;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;

public class GraphmlFactory extends JsonFactory {

    public GraphmlFactory enable(GraphmlParser.Feature f) {
        this._parserFeatures |= f.getMask();
        return this;
    }

    public GraphmlFactory disable(GraphmlParser.Feature f) {
        this._parserFeatures &= ~f.getMask();
        return this;
    }

    public GraphmlFactory enable(GraphmlGenerator.Feature f) {
        this._generatorFeatures |= f.getMask();
        return this;
    }

    public GraphmlFactory disable(GraphmlGenerator.Feature f) {
        this._generatorFeatures &= ~f.getMask();
        return this;
    }

}
