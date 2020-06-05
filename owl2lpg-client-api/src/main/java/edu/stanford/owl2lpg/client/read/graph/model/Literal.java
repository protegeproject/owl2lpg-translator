package edu.stanford.owl2lpg.client.read.graph.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@NodeEntity(label = "Literal")
public class Literal extends GraphObject
    implements HasToOwlObject<OWLLiteral> {

  private String lexicalForm;

  private String datatype;

  private String language;

  private Literal() {
  }

  @Nonnull
  public String getLexicalForm() {
    return lexicalForm;
  }

  @Nullable
  public String getDatatype() {
    return datatype;
  }

  @Nullable
  public String getLanguage() {
    return language;
  }

  @Override
  public OWLLiteral toOwlObject(OWLDataFactory dataFactory) {
    if (Objects.isNull(language)) {
      if (Objects.isNull(datatype)) {
        return dataFactory.getOWLLiteral(lexicalForm);
      } else {
        return dataFactory.getOWLLiteral(
            lexicalForm,
            dataFactory.getOWLDatatype(IRI.create(datatype)));
      }
    } else {
      return dataFactory.getOWLLiteral(lexicalForm, language);
    }
  }
}
