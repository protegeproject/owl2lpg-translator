package edu.stanford.owl2lpg.client.read.axiom.impl;

import com.google.common.collect.ImmutableSet;
import edu.stanford.owl2lpg.client.read.Parameters;
import edu.stanford.owl2lpg.client.read.axiom.AssertionAxiomBySubjectAccessor;
import edu.stanford.owl2lpg.client.read.axiom.AxiomContext;
import edu.stanford.owl2lpg.client.read.axiom.NodeIndex;
import edu.stanford.owl2lpg.client.read.axiom.NodeMapper;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.Path;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.client.util.Resources.read;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.ANNOTATION_ASSERTION;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.CLASS_ASSERTION;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.DATA_PROPERTY_ASSERTION;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.OBJECT_PROPERTY_ASSERTION;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AssertionAxiomBySubjectAccessorImpl implements AssertionAxiomBySubjectAccessor {

  private static final String CLASS_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY_FILE =
      "axioms/class-assertion-axiom-by-individual.cpy";
  private static final String OBJECT_PROPERTY_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY_FILE =
      "axioms/object-property-assertion-axiom-by-individual.cpy";
  private static final String DATA_PROPERTY_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY_FILE =
      "axioms/data-property-assertion-axiom-by-individual.cpy";
  private static final String ANNOTATION_ASSERTION_AXIOM_BY_ANNOTATION_SUBJECT_QUERY_FILE =
      "axioms/annotation-assertion-axiom-by-annotation-subject.cpy";

  private static final String CLASS_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY =
      read(CLASS_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY_FILE);
  private static final String OBJECT_PROPERTY_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY =
      read(OBJECT_PROPERTY_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY_FILE);
  private static final String DATA_PROPERTY_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY =
      read(DATA_PROPERTY_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY_FILE);
  private static final String ANNOTATION_ASSERTION_AXIOM_BY_ANNOTATION_SUBJECT_QUERY =
      read(ANNOTATION_ASSERTION_AXIOM_BY_ANNOTATION_SUBJECT_QUERY_FILE);

  @Nonnull
  private final Driver driver;

  @Nonnull
  private final NodeMapper nodeMapper;

  @Inject
  public AssertionAxiomBySubjectAccessorImpl(@Nonnull Driver driver,
                                             @Nonnull NodeMapper nodeMapper) {
    this.driver = checkNotNull(driver);
    this.nodeMapper = checkNotNull(nodeMapper);
  }

  @Nonnull
  @Override
  public Set<OWLClassAssertionAxiom> getClassAssertionForSubject(OWLIndividual owlIndividual,
                                                                 AxiomContext context) {
    if (owlIndividual.isNamed()) {
      var nodeIndex = getNodeIndex(CLASS_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY, owlIndividual.asOWLNamedIndividual().getIRI(), context
      );
      return collectClassAssertionAxiomsFromIndex(nodeIndex);
    } else {
      return ImmutableSet.of();
    }
  }

  @Nonnull
  @Override
  public Set<OWLObjectPropertyAssertionAxiom> getObjectPropertyAssertionsForSubject(OWLIndividual owlIndividual,
                                                                                    AxiomContext context) {
    if (owlIndividual.isNamed()) {
      var nodeIndex = getNodeIndex(OBJECT_PROPERTY_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY, owlIndividual.asOWLNamedIndividual().getIRI(), context
      );
      return collectObjectPropertyAssertionAxiomsFromIndex(nodeIndex);
    } else {
      return ImmutableSet.of();
    }
  }

  @Nonnull
  @Override
  public Set<OWLDataPropertyAssertionAxiom> getDataPropertyAssertionsForSubject(OWLIndividual owlIndividual,
                                                                                AxiomContext context) {
    if (owlIndividual.isNamed()) {
      var nodeIndex = getNodeIndex(DATA_PROPERTY_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY, owlIndividual.asOWLNamedIndividual().getIRI(), context
      );
      return collectDataPropertyAssertionAxiomsFromIndex(nodeIndex);
    } else {
      return ImmutableSet.of();
    }
  }

  @Nonnull
  @Override
  public Set<OWLAnnotationAssertionAxiom> getAnnotationAssertionsForSubject(OWLAnnotationSubject owlAnnotationSubject,
                                                                            AxiomContext context) {
    if (owlAnnotationSubject.isIRI()) {
      var nodeIndex = getNodeIndex(ANNOTATION_ASSERTION_AXIOM_BY_ANNOTATION_SUBJECT_QUERY, (IRI) owlAnnotationSubject, context
      );
      return collectAnnotationAssertionAxiomsFromIndex(nodeIndex);
    } else {
      return ImmutableSet.of();
    }
  }

  private NodeIndex getNodeIndex(String queryString, IRI subjectIri, AxiomContext context) {
    try (var session = driver.session()) {
      var inputParams = createInputParams(subjectIri, context);
      return session.readTransaction(tx -> {
        var result = tx.run(queryString, inputParams);
        var nodeIndexBuilder = new NodeIndexImpl.Builder();
        while (result.hasNext()) {
          var row = result.next().asMap();
          for (var column : row.entrySet()) {
            if (column.getKey().equals("p")) {
              var path = (Path) column.getValue();
              if (path != null) {
                path.spliterator().forEachRemaining(nodeIndexBuilder::add);
              }
            }
          }
        }
        return nodeIndexBuilder.build();
      });
    }
  }

  @Nonnull
  private Set<OWLClassAssertionAxiom> collectClassAssertionAxiomsFromIndex(@Nonnull NodeIndex nodeIndex) {
    return nodeIndex.getNodes(CLASS_ASSERTION.getMainLabel())
        .stream()
        .map(axiomNode -> nodeMapper.toObject(axiomNode, nodeIndex, OWLClassAssertionAxiom.class))
        .collect(ImmutableSet.toImmutableSet());
  }

  @Nonnull
  private Set<OWLObjectPropertyAssertionAxiom> collectObjectPropertyAssertionAxiomsFromIndex(@Nonnull NodeIndex nodeIndex) {
    return nodeIndex.getNodes(OBJECT_PROPERTY_ASSERTION.getMainLabel())
        .stream()
        .map(axiomNode -> nodeMapper.toObject(axiomNode, nodeIndex, OWLObjectPropertyAssertionAxiom.class))
        .collect(ImmutableSet.toImmutableSet());
  }

  @Nonnull
  private Set<OWLDataPropertyAssertionAxiom> collectDataPropertyAssertionAxiomsFromIndex(@Nonnull NodeIndex nodeIndex) {
    return nodeIndex.getNodes(DATA_PROPERTY_ASSERTION.getMainLabel())
        .stream()
        .map(axiomNode -> nodeMapper.toObject(axiomNode, nodeIndex, OWLDataPropertyAssertionAxiom.class))
        .collect(ImmutableSet.toImmutableSet());
  }

  @Nonnull
  private Set<OWLAnnotationAssertionAxiom> collectAnnotationAssertionAxiomsFromIndex(@Nonnull NodeIndex nodeIndex) {
    return nodeIndex.getNodes(ANNOTATION_ASSERTION.getMainLabel())
        .stream()
        .map(axiomNode -> nodeMapper.toObject(axiomNode, nodeIndex, OWLAnnotationAssertionAxiom.class))
        .collect(ImmutableSet.toImmutableSet());
  }

  @Nonnull
  private static Value createInputParams(IRI entityIri, AxiomContext context) {
    return Parameters.forEntityIri(entityIri, context.getProjectId(), context.getBranchId(), context.getOntologyDocumentId());
  }
}
