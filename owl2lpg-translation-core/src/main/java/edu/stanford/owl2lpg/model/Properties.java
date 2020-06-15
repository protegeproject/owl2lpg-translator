package edu.stanford.owl2lpg.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/*
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public abstract class Properties {

  private static final Properties EMPTY = create(ImmutableMap.of());

  public static Properties empty() {
    return EMPTY;
  }

  public static Properties create(@Nonnull ImmutableMap<String, Object> map) {
    return new AutoValue_Properties(map);
  }

  @Nonnull
  public static Properties of(@Nonnull String property, @Nonnull Object value) {
    return create(ImmutableMap.of(property, value));
  }

  private static String escape(String value) {
    return value.replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\b", "\\b")
        .replace("\f", "\\f")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t");
  }

  @JsonValue
  public abstract ImmutableMap<String, Object> getMap();

  @Nullable
  public <E> E get(String key) {
    var obj = getMap().get(key);
    return (obj != null) ? (E) obj.getClass().cast(obj) : null;
  }

  public Map<String, Object> neoProperties() {
    return getMap().entrySet().stream()
        .collect(collectToTypedNeoMap());
  }

  @NotNull
  private static Collector<Map.Entry<String, Object>, ?, Map<String, Object>> collectToTypedNeoMap() {
    return Collectors.toMap(Properties::toTypedNeoKey, Map.Entry::getValue);
  }

  private static String toTypedNeoKey(Map.Entry<String, Object> e) {
    if (e.getValue() instanceof Integer) {
      return e.getKey() + ":int";
    }
    else if (e.getValue() instanceof Boolean) {
      return e.getKey() + ":boolean";
    }
    else {
      return e.getKey();
    }
  }

  public void forEach(@Nonnull BiConsumer<String, Object> consumer) {
    getMap().forEach(consumer);
  }

  @Nonnull
  public String printProperties() {
    var sb = new StringBuilder();
    sb.append("{");
    forEach((key, value) -> {
          if (sb.length() > 1) {
            sb.append(",");
          }
          if (value instanceof String) {
            sb.append(key)
                .append(": \"")
                .append(escape((String) value))
                .append("\"");
          } else {
            sb.append(key).append(": ").append(value);
          }
        }
    );
    sb.append("}");
    return sb.toString();
  }
}
