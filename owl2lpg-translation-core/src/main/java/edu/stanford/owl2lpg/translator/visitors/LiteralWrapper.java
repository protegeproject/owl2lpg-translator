package edu.stanford.owl2lpg.translator.visitors;

import com.google.auto.value.AutoValue;
import org.semanticweb.owlapi.model.OWLLiteral;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class LiteralWrapper {

  public static LiteralWrapper create(@Nonnull String literal,
                                      @Nonnull String datatype,
                                      @Nonnull String language) {
    return new AutoValue_LiteralWrapper(literal, datatype, language);
  }

  public static LiteralWrapper create(@Nonnull OWLLiteral literal) {
    return create(literal.getLiteral(),
        literal.getDatatype().toStringID(),
        literal.getLang());
  }

  public abstract String getLiteral();

  public abstract String getDatatype();

  public abstract String getLanguage();
}
