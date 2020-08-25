package edu.stanford.owl2lpg.client.read.signature;

import edu.stanford.owl2lpg.model.ProjectId;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface ProjectSignatureAccessor {

  @Nonnull
  Set<OWLEntity> getEntitiesInSignature(@Nonnull IRI iri, @Nonnull ProjectId projectId);

  boolean containsEntityInSignature(@Nonnull OWLEntity owlEntity, @Nonnull ProjectId projectId);
}
