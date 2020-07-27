package edu.stanford.owl2lpg.client.read.shortform;

import edu.stanford.bmir.protege.web.shared.shortform.DictionaryLanguage;
import org.neo4j.driver.types.Node;
import org.semanticweb.owlapi.model.OWLEntity;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface Neo4jNodeTranslator {

  OWLEntity getOwlEntity(Node entityNode);

  String getShortForm(Node literalNode);

  DictionaryLanguage getDictionaryLanguage(Node propertyNode, Node literalNode);
}
