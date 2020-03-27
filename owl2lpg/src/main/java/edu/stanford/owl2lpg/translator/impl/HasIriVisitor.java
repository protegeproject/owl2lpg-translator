package edu.stanford.owl2lpg.translator.impl;

import edu.stanford.owl2lpg.datastructure.Node;
import edu.stanford.owl2lpg.translator.NodeLabels;
import edu.stanford.owl2lpg.translator.PropertyNames;
import org.semanticweb.owlapi.model.HasIRI;

import static edu.stanford.owl2lpg.datastructure.GraphFactory.Node;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class HasIriVisitor {

  protected Node createIriNode(HasIRI entity) {
    return Node(NodeLabels.IRI,
        PropertiesBuilder.create()
            .set(PropertyNames.IRI, entity.getIRI())
            .build());
  }
}
