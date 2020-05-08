package edu.stanford.owl2lpg.client.read.frame;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import edu.stanford.bmir.protege.web.shared.entity.OWLClassData;
import edu.stanford.bmir.protege.web.shared.entity.OWLObjectPropertyData;
import edu.stanford.bmir.protege.web.shared.frame.ClassFrame;
import edu.stanford.bmir.protege.web.shared.frame.PropertyClassValue;
import edu.stanford.bmir.protege.web.shared.frame.PropertyValue;
import edu.stanford.bmir.protege.web.shared.frame.State;
import edu.stanford.bmir.protege.web.shared.shortform.DictionaryLanguage;
import edu.stanford.owl2lpg.client.read.FrameAccessor;
import edu.stanford.owl2lpg.client.read.statement.SelectResult;
import edu.stanford.owl2lpg.client.shared.Arguments;
import edu.stanford.owl2lpg.versioning.model.AxiomContext;
import org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory;
import org.semanticweb.owlapi.model.OWLClass;

import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.IRI;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.ObjectProperty;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ClassFrameAccessor extends FrameAccessor<ClassFrame> {

  @Override
  protected String getCypherQuery(Arguments arguments) {
    var context = arguments.<AxiomContext>get("context");
    var subject = arguments.<OWLClass>get("subject");
    // @formatter:off
    var queryTemplate = String.join(System.getProperty("line.separator"),
        "MATCH (project)-[:BRANCH]->(branch)-[:ONTOLOGY_DOCUMENT]->(document)-[:AXIOM]->(axiom)",
        "MATCH (axiom)<-[:IS_SUBJECT_OF]-(subject:Class)",
        "WHERE subject.iri = '%s'",
        "AND project.projectId = '%s'",
        "AND branch.branchId = '%s'",
        "AND document.ontologyDocumentId = '%s'",
        "WITH DISTINCT(subject) AS subject",
        "",
        "MATCH (:IRI {iri:subject.iri})-[:RELATED_TO {iri:'http://www.w3.org/2000/01/rdf-schema#label'}]->(subject_label)",
        "WITH subject, COLLECT({ label: subject_label.lexicalForm, language: subject_label.language }) AS shortForms",
        "WITH { iri: subject.iri, shortForms: shortForms } AS subjectClass",
        "",
        "MATCH (subject {iri:subjectClass.iri})-[:SUB_CLASS_OF]->(parent:Class)",
        "MATCH (:IRI {iri:parent.iri})-[:RELATED_TO {iri:'http://www.w3.org/2000/01/rdf-schema#label'}]->(parent_label)",
        "WITH parent, subjectClass,",
        "   COLLECT({ label: parent_label.lexicalForm, language: parent_label.language}) AS shortForms",
        "WITH subjectClass,",
        "	COLLECT({iri: parent.iri, shortForms: shortForms}) AS classEntries",
        "",
        "MATCH (subject {iri:subjectClass.iri})-[property:RELATED_TO]->(filler)",
        "MATCH (:IRI {iri:property.iri})-[:RELATED_TO {iri:'http://www.w3.org/2000/01/rdf-schema#label'}]->(property_label)",
        "WITH property, filler, subjectClass, classEntries,",
        "   COLLECT({ label: property_label.lexicalForm, language: property_label.language}) AS propertyShortForms",
        "MATCH (:IRI {iri:filler.iri})-[:RELATED_TO {iri:'http://www.w3.org/2000/01/rdf-schema#label'}]->(filler_label)",
        "WITH property, filler, subjectClass, classEntries, propertyShortForms,",
        "   COLLECT({ label: filler_label.lexicalForm, language: filler_label.language}) AS fillerShortForms",
        "WITH subjectClass, classEntries,",
        "	  COLLECT({property: {iri: property.iri, shortForms: propertyShortForms}, ",
        "		   value: {iri: filler.iri, shortForms: fillerShortForms}}) AS propertyValues",
        "RETURN subjectClass, classEntries, propertyValues");
    // @formatter:off
    return format(queryTemplate, subject.getIRI(),
        context.getProjectId(),
        context.getBranchId(),
        context.getOntologyDocumentId());
  }

  @SuppressWarnings("unchecked")
  @Override
  protected ClassFrame getFrame(SelectResult result) {
    var expected = 1;
    if (result.size() > expected) {
      throw new RuntimeException("Expecting to return a single row from the class frame query");
    }
    var firstRow = result.get(0);
    var subjectClassMap = get("subjectClass", firstRow, Map.class);
    var subject = getClassData(subjectClassMap);
    var classEntriesList = get("classEntries", firstRow, List.class);
    var mutableClassEntries = Sets.<OWLClassData>newHashSet();
    classEntriesList
        .forEach(item -> {
          var classMap = (Map<String, Object>) item;
          var classData = getClassData(classMap);
          mutableClassEntries.add(classData);
        });
    var classEntries = ImmutableSet.copyOf(mutableClassEntries);
    var propertyValuesList = get("propertyValues", firstRow, List.class);
    var mutablePropertyValues = Sets.<PropertyValue>newHashSet();
    propertyValuesList
        .forEach(item -> {
          var propertyValueMap = (Map<String, Object>) item;
          var property = getObjectPropertyData(get("property", propertyValueMap, Map.class));
          var value = getClassData(get("value", propertyValueMap, Map.class));
          var propertyValue = PropertyClassValue.get(property, value, State.ASSERTED);
          mutablePropertyValues.add(propertyValue);
        });
    var propertyValues = ImmutableSet.copyOf(mutablePropertyValues);
    return ClassFrame.get(subject, classEntries, propertyValues);
  }

  @SuppressWarnings("unchecked")
  private OWLClassData getClassData(Map<String, Object> classMap) {
    var classIriString = get("iri", classMap, String.class);
    var shortFormList = get("shortForms", classMap, List.class);
    var cls = OWLFunctionalSyntaxFactory.Class(IRI(classIriString));
    var browserText = cls.getIRI().getShortForm();
    var mutableShortForms = Maps.<DictionaryLanguage, String>newHashMap();
    shortFormList
        .forEach(item -> {
          var shortFormMap = (Map<String, Object>) item;
          var dictLang = DictionaryLanguage.rdfsLabel(get("language", shortFormMap, String.class));
          var label = get("label", shortFormMap, String.class);
          mutableShortForms.put(dictLang, label);
        });
    var shortForms = ImmutableMap.copyOf(mutableShortForms);
    return OWLClassData.get(cls, browserText, shortForms);
  }

  @SuppressWarnings("unchecked")
  private OWLObjectPropertyData getObjectPropertyData(Map<String, Object> propertyMap) {
    var propertyIriString = get("iri", propertyMap, String.class);
    var shortFormList = get("shortForms", propertyMap, List.class);
    var op = ObjectProperty(IRI(propertyIriString));
    var browserText = op.getIRI().getShortForm();
    var mutableShortForms = Maps.<DictionaryLanguage, String>newHashMap();
    shortFormList
        .forEach(item -> {
          var shortFormMap = (Map<String, Object>) item;
          var dictLang = DictionaryLanguage.rdfsLabel(get("language", shortFormMap, String.class));
          var label = get("label", shortFormMap, String.class);
          mutableShortForms.put(dictLang, label);
        });
    var shortForms = ImmutableMap.copyOf(mutableShortForms);
    return OWLObjectPropertyData.get(op, browserText, shortForms);
  }

  private static <T> T get(String key, Map<String, Object> map, Class<T> castTo) {
    return castTo.cast(map.get(key));
  }
}
