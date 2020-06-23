package edu.stanford.owl2lpg.client.read.axiom.handlers;

import edu.stanford.owl2lpg.client.read.axiom.NodeIndex;
import edu.stanford.owl2lpg.client.read.axiom.NodeMapper;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;
import org.neo4j.driver.types.Node;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

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
    if (languageTag.isNull()) {
      var datatypeIri = IRI.create(node.get(PropertyFields.DATATYPE).asString());
      return dataFactory.getOWLLiteral(lexicalForm, OWL2Datatype.getDatatype(datatypeIri));
    } else {
      return dataFactory.getOWLLiteral(lexicalForm, languageTag.asString());
    }
  }
}
