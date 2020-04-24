package edu.stanford.owl2lpg.exporter.csv;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.opencsv.bean.*;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.apache.commons.collections4.ListValuedMap;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

/*
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
/* package */ class CsvWritingStrategy<T>
    extends HeaderColumnNameTranslateMappingStrategy<T> {

  public CsvWritingStrategy(Class<? extends T> cls) {
    super.setType(cls);
    var map = getColumnNameMap(cls);
    setColumnMapping(map);
    var columnNamesInOrder = getColumnNameList(cls);
    setColumnOrderOnWrite(Comparator.comparingInt(columnNamesInOrder::indexOf));
  }

  /*
   * Code is inspired from from:
   * https://stackoverflow.com/questions/56168094/why-opencsv-capitalizing-csv-
   * headers-while-writing-to-file
   */
  private HashMap<String, String> getColumnNameMap(Class<? extends T> cls) {
    var map = Maps.<String, String>newHashMap();
    Arrays.stream(cls.getDeclaredFields())
        .forEach(field -> {
          var a1 = field.getAnnotation(CsvBindByName.class);
          if (a1 != null) {
            map.put(a1.column().toUpperCase(), a1.column());
          }
          var a2 = field.getAnnotation(CsvBindAndSplitByName.class);
          if (a2 != null) {
            map.put(a2.column().toUpperCase(), a2.column());
          }
        });
    return map;
  }

  /**
   * Code is inspired from from:
   * https://stackoverflow.com/questions/45203867/opencsv-how-to-create-csv-
   * file-from-pojo-with-custom-column-headers-and-custom
   */
  private List<String> getColumnNameList(Class<? extends T> cls) {
    var list = Lists.<String>newArrayList();
    Arrays.stream(cls.getDeclaredFields())
        .forEach(field -> {
          var a1 = field.getAnnotation(CsvBindByName.class);
          if (a1 != null) {
            list.add(a1.column().toUpperCase());
          }
          var a2 = field.getAnnotation(CsvBindAndSplitByName.class);
          if (a2 != null) {
            list.add(a2.column().toUpperCase());
          }
        });
    return list;
  }

  @Override
  public String[] generateHeader(T bean) throws CsvRequiredFieldEmptyException {
    var result = super.generateHeader(bean);
    for (int i = 0; i < result.length; i++) {
      result[i] = getColumnName(i);
    }
    return result;
  }

  @Override
  protected void loadAnnotatedFieldMap(ListValuedMap<Class<?>, Field> fields) {
    boolean required;
    for (Map.Entry<Class<?>, Field> classField : fields.entries()) {
      var localType = classField.getKey();
      var localField = classField.getValue();
      String columnName, locale, writeLocale, capture, format;

      // Always check for a custom converter first.
      if (localField.isAnnotationPresent(CsvCustomBindByName.class)) {
        var annotation = localField
            .getAnnotation(CsvCustomBindByName.class);
        columnName = annotation.column().toUpperCase().trim();
        if (StringUtils.isEmpty(columnName)) {
          columnName = localField.getName().toUpperCase();
        }
        @SuppressWarnings("unchecked")
        var converter =
            (Class<? extends AbstractBeanField<T, String>>) localField
                .getAnnotation(CsvCustomBindByName.class)
                .converter();
        var bean = instantiateCustomConverter(converter);
        bean.setType(localType);
        bean.setField(localField);
        required = annotation.required();
        bean.setRequired(required);
        fieldMap.put(columnName, bean);
      }

      // Then check for a collection
      else if (localField.isAnnotationPresent(CsvBindAndSplitByName.class)) {
        var annotation = localField.getAnnotation(CsvBindAndSplitByName.class);
        required = annotation.required();
        columnName = annotation.column().toUpperCase().trim();
        locale = annotation.locale();
        writeLocale = annotation.writeLocaleEqualsReadLocale()
            ? locale : annotation.writeLocale();
        var splitOn = annotation.splitOn();
        var writeDelimiter = annotation.writeDelimiter();
        var collectionType = annotation.collectionType();
        var elementType = annotation.elementType();
        var splitConverter = annotation.converter();
        capture = annotation.capture();
        format = annotation.format();

        var converter = determineConverter(
            localField, elementType, locale,
            writeLocale, splitConverter);
        if (StringUtils.isEmpty(columnName)) {
          fieldMap.put(localField.getName().toUpperCase(),
              new BeanFieldSplit<>(
                  localType,
                  localField, required,
                  errorLocale, converter, splitOn,
                  writeDelimiter, collectionType, capture,
                  format));
        } else {
          fieldMap.put(columnName, new BeanFieldSplit<>(
              localType,
              localField, required, errorLocale,
              converter, splitOn, writeDelimiter, collectionType,
              capture, format));
        }
      }

      // Then for a multi-column annotation
      else if (localField.isAnnotationPresent(CsvBindAndJoinByName.class)) {
        var annotation = localField
            .getAnnotation(CsvBindAndJoinByName.class);
        required = annotation.required();
        var columnRegex = annotation.column();
        locale = annotation.locale();
        writeLocale = annotation.writeLocaleEqualsReadLocale()
            ? locale : annotation.writeLocale();
        var elementType = annotation.elementType();
        var mapType = annotation.mapType();
        var joinConverter = annotation.converter();
        capture = annotation.capture();
        format = annotation.format();

        var converter = determineConverter(
            localField, elementType, locale,
            writeLocale, joinConverter);
        if (StringUtils.isEmpty(columnRegex)) {
          fieldMap.putComplex(localField.getName(),
              new BeanFieldJoinStringIndex<>(
                  localType,
                  localField, required,
                  errorLocale, converter, mapType, capture,
                  format));
        } else {
          fieldMap.putComplex(columnRegex, new BeanFieldJoinStringIndex<>(
              localType,
              localField, required, errorLocale,
              converter, mapType, capture, format));
        }
      }

      // Otherwise it must be CsvBindByName.
      else {
        var annotation = localField.getAnnotation(CsvBindByName.class);
        required = annotation.required();
        columnName = annotation.column().toUpperCase().trim();
        locale = annotation.locale();
        writeLocale = annotation.writeLocaleEqualsReadLocale()
            ? locale : annotation.writeLocale();
        capture = annotation.capture();
        format = annotation.format();
        var converter = determineConverter(
            localField,
            localField.getType(), locale,
            writeLocale, null);

        if (StringUtils.isEmpty(columnName)) {
          fieldMap.put(localField.getName().toUpperCase(),
              new BeanFieldSingleValue<>(
                  localType,
                  localField, required,
                  errorLocale, converter, capture, format));
        } else {
          fieldMap.put(columnName, new BeanFieldSingleValue<>(
              localType,
              localField, required, errorLocale,
              converter, capture, format));
        }
      }
    }
  }

  /**
   * Returns a set of the annotations that are used for binding in this
   * mapping strategy.
   * <p>In this mapping strategy, those are currently:<ul>
   * <li>{@link CsvBindByName}</li>
   * <li>{@link CsvCustomBindByName}</li>
   * <li>{@link CsvBindAndJoinByName}</li>
   * <li>{@link CsvBindAndSplitByName}</li>
   * </ul></p>
   */
  @Override
  protected Set<Class<? extends Annotation>> getBindingAnnotations() {
    // With Java 9 this can be done more easily with Set.of()
    return Set.of(
        CsvBindByName.class,
        CsvBindAndSplitByName.class);
  }
}