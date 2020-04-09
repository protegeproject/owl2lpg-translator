package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import edu.stanford.owl2lpg.translator.vocab.PropertyNames;
import org.semanticweb.owlapi.model.HasIRI;
import org.semanticweb.owlapi.model.IRI;

import javax.annotation.Nonnull;

import static edu.stanford.owl2lpg.model.GraphFactory.Node;
import static edu.stanford.owl2lpg.model.GraphFactory.withIdentifierFrom;
import static edu.stanford.owl2lpg.translator.utils.PropertiesFactory.Properties;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class VisitorBase {

  protected Translation createIriTranslation(@Nonnull HasIRI entity) {
    return createIriTranslation(entity.getIRI());
  }

  protected Translation createIriTranslation(@Nonnull IRI iri) {
    var iriNode = createIriNode(iri);
    return Translation.create(iriNode, ImmutableList.of(), ImmutableList.of());
  }

  protected Node createIriNode(@Nonnull HasIRI entity) {
    return createIriNode(entity.getIRI());
  }

  protected Node createIriNode(@Nonnull IRI iri) {
    return Node(NodeLabels.IRI,
        Properties(PropertyNames.IRI, iri.toString()),
        withIdentifierFrom(iri));
  }
}
