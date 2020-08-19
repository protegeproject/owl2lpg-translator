package edu.stanford.owl2lpg.client.read.axiom;

import com.google.common.collect.ImmutableSet;
import edu.stanford.owl2lpg.client.read.Parameters;
import org.neo4j.driver.Driver;
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
      "axioms/annotation-assertion-axiom-by-annotation-subject.cpy.cpy";

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
      var nodeIndex = getNodeIndex(context, owlIndividual.asOWLNamedIndividual().getIRI(),
          CLASS_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY);
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
      var nodeIndex = getNodeIndex(context, owlIndividual.asOWLNamedIndividual().getIRI(),
          OBJECT_PROPERTY_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY);
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
      var nodeIndex = getNodeIndex(context, owlIndividual.asOWLNamedIndividual().getIRI(),
          DATA_PROPERTY_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY);
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
      var nodeIndex = getNodeIndex(context, (IRI) owlAnnotationSubject,
          DATA_PROPERTY_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY);
      return collectAnnotationAssertionAxiomsFromIndex(nodeIndex);
    } else {
      return ImmutableSet.of();
    }
  }

  private NodeIndex getNodeIndex(AxiomContext context, IRI subjectIri, String queryString) {
    try (var session = driver.session()) {
      var args = Parameters.forEntityIri(context, subjectIri);
      return session.readTransaction(tx -> {
        var result = tx.run(queryString, args);
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
}
