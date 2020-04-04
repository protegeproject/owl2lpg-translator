package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import edu.stanford.owl2lpg.translator.vocab.PropertyNames;
import org.semanticweb.owlapi.model.HasIRI;

import javax.annotation.Nonnull;

import static edu.stanford.owl2lpg.model.GraphFactory.Node;
import static edu.stanford.owl2lpg.translator.utils.PropertiesFactory.Properties;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class HasIriVisitor {

  protected Translation createIriNode(@Nonnull HasIRI entity) {
    var iriNode = Node(NodeLabels.IRI, Properties(PropertyNames.IRI, entity.getIRI().toString()));
    return Translation.create(iriNode, ImmutableList.of(), ImmutableList.of());
  }
}
