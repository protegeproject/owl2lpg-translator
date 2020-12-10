package edu.stanford.owl2lpg.exporter.graphml.wip;

import com.fasterxml.jackson.dataformat.csv.CsvSchema;

public class GraphmlSchema extends CsvSchema {
    protected GraphmlSchema(CsvSchema base, int features) {
        super(base, features);
    }

    public static GraphmlSchema.Builder builder() {
        return new GraphmlSchema.Builder();
    }

    public static class Builder extends CsvSchema.Builder {
        @Override
        public GraphmlSchema.Builder addColumn(String c) { return (Builder) this.addColumn(c); }

        @Override
        public GraphmlSchema.Builder setUseHeader(boolean b) { return (Builder) this.setUseHeader(b); }

        @Override
        public GraphmlSchema build() { return (GraphmlSchema) this.build(); }
    }

}
