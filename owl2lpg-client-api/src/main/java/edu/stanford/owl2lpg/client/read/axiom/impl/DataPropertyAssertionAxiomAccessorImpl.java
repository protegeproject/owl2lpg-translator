package edu.stanford.owl2lpg.client.read.axiom.impl;

import com.google.common.collect.ImmutableSet;
import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.OntologyDocumentId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.owl2lpg.client.read.GraphReader;
import edu.stanford.owl2lpg.client.read.NodeIndex;
import edu.stanford.owl2lpg.client.read.NodeMapper;
import edu.stanford.owl2lpg.client.read.Parameters;
import edu.stanford.owl2lpg.client.read.axiom.AxiomAccessor;
import edu.stanford.owl2lpg.client.read.axiom.DataPropertyAssertionAxiomAccessor;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.client.util.Resources.read;
import static org.semanticweb.owlapi.model.AxiomType.DATA_PROPERTY_ASSERTION;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class DataPropertyAssertionAxiomAccessorImpl implements DataPropertyAssertionAxiomAccessor {

  private static final String DATA_PROPERTY_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY_FILE =
      "read/axioms/data-property-assertion-axiom-by-individual.cpy";
  private static final String DATA_PROPERTY_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY_FILE =
      "read/axioms/data-property-assertion-axiom-by-anonymous-individual.cpy";

  private static final String DATA_PROPERTY_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY =
      read(DATA_PROPERTY_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY_FILE);
  private static final String DATA_PROPERTY_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY =
      read(DATA_PROPERTY_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY_FILE);

  @Nonnull
  private final GraphReader graphReader;

  @Nonnull
  private final NodeMapper nodeMapper;

  @Nonnull
  private final AxiomAccessor axiomAccessor;

  @Inject
  public DataPropertyAssertionAxiomAccessorImpl(@Nonnull GraphReader graphReader,
                                                @Nonnull NodeMapper nodeMapper,
                                                @Nonnull AxiomAccessor axiomAccessor) {
    this.graphReader = checkNotNull(graphReader);
    this.nodeMapper = checkNotNull(nodeMapper);
    this.axiomAccessor = checkNotNull(axiomAccessor);
  }

  @Nonnull
  @Override
  public ImmutableSet<OWLDataPropertyAssertionAxiom> getAllAxioms(@Nonnull ProjectId projectId, @Nonnull BranchId branchId, @Nonnull OntologyDocumentId ontoDocId) {
    return axiomAccessor.getAxiomsByType(DATA_PROPERTY_ASSERTION, projectId, branchId, ontoDocId);
  }

  @Nonnull
  @Override
  public ImmutableSet<OWLDataPropertyAssertionAxiom> getAxiomsBySubject(@Nonnull OWLIndividual owlIndividual, @Nonnull ProjectId projectId, @Nonnull BranchId branchId, @Nonnull OntologyDocumentId ontoDocId) {
    var nodeIndex = (owlIndividual.isNamed()) ?
        graphReader.getNodeIndex(DATA_PROPERTY_ASSERTION_AXIOM_BY_INDIVIDUAL_QUERY,
            Parameters.forEntityIri(owlIndividual.asOWLNamedIndividual().getIRI(), projectId, branchId, ontoDocId)) :
        graphReader.getNodeIndex(DATA_PROPERTY_ASSERTION_AXIOM_BY_ANONYMOUS_INDIVIDUAL_QUERY,
            Parameters.forNodeId(owlIndividual.asOWLAnonymousIndividual().getID(), projectId, branchId, ontoDocId));
    return collectDataPropertyAssertionAxiomsFromIndex(nodeIndex);
  }

  @Nonnull
  private ImmutableSet<OWLDataPropertyAssertionAxiom> collectDataPropertyAssertionAxiomsFromIndex(@Nonnull NodeIndex nodeIndex) {
    return nodeIndex.getNodes(NodeLabels.DATA_PROPERTY_ASSERTION.getMainLabel())
        .stream()
        .map(axiomNode -> nodeMapper.toObject(axiomNode, nodeIndex, OWLDataPropertyAssertionAxiom.class))
        .collect(ImmutableSet.toImmutableSet());
  }
}
