package edu.stanford.owl2lpg.exporter.csv;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;


public class CsvWriter_IT {

    private CsvWriter<Ex> writer;

    private StringWriter sw;

    @Before
    public void setUp() throws Exception {
        sw = new StringWriter();
        writer = new CsvWriter<>(new CsvMapper(), Ex.class, sw);
    }

    @Test
    public void shouldWriteHeader() throws IOException {
        writer.write(new Ex("S1", "S2"));
        var written = sw.toString().trim();
        assertThat(written, startsWith("a,b"));
    }

    @Test
    public void shouldWriteFirstRow() throws IOException {
        writer.write(new Ex("S1", "S2"));
        var written = sw.toString().trim();
        assertThat(written, startsWith("a,b\nS1,S2"));
    }

    @Test
    public void shouldWriteSecondRow() throws IOException {
        writer.write(new Ex("S1", "S2"));
        writer.write(new Ex("S3", "S4"));
        var written = sw.toString().trim();
        assertThat(written, startsWith("a,b\nS1,S2\nS3,S4"));
    }

    @JsonPropertyOrder({"a", "b"})
    private static class Ex {

        private final String a;

        private final String b;

        public Ex(String a, String b) {
            this.a = a;
            this.b = b;
        }

        public String getA() {
            return a;
        }

        public String getB() {
            return b;
        }
    }
}