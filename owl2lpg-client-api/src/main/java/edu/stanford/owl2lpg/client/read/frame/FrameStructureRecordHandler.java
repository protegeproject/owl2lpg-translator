package edu.stanford.owl2lpg.client.read.frame;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import edu.stanford.bmir.protege.web.shared.entity.OWLClassData;
import edu.stanford.bmir.protege.web.shared.entity.OWLObjectPropertyData;
import edu.stanford.bmir.protege.web.shared.frame.*;
import edu.stanford.bmir.protege.web.shared.shortform.DictionaryLanguage;
import org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.IRI;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.ObjectProperty;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class FrameStructureRecordHandler {

  @Inject
  public FrameStructureRecordHandler() {

  }

  public OWLClassData getClass(Map<String, Object> row) {
    var subjectMap = get("subjectClass", row, Map.class);
    return getClassData(subjectMap);
  }

  public OWLObjectPropertyData getObjectProperty(Map<String, Object> row) {
    return null;
  }

  public ImmutableSet<OWLClassData> getClassEntries(Map<String, Object> row) {
    var listOfClassEntries = get("classEntries", row, List.class);
    return getClassData(listOfClassEntries);
  }

  public ImmutableSet<PropertyValue> getPropertyValues(Map<String, Object> row) {
    var listOfPropertyValues = get("propertyValues", row, List.class);
    return getObjectPropertyData(listOfPropertyValues);
  }

  public ImmutableSet<PropertyAnnotationValue> getAnnotationValues(Map<String, Object> row) {
    return null;
  }

  public ImmutableSet<OWLClassData> getDomains(Map<String, Object> row) {
    return null;
  }

  public ImmutableSet<OWLClassData> getRanges(Map<String, Object> row) {
    return null;
  }

  public ImmutableSet<OWLObjectPropertyData> getInverseProperties(Map<String, Object> row) {
    return null;
  }

  public ImmutableSet<ObjectPropertyCharacteristic> getObjectPropertyCharacteristics(Map<String, Object> row) {
    return null;
  }

  @SuppressWarnings("unchecked")
  private OWLClassData getClassData(Map<String, Object> classMap) {
    var classIriString = get("iri", classMap, String.class);
    var cls = OWLFunctionalSyntaxFactory.Class(IRI(classIriString));
    var browserText = cls.getIRI().getShortForm();
    var shortFormList = get("shortForms", classMap, List.class);
    var shortForms = getShortForms(shortFormList);
    return OWLClassData.get(cls, browserText, shortForms);
  }

  private ImmutableSet<OWLClassData> getClassData(
      List<Map<String, Object>> listOfClassMaps) {
    return listOfClassMaps.stream()
        .map(this::getClassData)
        .collect(ImmutableSet.toImmutableSet());
  }

  @SuppressWarnings("unchecked")
  private OWLObjectPropertyData getObjectPropertyData(Map<String, Object> propertyMap) {
    var propertyIriString = get("iri", propertyMap, String.class);
    var op = ObjectProperty(IRI(propertyIriString));
    var browserText = op.getIRI().getShortForm();
    var shortFormList = get("shortForms", propertyMap, List.class);
    var shortForms = getShortForms(shortFormList);
    return OWLObjectPropertyData.get(op, browserText, shortForms);
  }

  @SuppressWarnings("unchecked")
  private ImmutableSet<PropertyClassValue> getObjectPropertyData(
      List<Map<String, Object>> listOfPropertyValues) {
    return listOfPropertyValues.stream()
        .map(propertyValueMap -> {
          var propertyMap = get("property", propertyValueMap, Map.class);
          var property = getObjectPropertyData(propertyMap);
          var valueMap = get("value", propertyValueMap, Map.class);
          var value = getClassData(valueMap);
          return PropertyClassValue.get(property, value, State.ASSERTED);
        })
        .collect(ImmutableSet.toImmutableSet());
  }

  private ImmutableMap<DictionaryLanguage, String> getShortForms(
      List<Map<String, Object>> listOfShortForms) {
    return listOfShortForms.stream()
        .collect(ImmutableMap.toImmutableMap(
            key -> DictionaryLanguage.rdfsLabel(get("language", key, String.class)),
            value -> get("label", value, String.class)
        ));
  }

  private <T> T get(String key, Map<String, Object> map, Class<T> castTo) {
    return castTo.cast(map.get(key));
  }
}
