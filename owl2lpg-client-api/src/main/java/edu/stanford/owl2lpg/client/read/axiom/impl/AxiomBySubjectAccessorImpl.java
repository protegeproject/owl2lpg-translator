package edu.stanford.owl2lpg.client.read.axiom.impl;

import com.google.common.collect.ImmutableSet;
import edu.stanford.owl2lpg.client.read.Parameters;
import edu.stanford.owl2lpg.client.read.axiom.AxiomBySubjectAccessor;
import edu.stanford.owl2lpg.client.read.axiom.AxiomContext;
import edu.stanford.owl2lpg.client.read.axiom.NodeIndex;
import edu.stanford.owl2lpg.client.read.axiom.NodeMapper;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.Path;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.client.util.Resources.read;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.AXIOM;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AxiomBySubjectAccessorImpl implements AxiomBySubjectAccessor {

  private static final String CLASS_AXIOM_BY_SUBJECT_QUERY_FILE = "axioms/class-axiom-by-subject.cpy";
  private static final String NAMED_INDIVIDUAL_AXIOM_BY_SUBJECT_QUERY_FILE = "axioms/named-individual-axiom-by-subject.cpy";
  private static final String ANY_AXIOM_BY_SUBJECT_QUERY_FILE = "axioms/axiom-by-subject.cpy";

  private static final String CLASS_AXIOM_BY_SUBJECT_QUERY = read(CLASS_AXIOM_BY_SUBJECT_QUERY_FILE);
  private static final String NAMED_INDIVIDUAL_AXIOM_BY_SUBEJCT_QUERY = read(NAMED_INDIVIDUAL_AXIOM_BY_SUBJECT_QUERY_FILE);
  private static final String ANY_AXIOM_BY_SUBJECT_QUERY = read(ANY_AXIOM_BY_SUBJECT_QUERY_FILE);

  @Nonnull
  private final Driver driver;

  @Nonnull
  private final NodeMapper nodeMapper;

  @Inject
  public AxiomBySubjectAccessorImpl(@Nonnull Driver driver,
                                    @Nonnull NodeMapper nodeMapper) {
    this.driver = checkNotNull(driver);
    this.nodeMapper = checkNotNull(nodeMapper);
  }

  @Override
  public Set<OWLAxiom> getAxiomsForSubject(OWLClass subject, AxiomContext context) {
    return getAxiomsForSubject(CLASS_AXIOM_BY_SUBJECT_QUERY, subject, context);
  }

  @Override
  public Set<OWLAxiom> getAxiomsForSubject(OWLNamedIndividual subject, AxiomContext context) {
    return getAxiomsForSubject(NAMED_INDIVIDUAL_AXIOM_BY_SUBEJCT_QUERY, subject, context);
  }

  @Override
  public Set<OWLAxiom> getAxiomsForSubject(OWLEntity subject, AxiomContext context) {
    return getAxiomsForSubject(ANY_AXIOM_BY_SUBJECT_QUERY, subject, context);
  }

  @Override
  public Set<OWLAxiom> getAxiomsForSubjects(Collection<OWLEntity> entities, AxiomContext context) {
    return entities.stream()
        .flatMap(entity -> getAxiomsForSubject(entity, context).stream())
        .collect(ImmutableSet.toImmutableSet());
  }

  private Set<OWLAxiom> getAxiomsForSubject(String queryString, OWLEntity subject, AxiomContext context) {
    var nodeIndex = getNodeIndex(queryString, subject, context);
    return collectAxiomsFromIndex(nodeIndex);
  }

  private NodeIndex getNodeIndex(String queryString, OWLEntity subject, AxiomContext context) {
    try (var session = driver.session()) {
      var inputParams = createInputParams(subject, context);
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
  private Set<OWLAxiom> collectAxiomsFromIndex(@Nonnull NodeIndex nodeIndex) {
    return nodeIndex.getNodes(AXIOM.getMainLabel())
        .stream()
        .map(axiomNode -> nodeMapper.toObject(axiomNode, nodeIndex, OWLAxiom.class))
        .collect(Collectors.toSet());
  }

  @Nonnull
  private static Value createInputParams(OWLEntity entity, AxiomContext context) {
    return Parameters.forEntityIri(entity.getIRI(), context.getProjectId(), context.getBranchId(), context.getOntologyDocumentId());
  }
}
