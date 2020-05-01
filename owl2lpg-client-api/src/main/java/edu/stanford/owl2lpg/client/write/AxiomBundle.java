package edu.stanford.owl2lpg.client.write;

import com.google.auto.value.AutoValue;
import edu.stanford.owl2lpg.versioning.model.AxiomContext;
import org.semanticweb.owlapi.model.OWLAxiom;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class AxiomBundle {

  public static AxiomBundle create(@Nonnull AxiomContext context, @Nonnull OWLAxiom axiom) {
    return new AutoValue_AxiomBundle(context, axiom);
  }

  public abstract AxiomContext getContext();

  public abstract OWLAxiom getAxiom();
}
