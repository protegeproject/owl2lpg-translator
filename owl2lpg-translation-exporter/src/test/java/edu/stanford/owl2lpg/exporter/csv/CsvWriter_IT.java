package edu.stanford.owl2lpg.exporter.csv;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import edu.stanford.owl2lpg.model.Properties;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;


public class CsvWriter_IT {

    private CsvWriter<Ex> writer;

    private StringWriter sw;


    public static final String THE_PROJECT_ID = "12345678-1234-1234-1234-123456789abc";

    @Before
    public void setUp() throws Exception {
        sw = new StringWriter();
        writer = new CsvWriter<>(new CsvMapper(), sw, new N4jNodeCsvSchema());
    }

    @Test
    public void shouldWriteHeader() throws IOException {
        writer.write(new Ex("S1", "S2"));
        var written = sw.toString().trim();
        assertThat(written, startsWith(":ID,:LABEL"));
    }

    @Test
    public void shouldWriteFirstRow() throws IOException {
        writer.write(new Ex("S1", "S2"));
        var written = sw.toString().trim();
        String [] lines = written.split("\n");
        assertThat(lines[1], startsWith("S1,S2"));
    }

    @Test
    public void shouldIncludeProperties() throws IOException {
        writer.write(new Ex("S1", "S2"));
        var written = sw.toString().trim();
        String [] lines = written.split("\n");
        assertThat(lines[1], startsWith("S1,S2,\"" + THE_PROJECT_ID + "\""));
    }

    @Test
    public void shouldWriteSecondRow() throws IOException {
        writer.write(new Ex("S1", "S2"));
        writer.write(new Ex("S3", "S4"));
        var written = sw.toString().trim();
        String [] lines = written.split("\n");
        assertThat(lines[2], startsWith("S3,S4"));
    }

    @JsonPropertyOrder({":ID", ":LABEL"})
    private static class Ex {

        private final String a;

        private final String b;

        private final Properties properties = Properties.of("projectId", THE_PROJECT_ID);

        public Ex(String a, String b) {
            this.a = a;
            this.b = b;
        }

        @JsonProperty(":ID")
        public String getA() {
            return a;
        }

        @JsonProperty(":LABEL")
        public String getB() {
            return b;
        }

        @JsonAnyGetter
        public Map<String, Object> getProperties() {
            return properties.getMap();
        }
    }
}