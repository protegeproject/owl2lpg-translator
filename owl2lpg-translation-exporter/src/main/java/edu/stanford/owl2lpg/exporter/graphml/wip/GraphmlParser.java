package edu.stanford.owl2lpg.exporter.graphml.wip;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.base.ParserMinimalBase;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

public class GraphmlParser extends ParserMinimalBase {

    @Override
    public JsonToken nextToken() throws IOException {
        return null;
    }

    @Override
    protected void _handleEOF() throws JsonParseException {

    }

    @Override
    public String getCurrentName() throws IOException {
        return null;
    }

    @Override
    public ObjectCodec getCodec() {
        return null;
    }

    @Override
    public void setCodec(ObjectCodec objectCodec) {

    }

    @Override
    public Version version() {
        return null;
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public JsonStreamContext getParsingContext() {
        return null;
    }

    @Override
    public JsonLocation getTokenLocation() {
        return null;
    }

    @Override
    public JsonLocation getCurrentLocation() {
        return null;
    }

    @Override
    public void overrideCurrentName(String s) {

    }

    @Override
    public String getText() throws IOException {
        return null;
    }

    @Override
    public char[] getTextCharacters() throws IOException {
        return new char[0];
    }

    @Override
    public boolean hasTextCharacters() {
        return false;
    }

    @Override
    public Number getNumberValue() throws IOException {
        return null;
    }

    @Override
    public NumberType getNumberType() throws IOException {
        return null;
    }

    @Override
    public int getIntValue() throws IOException {
        return 0;
    }

    @Override
    public long getLongValue() throws IOException {
        return 0;
    }

    @Override
    public BigInteger getBigIntegerValue() throws IOException {
        return null;
    }

    @Override
    public float getFloatValue() throws IOException {
        return 0;
    }

    @Override
    public double getDoubleValue() throws IOException {
        return 0;
    }

    @Override
    public BigDecimal getDecimalValue() throws IOException {
        return null;
    }

    @Override
    public int getTextLength() throws IOException {
        return 0;
    }

    @Override
    public int getTextOffset() throws IOException {
        return 0;
    }

    @Override
    public byte[] getBinaryValue(Base64Variant base64Variant) throws IOException {
        return new byte[0];
    }

    public static enum Feature implements FormatFeature {
        TRIM_SPACES(false),
        WRAP_AS_ARRAY(false),
        IGNORE_TRAILING_UNMAPPABLE(false),
        SKIP_EMPTY_LINES(false),
        ALLOW_TRAILING_COMMA(true),
        ALLOW_COMMENTS(false),
        FAIL_ON_MISSING_COLUMNS(false),
        INSERT_NULLS_FOR_MISSING_COLUMNS(false),
        EMPTY_STRING_AS_NULL(false);


        private final boolean _defaultState;
        private final int _mask;

        private Feature(boolean defaultState) {
            this._defaultState = defaultState;
            this._mask = 1 << this.ordinal();
        }

        @Override
        public boolean enabledByDefault() {
            return false;
        }

        @Override
        public int getMask() {
            return 0;
        }

        @Override
        public boolean enabledIn(int i) {
            return false;
        }
    }
}
