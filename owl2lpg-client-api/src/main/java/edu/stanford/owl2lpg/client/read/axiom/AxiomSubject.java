package edu.stanford.owl2lpg.client.read.axiom;

import com.google.auto.value.AutoValue;
import org.semanticweb.owlapi.model.OWLAxiom;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class AxiomSubject {

  public static AxiomSubject create(@Nonnull Collection<OWLAxiom> axioms,
                                    @Nonnull DictionaryNameIndex dictionaryNameIndex) {
    return new AutoValue_AxiomSubject(axioms, dictionaryNameIndex);
  }

  @Nonnull
  public abstract Collection<OWLAxiom> getAxioms();

  @Nonnull
  public abstract DictionaryNameIndex getDictionaryNameIndex();
}
