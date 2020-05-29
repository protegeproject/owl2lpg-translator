package edu.stanford.owl2lpg.exporter.csv;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Writer;

public class NoOpWriter extends Writer {

    @Override
    public void write(@NotNull char[] cbuf, int off, int len) throws IOException {

    }

    @Override
    public void flush() throws IOException {

    }

    @Override
    public void close() throws IOException {

    }
}
