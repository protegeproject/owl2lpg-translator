package edu.stanford.owl2lpg.client.read.handlers;

import edu.stanford.owl2lpg.client.read.NodeIndex;
import edu.stanford.owl2lpg.client.read.NodeMapper;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;
import org.neo4j.driver.types.Node;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.LITERAL;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class OwlLiteralNodeHandler implements NodeHandler<OWLLiteral> {

  @Nonnull
  private final OWLDataFactory dataFactory;

  @Inject
  public OwlLiteralNodeHandler(@Nonnull OWLDataFactory dataFactory) {
    this.dataFactory = checkNotNull(dataFactory);
  }

  @Override
  public String getLabel() {
    return LITERAL.getMainLabel();
  }

  @Override
  public OWLLiteral handle(Node node, NodeIndex nodeIndex, NodeMapper nodeMapper) {
    var lexicalForm = node.get(PropertyFields.LEXICAL_FORM).asString();
    var languageTag = node.get(PropertyFields.LANGUAGE);
    var datatype = node.get(PropertyFields.DATATYPE);
    if (languageTag.isNull()) {
      var datatypeIri = IRI.create(datatype.asString());
      return dataFactory.getOWLLiteral(lexicalForm, dataFactory.getOWLDatatype(datatypeIri));
    } else {
      return dataFactory.getOWLLiteral(lexicalForm, languageTag.asString());
    }
  }
}
