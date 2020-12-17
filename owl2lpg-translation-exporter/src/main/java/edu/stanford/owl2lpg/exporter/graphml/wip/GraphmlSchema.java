package edu.stanford.owl2lpg.exporter.graphml.wip;

import com.fasterxml.jackson.core.FormatSchema;

import java.io.Serializable;
import java.util.*;

/**
 *
 * @see com.fasterxml.jackson.dataformat.csv.CsvSchema
 */
public class GraphmlSchema implements FormatSchema, Iterable<GraphmlSchema.Property>, Serializable {
    private static final long serialVersionUID = 1L;
    protected static final int ENCODING_FEATURE_USE_HEADER = 1;
    protected static final int ENCODING_FEATURE_SKIP_FIRST_DATA_ROW = 2;
    protected static final int ENCODING_FEATURE_ALLOW_COMMENTS = 4;
    protected static final int ENCODING_FEATURE_REORDER_COLUMNS = 8;
    protected static final int ENCODING_FEATURE_STRICT_HEADERS = 16;
    protected static final int DEFAULT_ENCODING_FEATURES = 0;
    protected static final char[] NO_CHARS = new char[0];
    public static final char DEFAULT_COLUMN_SEPARATOR = ',';
    public static final String DEFAULT_ARRAY_ELEMENT_SEPARATOR = ";";
    public static final String NO_ARRAY_ELEMENT_SEPARATOR = "";
    public static final String DEFAULT_ANY_PROPERTY_NAME = null;
    public static final char DEFAULT_QUOTE_CHAR = '"';
    public static final char[] DEFAULT_NULL_VALUE = null;
    public static final int DEFAULT_ESCAPE_CHAR = -1;
    public static final char[] DEFAULT_LINEFEED = "\n".toCharArray();
    protected static final Property[] NO_PROPERTIES = new Property[0];
    protected final Property[] _properties;
    protected final Map<String, Property> _columnsByName;
    protected int _features;
    protected final char _columnSeparator;
    protected final String _arrayElementSeparator;
    protected final int _quoteChar;
    protected final int _escapeChar;
    protected final char[] _lineSeparator;
    protected final char[] _nullValue;
    protected transient String _nullValueAsString;
    protected final String _anyPropertyName;


    public GraphmlSchema(Property[] properties, int features, char columnSeparator, int quoteChar, int escapeChar, char[] lineSeparator, String arrayElementSeparator, char[] nullValue, String anyPropertyName) {
        this._features = 0;
        if (properties == null) {
            properties = NO_PROPERTIES;
        } else {
            properties = _link(properties);
        }

        this._properties = properties;
        this._features = features;
        this._columnSeparator = columnSeparator;
        this._arrayElementSeparator = arrayElementSeparator;
        this._quoteChar = quoteChar;
        this._escapeChar = escapeChar;
        this._lineSeparator = lineSeparator;
        this._nullValue = nullValue;
        this._anyPropertyName = anyPropertyName;
        if (this._properties.length == 0) {
            this._columnsByName = Collections.emptyMap();
        } else {
            this._columnsByName = new HashMap(4 + this._properties.length);
            Property[] var10 = this._properties;
            int var11 = var10.length;

            for(int var12 = 0; var12 < var11; ++var12) {
                Property c = var10[var12];
                this._columnsByName.put(c.getName(), c);
            }
        }

    }

    protected GraphmlSchema(Property[] properties, int features, char columnSeparator, int quoteChar, int escapeChar, char[] lineSeparator, String arrayElementSeparator, char[] nullValue, Map<String, Property> columnsByName, String anyPropertyName) {
        this._features = 0;
        this._properties = properties;
        this._features = features;
        this._columnSeparator = columnSeparator;
        this._quoteChar = quoteChar;
        this._escapeChar = escapeChar;
        this._lineSeparator = lineSeparator;
        this._arrayElementSeparator = arrayElementSeparator;
        this._nullValue = nullValue;
        this._columnsByName = columnsByName;
        this._anyPropertyName = anyPropertyName;
    }

    protected GraphmlSchema(GraphmlSchema base, Property[] properties) {
        this._features = 0;
        this._properties = _link(properties);
        this._features = base._features;
        this._columnSeparator = base._columnSeparator;
        this._quoteChar = base._quoteChar;
        this._escapeChar = base._escapeChar;
        this._lineSeparator = base._lineSeparator;
        this._arrayElementSeparator = base._arrayElementSeparator;
        this._nullValue = base._nullValue;
        this._anyPropertyName = base._anyPropertyName;
        if (this._properties.length == 0) {
            this._columnsByName = Collections.emptyMap();
        } else {
            this._columnsByName = new HashMap(4 + this._properties.length);
            Property[] var3 = this._properties;
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                Property c = var3[var5];
                this._columnsByName.put(c.getName(), c);
            }
        }

    }

    protected GraphmlSchema(GraphmlSchema base, int features) {
        this._features = 0;
        this._properties = base._properties;
        this._features = features;
        this._columnSeparator = base._columnSeparator;
        this._quoteChar = base._quoteChar;
        this._escapeChar = base._escapeChar;
        this._lineSeparator = base._lineSeparator;
        this._arrayElementSeparator = base._arrayElementSeparator;
        this._nullValue = base._nullValue;
        this._anyPropertyName = base._anyPropertyName;
        this._columnsByName = base._columnsByName;
    }

    private static Property[] _link(Property[] orig) {
        int i = orig.length;
        Property[] result = new Property[i];
        Property prev = null;

        while(true) {
            --i;
            if (i < 0) {
                return result;
            }

            Property curr = orig[i].withNext(i, prev);
            result[i] = curr;
            prev = curr;
        }
    }

    public static GraphmlSchema.Builder builder() {
        return new GraphmlSchema.Builder();
    }

    public static GraphmlSchema emptySchema() {
        return builder().build();
    }

    public GraphmlSchema.Builder rebuild() {
        return new GraphmlSchema.Builder(this);
    }

    public GraphmlSchema withUseHeader(boolean state) {
        return this._withFeature(1, state);
    }

    public GraphmlSchema withColumnReordering(boolean state) {
        return this._withFeature(8, state);
    }

    public GraphmlSchema withStrictHeaders(boolean state) {
        return this._withFeature(16, state);
    }

    public GraphmlSchema withHeader() {
        return this._withFeature(1, true);
    }

    public GraphmlSchema withoutHeader() {
        return this._withFeature(1, false);
    }

    public GraphmlSchema withSkipFirstDataRow(boolean state) {
        return this._withFeature(2, state);
    }

    public GraphmlSchema withAllowComments(boolean state) {
        return this._withFeature(4, state);
    }

    public GraphmlSchema withComments() {
        return this._withFeature(4, true);
    }

    public GraphmlSchema withoutComments() {
        return this._withFeature(4, false);
    }

    protected GraphmlSchema _withFeature(int feature, boolean state) {
        int newFeatures = state ? this._features | feature : this._features & ~feature;
        return newFeatures == this._features ? this : new GraphmlSchema(this, newFeatures);
    }

    public GraphmlSchema withColumnSeparator(char sep) {
        return this._columnSeparator == sep ? this : new GraphmlSchema(this._properties, this._features, sep, this._quoteChar, this._escapeChar, this._lineSeparator, this._arrayElementSeparator, this._nullValue, this._columnsByName, this._anyPropertyName);
    }

    public GraphmlSchema withQuoteChar(char c) {
        return this._quoteChar == c ? this : new GraphmlSchema(this._properties, this._features, this._columnSeparator, c, this._escapeChar, this._lineSeparator, this._arrayElementSeparator, this._nullValue, this._columnsByName, this._anyPropertyName);
    }

    public GraphmlSchema withoutQuoteChar() {
        return this._quoteChar == -1 ? this : new GraphmlSchema(this._properties, this._features, this._columnSeparator, -1, this._escapeChar, this._lineSeparator, this._arrayElementSeparator, this._nullValue, this._columnsByName, this._anyPropertyName);
    }

    public GraphmlSchema withEscapeChar(char c) {
        return this._escapeChar == c ? this : new GraphmlSchema(this._properties, this._features, this._columnSeparator, this._quoteChar, c, this._lineSeparator, this._arrayElementSeparator, this._nullValue, this._columnsByName, this._anyPropertyName);
    }

    public GraphmlSchema withoutEscapeChar() {
        return this._escapeChar == -1 ? this : new GraphmlSchema(this._properties, this._features, this._columnSeparator, this._quoteChar, -1, this._lineSeparator, this._arrayElementSeparator, this._nullValue, this._columnsByName, this._anyPropertyName);
    }

    /** @deprecated */
    @Deprecated
    public GraphmlSchema withArrayElementSeparator(char c) {
        return this.withArrayElementSeparator(Character.toString(c));
    }

    public GraphmlSchema withArrayElementSeparator(String separator) {
        String sep = separator == null ? "" : separator;
        return this._arrayElementSeparator.equals(sep) ? this : new GraphmlSchema(this._properties, this._features, this._columnSeparator, this._quoteChar, this._escapeChar, this._lineSeparator, separator, this._nullValue, this._columnsByName, this._anyPropertyName);
    }

    public GraphmlSchema withoutArrayElementSeparator() {
        return this._arrayElementSeparator.isEmpty() ? this : new GraphmlSchema(this._properties, this._features, this._columnSeparator, this._quoteChar, this._escapeChar, this._lineSeparator, "", this._nullValue, this._columnsByName, this._anyPropertyName);
    }

    public GraphmlSchema withLineSeparator(String sep) {
        return new GraphmlSchema(this._properties, this._features, this._columnSeparator, this._quoteChar, this._escapeChar, sep.toCharArray(), this._arrayElementSeparator, this._nullValue, this._columnsByName, this._anyPropertyName);
    }

    public GraphmlSchema withNullValue(String nvl) {
        return new GraphmlSchema(this._properties, this._features, this._columnSeparator, this._quoteChar, this._escapeChar, this._lineSeparator, this._arrayElementSeparator, nvl == null ? null : nvl.toCharArray(), this._columnsByName, this._anyPropertyName);
    }

    public GraphmlSchema withoutColumns() {
        return new GraphmlSchema(NO_PROPERTIES, this._features, this._columnSeparator, this._quoteChar, this._escapeChar, this._lineSeparator, this._arrayElementSeparator, this._nullValue, this._columnsByName, this._anyPropertyName);
    }

    public GraphmlSchema withColumnsFrom(GraphmlSchema toAppend) {
        int addCount = toAppend.size();
        if (addCount == 0) {
            return this;
        } else {
            GraphmlSchema.Builder b = this.rebuild();

            for(int i = 0; i < addCount; ++i) {
                Property col = toAppend.column(i);
                if (this.column(col.getName()) == null) {
                    b.addProperty(col);
                }
            }

            return b.build();
        }
    }

    public GraphmlSchema withAnyPropertyName(String name) {
        return new GraphmlSchema(this._properties, this._features, this._columnSeparator, this._quoteChar, this._escapeChar, this._lineSeparator, this._arrayElementSeparator, this._nullValue, this._columnsByName, name);
    }

    public GraphmlSchema sortedBy(String... columnNames) {
        LinkedHashMap<String, Property> map = new LinkedHashMap();
        String[] var3 = columnNames;
        int var4 = columnNames.length;

        int var5;
        for(var5 = 0; var5 < var4; ++var5) {
            String colName = var3[var5];
            Property col = (Property)this._columnsByName.get(colName);
            if (col != null) {
                map.put(col.getName(), col);
            }
        }

        Property[] var8 = this._properties;
        var4 = var8.length;

        for(var5 = 0; var5 < var4; ++var5) {
            Property col = var8[var5];
            map.put(col.getName(), col);
        }

        return new GraphmlSchema(this, (Property[])map.values().toArray(new Property[map.size()]));
    }

    public GraphmlSchema sortedBy(Comparator<String> cmp) {
        TreeMap<String, Property> map = new TreeMap(cmp);
        Property[] var3 = this._properties;
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            Property col = var3[var5];
            map.put(col.getName(), col);
        }

        return new GraphmlSchema(this, (Property[])map.values().toArray(new Property[map.size()]));
    }

    public String getSchemaType() {
        return "CSV";
    }

    public boolean usesHeader() {
        return (this._features & 1) != 0;
    }

    public boolean reordersColumns() {
        return (this._features & 8) != 0;
    }

    public boolean skipsFirstDataRow() {
        return (this._features & 2) != 0;
    }

    public boolean allowsComments() {
        return (this._features & 4) != 0;
    }

    public boolean strictHeaders() {
        return (this._features & 16) != 0;
    }

    /** @deprecated */
    @Deprecated
    public boolean useHeader() {
        return (this._features & 1) != 0;
    }

    /** @deprecated */
    @Deprecated
    public boolean skipFirstDataRow() {
        return (this._features & 2) != 0;
    }

    public char getColumnSeparator() {
        return this._columnSeparator;
    }

    public String getArrayElementSeparator() {
        return this._arrayElementSeparator;
    }

    public int getQuoteChar() {
        return this._quoteChar;
    }

    public int getEscapeChar() {
        return this._escapeChar;
    }

    public char[] getLineSeparator() {
        return this._lineSeparator;
    }

    public char[] getNullValue() {
        return this._nullValue;
    }

    public char[] getNullValueOrEmpty() {
        return this._nullValue == null ? NO_CHARS : this._nullValue;
    }

    public String getNullValueString() {
        String str = this._nullValueAsString;
        if (str == null) {
            if (this._nullValue == null) {
                return null;
            }

            str = this._nullValue.length == 0 ? "" : new String(this._nullValue);
            this._nullValueAsString = str;
        }

        return str;
    }

    public boolean usesQuoteChar() {
        return this._quoteChar >= 0;
    }

    public boolean usesEscapeChar() {
        return this._escapeChar >= 0;
    }

    public boolean hasArrayElementSeparator() {
        return !this._arrayElementSeparator.isEmpty();
    }

    public String getAnyPropertyName() {
        return this._anyPropertyName;
    }

    public Iterator<Property> iterator() {
        return Arrays.asList(this._properties).iterator();
    }

    public int size() {
        return this._properties.length;
    }

    public Property column(int index) {
        return this._properties[index];
    }

    public String columnName(int index) {
        return this._properties[index].getName();
    }

    public Property column(String name) {
        return (Property)this._columnsByName.get(name);
    }

    public Property column(String name, int probableIndex) {
        if (probableIndex < this._properties.length) {
            Property col = this._properties[probableIndex];
            if (col.hasName(name)) {
                return col;
            }
        }

        return (Property)this._columnsByName.get(name);
    }

    public String getColumnDesc() {
        StringBuilder sb = new StringBuilder(100);
        Property[] var2 = this._properties;
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Property col = var2[var4];
            if (sb.length() == 0) {
                sb.append('[');
            } else {
                sb.append(',');
            }

            sb.append('"');
            sb.append(col.getName());
            sb.append('"');
        }

        sb.append(']');
        return sb.toString();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(150);
        sb.append("[GraphmlSchema: ").append("columns=[");
        boolean first = true;
        Property[] var3 = this._properties;
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            Property col = var3[var5];
            if (first) {
                first = false;
            } else {
                sb.append(',');
            }

            sb.append('"');
            sb.append(col.getName());
            sb.append("\"/");
            sb.append(col.getType());
        }

        sb.append(']');
        sb.append(", header? ").append(this.usesHeader());
        sb.append(", skipFirst? ").append(this.skipsFirstDataRow());
        sb.append(", comments? ").append(this.allowsComments());
        sb.append(", any-properties? ");
        String anyProp = this.getAnyPropertyName();
        if (anyProp == null) {
            sb.append("N/A");
        } else {
            sb.append("as '").append(anyProp).append("'");
        }

        sb.append(']');
        return sb.toString();
    }

    protected static String _validArrayElementSeparator(String sep) {
        return sep != null && !sep.isEmpty() ? sep : "";
    }

    public static class Builder {
        protected final ArrayList<Property> _properties = new ArrayList();
        protected int _encodingFeatures = 0;
        protected char _columnSeparator = ',';
        protected String _arrayElementSeparator = ";";
        protected String _anyPropertyName;
        protected int _quoteChar;
        protected int _escapeChar;
        protected char[] _lineSeparator;
        protected char[] _nullValue;

        public Builder() {
            this._anyPropertyName = GraphmlSchema.DEFAULT_ANY_PROPERTY_NAME;
            this._quoteChar = 34;
            this._escapeChar = -1;
            this._lineSeparator = GraphmlSchema.DEFAULT_LINEFEED;
            this._nullValue = GraphmlSchema.DEFAULT_NULL_VALUE;
        }

        public Builder(GraphmlSchema src) {
            this._anyPropertyName = GraphmlSchema.DEFAULT_ANY_PROPERTY_NAME;
            this._quoteChar = 34;
            this._escapeChar = -1;
            this._lineSeparator = GraphmlSchema.DEFAULT_LINEFEED;
            this._nullValue = GraphmlSchema.DEFAULT_NULL_VALUE;
            Property[] var2 = src._properties;
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                Property col = var2[var4];
                this._properties.add(col);
            }

            this._encodingFeatures = src._features;
            this._columnSeparator = src._columnSeparator;
            this._arrayElementSeparator = src._arrayElementSeparator;
            this._quoteChar = src._quoteChar;
            this._escapeChar = src._escapeChar;
            this._lineSeparator = src._lineSeparator;
            this._nullValue = src._nullValue;
            this._anyPropertyName = src._anyPropertyName;
        }

        public GraphmlSchema.Builder addProperty(String name) {
            int index = this._properties.size();
            return this.addProperty(new Property(index, name));
        }

        public GraphmlSchema.Builder addProperty(String name, PropertyType type) {
            int index = this._properties.size();
            return this.addProperty(new Property(index, name, type));
        }

        public GraphmlSchema.Builder addProperty(Property c) {
            this._properties.add(c);
            return this;
        }

        public GraphmlSchema.Builder addColumns(Iterable<Property> cs) {
            Iterator var2 = cs.iterator();

            while(var2.hasNext()) {
                Property c = (Property)var2.next();
                this._properties.add(c);
            }

            return this;
        }

        public GraphmlSchema.Builder addColumns(Iterable<String> names, PropertyType type) {
            GraphmlSchema.Builder result = this;

            String name;
            for(Iterator var4 = names.iterator(); var4.hasNext(); result = this.addProperty(name, type)) {
                name = (String)var4.next();
            }

            return result;
        }

        public GraphmlSchema.Builder addColumnsFrom(GraphmlSchema schema) {
            GraphmlSchema.Builder result = this;
            Iterator var3 = schema.iterator();

            while(var3.hasNext()) {
                Property col = (Property)var3.next();
                if (!this.hasColumn(col.getName())) {
                    result = result.addProperty(col);
                }
            }

            return result;
        }

        public GraphmlSchema.Builder addArrayColumn(String name) {
            int index = this._properties.size();
            return this.addProperty(new Property(index, name, PropertyType.ARRAY, ""));
        }

        /** @deprecated */
        @Deprecated
        public GraphmlSchema.Builder addArrayColumn(String name, int elementSeparator) {
            int index = this._properties.size();
            return this.addProperty(new Property(index, name, PropertyType.ARRAY, elementSeparator));
        }

        public GraphmlSchema.Builder addArrayColumn(String name, String elementSeparator) {
            int index = this._properties.size();
            return this.addProperty(new Property(index, name, PropertyType.ARRAY, elementSeparator));
        }

        public GraphmlSchema.Builder addNumberColumn(String name) {
            int index = this._properties.size();
            return this.addProperty(new Property(index, name, PropertyType.NUMBER));
        }

        public GraphmlSchema.Builder addBooleanColumn(String name) {
            int index = this._properties.size();
            return this.addProperty(new Property(index, name, PropertyType.BOOLEAN));
        }

        public GraphmlSchema.Builder replaceColumn(int index, Property c) {
            this._checkIndex(index);
            this._properties.set(index, c);
            return this;
        }

        public GraphmlSchema.Builder renameColumn(int index, String newName) {
            this._checkIndex(index);
            this._properties.set(index, ((Property)this._properties.get(index)).withName(newName));
            return this;
        }

        public GraphmlSchema.Builder setColumnType(int index, PropertyType type) {
            this._checkIndex(index);
            this._properties.set(index, ((Property)this._properties.get(index)).withType(type));
            return this;
        }

        public GraphmlSchema.Builder removeArrayElementSeparator(int index) {
            this._checkIndex(index);
            this._properties.set(index, ((Property)this._properties.get(index)).withArrayElementSeparator(""));
            return this;
        }

        /** @deprecated */
        @Deprecated
        public void setArrayElementSeparator(int index, char sep) {
            this._checkIndex(index);
            this._properties.set(index, ((Property)this._properties.get(index)).withElementSeparator(sep));
        }

        public GraphmlSchema.Builder setArrayElementSeparator(int index, String sep) {
            this._checkIndex(index);
            this._properties.set(index, ((Property)this._properties.get(index)).withArrayElementSeparator(sep));
            return this;
        }

        public GraphmlSchema.Builder setAnyPropertyName(String name) {
            this._anyPropertyName = name;
            return this;
        }

        public GraphmlSchema.Builder clearColumns() {
            this._properties.clear();
            return this;
        }

        public int size() {
            return this._properties.size();
        }

        public Iterator<Property> getColumns() {
            return this._properties.iterator();
        }

        public boolean hasColumn(String name) {
            int i = 0;

            for(int end = this._properties.size(); i < end; ++i) {
                if (((Property)this._properties.get(i)).getName().equals(name)) {
                    return true;
                }
            }

            return false;
        }

        public GraphmlSchema.Builder setUseHeader(boolean b) {
            this._feature(1, b);
            return this;
        }

        public GraphmlSchema.Builder setReorderColumns(boolean b) {
            this._feature(8, b);
            return this;
        }

        public GraphmlSchema.Builder setStrictHeaders(boolean b) {
            this._feature(16, b);
            return this;
        }

        public GraphmlSchema.Builder setSkipFirstDataRow(boolean b) {
            this._feature(2, b);
            return this;
        }

        public GraphmlSchema.Builder setAllowComments(boolean b) {
            this._feature(4, b);
            return this;
        }

        protected final void _feature(int feature, boolean state) {
            this._encodingFeatures = state ? this._encodingFeatures | feature : this._encodingFeatures & ~feature;
        }

        public GraphmlSchema.Builder setColumnSeparator(char c) {
            this._columnSeparator = c;
            return this;
        }

        /** @deprecated */
        @Deprecated
        public GraphmlSchema.Builder setArrayElementSeparator(char c) {
            this._arrayElementSeparator = Character.toString(c);
            return this;
        }

        public GraphmlSchema.Builder setArrayElementSeparator(String separator) {
            this._arrayElementSeparator = GraphmlSchema._validArrayElementSeparator(separator);
            return this;
        }

        /** @deprecated */
        @Deprecated
        public GraphmlSchema.Builder disableElementSeparator(char c) {
            return this.disableArrayElementSeparator();
        }

        public GraphmlSchema.Builder disableArrayElementSeparator() {
            this._arrayElementSeparator = "";
            return this;
        }

        public GraphmlSchema.Builder setQuoteChar(char c) {
            this._quoteChar = c;
            return this;
        }

        public GraphmlSchema.Builder disableQuoteChar() {
            this._quoteChar = -1;
            return this;
        }

        public GraphmlSchema.Builder setEscapeChar(char c) {
            this._escapeChar = c;
            return this;
        }

        public GraphmlSchema.Builder disableEscapeChar() {
            this._escapeChar = -1;
            return this;
        }

        public GraphmlSchema.Builder setLineSeparator(String lf) {
            this._lineSeparator = lf.toCharArray();
            return this;
        }

        public GraphmlSchema.Builder setLineSeparator(char lf) {
            this._lineSeparator = new char[]{lf};
            return this;
        }

        public GraphmlSchema.Builder setNullValue(String nvl) {
            return this.setNullValue(nvl == null ? null : nvl.toCharArray());
        }

        public GraphmlSchema.Builder setNullValue(char[] nvl) {
            this._nullValue = nvl;
            return this;
        }

        public GraphmlSchema build() {
            Property[] cols = (Property[])this._properties.toArray(new Property[this._properties.size()]);
            return new GraphmlSchema(cols, this._encodingFeatures, this._columnSeparator, this._quoteChar, this._escapeChar, this._lineSeparator, this._arrayElementSeparator, this._nullValue, this._anyPropertyName);
        }

        protected void _checkIndex(int index) {
            if (index < 0 || index >= this._properties.size()) {
                throw new IllegalArgumentException("Illegal index " + index + "; only got " + this._properties.size() + " columns");
            }
        }
    }

    public static class Property implements Serializable {
        private static final long serialVersionUID = 1L;
        public static final Property PLACEHOLDER = new Property(0, "");
        private final String _name;
        private final int _index;
        private final PropertyType _type;
        private final String _arrayElementSeparator;
        private final Property _next;

        public Property(int index, String name) {
            this(index, name, PropertyType.STRING, "");
        }

        public Property(int index, String name, PropertyType type) {
            this(index, name, type, "");
        }

        /** @deprecated */
        @Deprecated
        public Property(int index, String name, PropertyType type, int arrayElementSep) {
            this(index, name, type, arrayElementSep < 0 ? "" : Character.toString((char)arrayElementSep));
        }

        public Property(int index, String name, PropertyType type, String arrayElementSep) {
            this._index = index;
            this._name = name;
            this._type = type;
            this._arrayElementSeparator = GraphmlSchema._validArrayElementSeparator(arrayElementSep);
            this._next = null;
        }

        public Property(Property src, Property next) {
            this(src, src._index, next);
        }

        protected Property(Property src, int index, Property next) {
            this._index = index;
            this._name = src._name;
            this._type = src._type;
            this._arrayElementSeparator = src._arrayElementSeparator;
            this._next = next;
        }

        public Property withName(String newName) {
            return this._name == newName ? this : new Property(this._index, newName, this._type, this._arrayElementSeparator);
        }

        public Property withType(PropertyType newType) {
            return newType == this._type ? this : new Property(this._index, this._name, newType, this._arrayElementSeparator);
        }

        /** @deprecated */
        @Deprecated
        public Property withElementSeparator(int separator) {
            return this.withArrayElementSeparator(separator < 0 ? "" : Character.toString((char)separator));
        }

        public Property withArrayElementSeparator(String separator) {
            String sep = GraphmlSchema._validArrayElementSeparator(separator);
            return this._arrayElementSeparator.equals(sep) ? this : new Property(this._index, this._name, this._type, sep);
        }

        public Property withNext(Property next) {
            return this._next == next ? this : new Property(this, next);
        }

        public Property withNext(int index, Property next) {
            return this._index == index && this._next == next ? this : new Property(this, index, next);
        }

        public int getIndex() {
            return this._index;
        }

        public String getName() {
            return this._name;
        }

        public PropertyType getType() {
            return this._type;
        }

        public Property getNext() {
            return this._next;
        }

        public Property getNextWithName(String name) {
            return this._next != null && name.equals(this._next._name) ? this._next : null;
        }

        public boolean hasName(String n) {
            return this._name == n || this._name.equals(n);
        }

        public String getArrayElementSeparator() {
            return this._arrayElementSeparator;
        }

        public boolean isArray() {
            return this._type == PropertyType.ARRAY;
        }
    }

    public static enum PropertyType {
        STRING,
        STRING_OR_LITERAL,
        NUMBER,
        NUMBER_OR_STRING,
        BOOLEAN,
        ARRAY;

        private PropertyType() {
        }
    }
}
