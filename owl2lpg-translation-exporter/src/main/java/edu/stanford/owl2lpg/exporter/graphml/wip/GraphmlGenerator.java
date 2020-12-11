package edu.stanford.owl2lpg.exporter.graphml.wip;

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.dataformat.csv.CsvGenerator;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import java.io.Writer;

public class GraphmlGenerator extends CsvGenerator {
    public GraphmlGenerator(IOContext ctxt, int jsonFeatures, int csvFeatures, ObjectCodec codec, Writer out, CsvSchema schema) {
        super(ctxt, jsonFeatures, csvFeatures, codec, out, schema);
    }
}
