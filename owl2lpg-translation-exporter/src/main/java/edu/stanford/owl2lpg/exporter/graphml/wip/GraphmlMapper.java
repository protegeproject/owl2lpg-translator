package edu.stanford.owl2lpg.exporter.graphml.wip;


import com.fasterxml.jackson.core.FormatSchema;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.cfg.MapperBuilder;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.fasterxml.jackson.databind.util.ViewMatcher;
import com.fasterxml.jackson.dataformat.csv.impl.LRUMap;
import edu.stanford.owl2lpg.exporter.graphml.writer.GraphmlWriter;

import java.util.Collection;
import java.util.Iterator;

/**
 * Specialized ObjectMapper with extended functionality to produce GraphmlSchema instances from POJOs.
 * 
 * @see com.fasterxml.jackson.dataformat.csv.CsvMapper
 */
public class GraphmlMapper extends ObjectMapper {
    private static final long serialVersionUID = 1L;
    protected final LRUMap<JavaType, GraphmlSchema> _untypedSchemas;
    protected final LRUMap<JavaType, GraphmlSchema> _typedSchemas;

    public GraphmlMapper() {
        this(new GraphmlFactory());
    }

    public GraphmlMapper(GraphmlFactory f) {
        super(f);
        this.enable((MapperFeature[]) (new MapperFeature[]{MapperFeature.SORT_PROPERTIES_ALPHABETICALLY}));
        this._untypedSchemas = new LRUMap(8, 32);
        this._typedSchemas = new LRUMap(8, 32);
    }

    protected GraphmlMapper(GraphmlMapper src) {
        super(src);
        this._untypedSchemas = new LRUMap(8, 32);
        this._typedSchemas = new LRUMap(8, 32);
    }

    public GraphmlWriter writer(FormatSchema schema) {
        this._verifySchemaType(schema);
        return null; // this._newWriter(this.getSerializationConfig(), schema);
    }

    public static GraphmlMapper.Builder graphmlBuilder() {
        return new GraphmlMapper.Builder(new GraphmlMapper());
    }

    public static GraphmlMapper.Builder builder() {
        return new GraphmlMapper.Builder(new GraphmlMapper());
    }

    public static GraphmlMapper.Builder builder(GraphmlFactory streamFactory) {
        return new GraphmlMapper.Builder(new GraphmlMapper(streamFactory));
    }

    public GraphmlMapper copy() {
        this._checkInvalidCopy(GraphmlMapper.class);
        return new GraphmlMapper(this);
    }

    public GraphmlMapper configure(GraphmlGenerator.Feature f, boolean state) {
        return state ? this.enable(f) : this.disable(f);
    }

    public GraphmlMapper configure(GraphmlParser.Feature f, boolean state) {
        return state ? this.enable(f) : this.disable(f);
    }

    public GraphmlMapper enable(GraphmlGenerator.Feature f) {
        ((GraphmlFactory) this._jsonFactory).enable(f);
        return this;
    }

    public GraphmlMapper enable(GraphmlParser.Feature f) {
        ((GraphmlFactory) this._jsonFactory).enable(f);
        return this;
    }

    public GraphmlMapper disable(GraphmlGenerator.Feature f) {
        ((GraphmlFactory) this._jsonFactory).disable(f);
        return this;
    }

    public GraphmlMapper disable(GraphmlParser.Feature f) {
        ((GraphmlFactory) this._jsonFactory).disable(f);
        return this;
    }

    public GraphmlFactory getFactory() {
        return (GraphmlFactory) this._jsonFactory;
    }

    public ObjectReader readerWithSchemaFor(Class<?> pojoType) {
        JavaType type = this.constructType(pojoType);
        if (!type.isArrayType() && !type.isCollectionLikeType()) {
            return this.readerFor(type).with(this.schemaFor(type));
        } else {
            throw new IllegalArgumentException("Type can NOT be a Collection or array type");
        }
    }

    public ObjectReader readerWithTypedSchemaFor(Class<?> pojoType) {
        JavaType type = this.constructType(pojoType);
        if (!type.isArrayType() && !type.isCollectionLikeType()) {
            return this.readerFor(type).with(this.typedSchemaFor(type));
        } else {
            throw new IllegalArgumentException("Type can NOT be a Collection or array type");
        }
    }

    public ObjectWriter writerWithSchemaFor(Class<?> pojoType) {
        JavaType type = this.constructType(pojoType);
        if (!type.isArrayType() && !type.isCollectionLikeType()) {
            return this.writerFor(type).with(this.schemaFor(type));
        } else {
            throw new IllegalArgumentException("Type can NOT be a Collection or array type");
        }
    }

    public ObjectWriter writerWithTypedSchemaFor(Class<?> pojoType) {
        JavaType type = this.constructType(pojoType);
        if (!type.isArrayType() && !type.isCollectionLikeType()) {
            return this.writerFor(type).with(this.typedSchemaFor(type));
        } else {
            throw new IllegalArgumentException("Type can NOT be a Collection or array type");
        }
    }

    public GraphmlSchema schema() {
        return GraphmlSchema.emptySchema();
    }

    public GraphmlSchema schemaFor(JavaType pojoType) {
        return this._schemaFor(pojoType, this._untypedSchemas, false, (Class) null);
    }

    public GraphmlSchema schemaForWithView(JavaType pojoType, Class<?> view) {
        return this._schemaFor(pojoType, this._untypedSchemas, false, view);
    }

    public final GraphmlSchema schemaFor(Class<?> pojoType) {
        return this._schemaFor(this.constructType(pojoType), this._untypedSchemas, false, (Class) null);
    }

    public final GraphmlSchema schemaForWithView(Class<?> pojoType, Class<?> view) {
        return this._schemaFor(this.constructType(pojoType), this._untypedSchemas, false, view);
    }

    public final GraphmlSchema schemaFor(TypeReference<?> pojoTypeRef) {
        return this._schemaFor(this.constructType(pojoTypeRef.getType()), this._untypedSchemas, false, (Class) null);
    }

    public final GraphmlSchema schemaForWithView(TypeReference<?> pojoTypeRef, Class<?> view) {
        return this._schemaFor(this.constructType(pojoTypeRef.getType()), this._untypedSchemas, false, view);
    }

    public GraphmlSchema typedSchemaFor(JavaType pojoType) {
        return this._schemaFor(pojoType, this._typedSchemas, true, (Class) null);
    }

    public GraphmlSchema typedSchemaForWithView(JavaType pojoType, Class<?> view) {
        return this._schemaFor(pojoType, this._typedSchemas, true, view);
    }

    public final GraphmlSchema typedSchemaFor(Class<?> pojoType) {
        return this._schemaFor(this.constructType(pojoType), this._typedSchemas, true, (Class) null);
    }

    public final GraphmlSchema typedSchemaForWithView(Class<?> pojoType, Class<?> view) {
        return this._schemaFor(this.constructType(pojoType), this._typedSchemas, true, view);
    }

    public final GraphmlSchema typedSchemaFor(TypeReference<?> pojoTypeRef) {
        return this._schemaFor(this.constructType(pojoTypeRef.getType()), this._typedSchemas, true, (Class) null);
    }

    public final GraphmlSchema typedSchemaForWithView(TypeReference<?> pojoTypeRef, Class<?> view) {
        return this._schemaFor(this.constructType(pojoTypeRef.getType()), this._typedSchemas, true, view);
    }

    protected GraphmlSchema _schemaFor(JavaType pojoType, LRUMap<JavaType, GraphmlSchema> schemas, boolean typed, Class<?> view) {
        synchronized (schemas) {
            GraphmlSchema s = (GraphmlSchema) schemas.get(pojoType);
            if (s != null) {
                return s;
            }
        }

        AnnotationIntrospector intr = this._deserializationConfig.getAnnotationIntrospector();
        GraphmlSchema.Builder builder = GraphmlSchema.builder();
        this._addSchemaProperties(builder, intr, typed, pojoType, (NameTransformer) null, view);
        GraphmlSchema result = builder.build();
        synchronized (schemas) {
            schemas.put(pojoType, result);
            return result;
        }
    }

    protected boolean _nonPojoType(JavaType t) {
        if (!t.isPrimitive() && !t.isEnumType()) {
            Class<?> raw = t.getRawClass();
            if (Number.class.isAssignableFrom(raw) && (raw == Byte.class || raw == Short.class || raw == Character.class || raw == Integer.class || raw == Long.class || raw == Float.class || raw == Double.class)) {
                return true;
            } else {
                return raw == Boolean.class || raw == String.class;
            }
        } else {
            return true;
        }
    }

    protected void _addSchemaProperties(GraphmlSchema.Builder builder, AnnotationIntrospector intr, boolean typed, JavaType pojoType, NameTransformer unwrapper, Class<?> view) {
        if (!this._nonPojoType(pojoType)) {
            BeanDescription beanDesc = this.getSerializationConfig().introspect(pojoType);
            Iterator var8 = beanDesc.findProperties().iterator();

            while (true) {
                while (true) {
                    BeanPropertyDefinition prop;
                    Class[] views;
                    do {
                        do {
                            if (!var8.hasNext()) {
                                return;
                            }

                            prop = (BeanPropertyDefinition) var8.next();
                            if (view == null) {
                                break;
                            }

                            views = prop.findViews();
                            if (views == null) {
                                views = beanDesc.findDefaultViews();
                            }
                        } while (!ViewMatcher.construct(views).isVisibleForView(view));
                    } while (!prop.couldSerialize());

                    AnnotatedMember m = prop.getPrimaryMember();
                    if (m != null) {
                        NameTransformer nextUnwrapper = intr.findUnwrappingNameTransformer(prop.getPrimaryMember());
                        if (nextUnwrapper != null) {
                            if (unwrapper != null) {
                                nextUnwrapper = NameTransformer.chainedTransformer(unwrapper, nextUnwrapper);
                            }

                            JavaType nextType = m.getType();
                            this._addSchemaProperties(builder, intr, typed, nextType, nextUnwrapper, view);
                            continue;
                        }
                    }

                    String name = prop.getName();
                    if (unwrapper != null) {
                        name = unwrapper.transform(name);
                    }

                    if (typed && m != null) {
                        builder.addProperty(name, this._determineType(m.getRawType()));
                    } else {
                        builder.addProperty(name);
                    }
                }
            }
        }
    }

    protected GraphmlSchema.PropertyType _determineType(Class<?> propType) {
        if (propType.isArray()) {
            return propType == byte[].class ? GraphmlSchema.PropertyType.STRING : GraphmlSchema.PropertyType.ARRAY;
        } else if (propType != String.class && propType != Character.TYPE && propType != Character.class) {
            if (propType != Boolean.class && propType != Boolean.TYPE) {
                if (propType.isPrimitive()) {
                    return GraphmlSchema.PropertyType.NUMBER;
                } else if (Number.class.isAssignableFrom(propType)) {
                    return GraphmlSchema.PropertyType.NUMBER;
                } else {
                    return Collection.class.isAssignableFrom(propType) ? GraphmlSchema.PropertyType.ARRAY : GraphmlSchema.PropertyType.NUMBER_OR_STRING;
                }
            } else {
                return GraphmlSchema.PropertyType.BOOLEAN;
            }
        } else {
            return GraphmlSchema.PropertyType.STRING;
        }
    }

    public static class Builder extends MapperBuilder<GraphmlMapper, GraphmlMapper.Builder> {
        public Builder(GraphmlMapper m) {
            super(m);
        }

        public GraphmlMapper.Builder enable(GraphmlParser.Feature... features) {
            GraphmlParser.Feature[] var2 = features;
            int var3 = features.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                GraphmlParser.Feature f = var2[var4];
                ((GraphmlMapper) this._mapper).enable(f);
            }

            return this;
        }

        public GraphmlMapper.Builder disable(GraphmlParser.Feature... features) {
            GraphmlParser.Feature[] var2 = features;
            int var3 = features.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                GraphmlParser.Feature f = var2[var4];
                ((GraphmlMapper) this._mapper).disable(f);
            }

            return this;
        }

        public GraphmlMapper.Builder configure(GraphmlParser.Feature f, boolean state) {
            if (state) {
                ((GraphmlMapper) this._mapper).enable(f);
            } else {
                ((GraphmlMapper) this._mapper).disable(f);
            }

            return this;
        }

        public GraphmlMapper.Builder enable(GraphmlGenerator.Feature... features) {
            GraphmlGenerator.Feature[] var2 = features;
            int var3 = features.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                GraphmlGenerator.Feature f = var2[var4];
                ((GraphmlMapper) this._mapper).enable(f);
            }

            return this;
        }

        public GraphmlMapper.Builder disable(GraphmlGenerator.Feature... features) {
            GraphmlGenerator.Feature[] var2 = features;
            int var3 = features.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                GraphmlGenerator.Feature f = var2[var4];
                ((GraphmlMapper) this._mapper).disable(f);
            }

            return this;
        }

        public GraphmlMapper.Builder configure(GraphmlGenerator.Feature f, boolean state) {
            if (state) {
                ((GraphmlMapper) this._mapper).enable(f);
            } else {
                ((GraphmlMapper) this._mapper).disable(f);
            }

            return this;
        }
    }
}
