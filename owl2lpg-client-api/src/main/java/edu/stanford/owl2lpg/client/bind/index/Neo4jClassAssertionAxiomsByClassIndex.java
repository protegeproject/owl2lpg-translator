package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.ClassAssertionAxiomsByClassIndex;
import edu.stanford.owl2lpg.client.DocumentIdMap;
import edu.stanford.owl2lpg.client.read.axiom.ClassAssertionAxiomAccessor;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntologyID;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.bmir.protege.web.shared.DataFactory.getOWLThing;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jClassAssertionAxiomsByClassIndex implements ClassAssertionAxiomsByClassIndex {

  @Nonnull
  private final OWLClass root;

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final DocumentIdMap documentIdMap;

  @Nonnull
  private final ClassAssertionAxiomAccessor classAssertionAxiomAccessor;

  @Inject
  public Neo4jClassAssertionAxiomsByClassIndex(@Nonnull OWLClass root,
                                               @Nonnull ProjectId projectId,
                                               @Nonnull BranchId branchId,
                                               @Nonnull DocumentIdMap documentIdMap,
                                               @Nonnull ClassAssertionAxiomAccessor classAssertionAxiomAccessor) {
    this.root = checkNotNull(root);
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.documentIdMap = checkNotNull(documentIdMap);
    this.classAssertionAxiomAccessor = checkNotNull(classAssertionAxiomAccessor);
  }

  @Nonnull
  @Override
  public Stream<OWLClassAssertionAxiom> getClassAssertionAxioms(@Nonnull OWLClass owlClass,
                                                                @Nonnull OWLOntologyID ontologyId) {
    var documentId = documentIdMap.get(projectId, ontologyId);
    if (root.equals(getOWLThing()) && root.equals(owlClass)) {
      return classAssertionAxiomAccessor.getAllAxioms(projectId, branchId, documentId).stream();
    } else {
      return classAssertionAxiomAccessor.getAxiomsByClass(owlClass, projectId, branchId, documentId).stream();
    }
  }
}
