package edu.stanford.owl2lpg.client.write.handlers;

import edu.stanford.owl2lpg.model.Translation;
import edu.stanford.owl2lpg.model.TranslationVisitor;
import org.semanticweb.owlapi.model.OWLAxiom;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class DeleteQueryBuilder implements TranslationVisitor {

  @Nonnull
  private final VariableNameGenerator variableNameGenerator;

  private final StringBuilder stringBuilder = new StringBuilder();

  public DeleteQueryBuilder(@Nonnull VariableNameGenerator variableNameGenerator) {
    this.variableNameGenerator = checkNotNull(variableNameGenerator);
  }

  @Override
  public void visit(@Nonnull Translation translation) {
    var translatedObject = translation.getTranslatedObject();
    if (translatedObject instanceof OWLAxiom) {
      var mainNode = translation.getMainNode();
      var variableName = variableNameGenerator.generate();
      stringBuilder.append("MATCH ")
          .append("(")
          .append(variableName)
          .append(mainNode.printLabels()).append(" ").append(mainNode.printProperties())
          .append(")\n")
          .append("DETACH DELETE ").append(variableName);
    }
  }

  public String build() {
    return stringBuilder.toString();
  }
}
