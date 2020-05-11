package edu.stanford.owl2lpg.client.read.frame;

import edu.stanford.bmir.protege.web.shared.frame.ClassFrame;
import edu.stanford.owl2lpg.versioning.model.AxiomContext;
import org.neo4j.driver.Session;
import org.semanticweb.owlapi.model.OWLClass;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ClassFrameAccessorImpl
    implements ClassFrameAccessor, AutoCloseable {

  @Nonnull
  private final Session session;

  @Nonnull
  private final Provider<ClassFrameRecordHandler> provider;

  @Inject
  public ClassFrameAccessorImpl(@Nonnull Session session,
                                @Nonnull Provider<ClassFrameRecordHandler> provider) {
    this.session = checkNotNull(session);
    this.provider = checkNotNull(provider);
  }

  private static String getCypherQuery(AxiomContext context, OWLClass subject) {
    return format(QUERY_TEMPLATE, subject.getIRI(),
        context.getProjectId(),
        context.getBranchId(),
        context.getOntologyDocumentId());
  }

  @Override
  public void close() throws Exception {
    session.close();
  }

  @Nonnull
  @Override
  public Optional<ClassFrame> getFrame(@Nonnull AxiomContext context,
                                       @Nonnull OWLClass subject) {
    var args = Parameters.forSubject(context, subject);
    return session.readTransaction(tx ->
        tx.run(QUERY_TEMPLATE, args)
            .stream()
            .findFirst()
            .map(provider.get()::translate));
  }

  // TODO: Get from a file
  private static final String QUERY_TEMPLATE = String.join(System.getProperty("line.separator"),
      "MATCH (project)-[:BRANCH]->(branch)-[:ONTOLOGY_DOCUMENT]->(document)-[:AXIOM]->(axiom)",
      "MATCH (axiom)<-[:IS_SUBJECT_OF]-(subject:Class)",
      "WHERE subject.iri = $subjectIri",
      "AND project.projectId = $projectId",
      "AND branch.branchId = $branchId",
      "AND document.ontologyDocumentId = $ontoDocId",
      "WITH DISTINCT(subject) AS subject",
      "",
      "MATCH (:IRI {iri:subject.iri})-[:RELATED_TO {iri:'http://www.w3.org/2000/01/rdf-schema#label'}]->" +
          "(subject_label)",
      "WITH subject, COLLECT({ label: subject_label.lexicalForm, language: subject_label.language }) AS shortForms",
      "WITH { iri: subject.iri, shortForms: shortForms } AS subjectClass",
      "",
      "MATCH (subject:Class {iri:subjectClass.iri})-[:SUB_CLASS_OF]->(parent:Class)",
      "MATCH (:IRI {iri:parent.iri})-[:RELATED_TO {iri:'http://www.w3.org/2000/01/rdf-schema#label'}]->" +
          "(parent_label)",
      "WITH parent, subjectClass,",
      "   COLLECT({ label: parent_label.lexicalForm, language: parent_label.language}) AS shortForms",
      "WITH subjectClass,",
      "	COLLECT({iri: parent.iri, shortForms: shortForms}) AS classEntries",
      "",
      "MATCH (subject:Class {iri:subjectClass.iri})-[property:RELATED_TO]->(filler)",
      "MATCH (:IRI {iri:property.iri})-[:RELATED_TO {iri:'http://www.w3.org/2000/01/rdf-schema#label'}]->" +
          "(property_label)",
      "WITH property, filler, subjectClass, classEntries,",
      "   COLLECT({ label: property_label.lexicalForm, language: property_label.language}) AS propertyShortForms",
      "MATCH (:IRI {iri:filler.iri})-[:RELATED_TO {iri:'http://www.w3.org/2000/01/rdf-schema#label'}]->" +
          "(filler_label)",
      "WITH property, filler, subjectClass, classEntries, propertyShortForms,",
      "   COLLECT({ label: filler_label.lexicalForm, language: filler_label.language}) AS fillerShortForms",
      "WITH subjectClass, classEntries,",
      "	  COLLECT({property: {iri: property.iri, shortForms: propertyShortForms}, ",
      "		   value: {iri: filler.iri, shortForms: fillerShortForms}}) AS propertyValues",
      "RETURN subjectClass, classEntries, propertyValues");
}
