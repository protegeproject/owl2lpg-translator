package edu.stanford.owl2lpg.client.read.graph.model;

import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Required;
import org.neo4j.ogm.session.Session;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@NodeEntity(label = "Literal")
public class Literal extends GraphObject
    implements HasToOwlObject<OWLLiteral> {

  @Property
  @Required
  @Index
  private String lexicalForm;

  @Property
  private String datatype;

  @Property
  private String language;

  private Literal() {
  }

  public Literal(@Nonnull String lexicalForm,
                 @Nonnull String datatype,
                 @Nonnull String language) {
    this.lexicalForm = checkNotNull(lexicalForm);
    this.datatype = checkNotNull(datatype);
    this.language = checkNotNull(language);
  }

  @Nullable
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
  public OWLLiteral toOwlObject(OWLDataFactory dataFactory, Session session) {
    // we don't check language and datatype because we allow
    // those properties to be null
    if (lexicalForm == null) {
      var nodeEntity = reloadThisNodeEntity(session);
      return nodeEntity.toOwlObject(dataFactory, session);
    } else {
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

  private Literal reloadThisNodeEntity(Session session) {
    return session.load(getClass(), getId(), 1);
  }
}
