package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.NodeIdProvider;
import edu.stanford.owl2lpg.model.StructuralEdgeFactory;
import edu.stanford.owl2lpg.model.Translation;
import edu.stanford.owl2lpg.translator.EntityTranslator;
import edu.stanford.owl2lpg.translator.PropertyExpressionTranslator;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLPropertyExpressionVisitorEx;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.OBJECT_INVERSE_OF;

/**
 * A visitor that contains the implementation to translate the OWL 2 property expressions.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class PropertyExpressionVisitor implements OWLPropertyExpressionVisitorEx<Translation> {

  @Nonnull
  private final StructuralEdgeFactory structuralEdgeFactory;

  @Nonnull
  private final EntityTranslator entityTranslator;

  @Nonnull
  private final PropertyExpressionTranslator propertyExprTranslator;

  @Nonnull
  private final NodeIdProvider nodeIdProvider;

  @Inject
  public PropertyExpressionVisitor(@Nonnull StructuralEdgeFactory structuralEdgeFactory,
                                   @Nonnull EntityTranslator entityTranslator,
                                   @Nonnull PropertyExpressionTranslator propertyExprTranslator,
                                   @Nonnull NodeIdProvider nodeIdProvider) {
    this.structuralEdgeFactory = checkNotNull(structuralEdgeFactory);
    this.entityTranslator = checkNotNull(entityTranslator);
    this.propertyExprTranslator = checkNotNull(propertyExprTranslator);
    this.nodeIdProvider = checkNotNull(nodeIdProvider);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataProperty dp) {
    return entityTranslator.translate(dp);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnnotationProperty ap) {
    return entityTranslator.translate(ap);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectProperty op) {
    return entityTranslator.translate(op);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectInverseOf ope) {
    var nodeId = nodeIdProvider.getId(ope);
    var mainNode = Node.create(nodeId, OBJECT_INVERSE_OF);
    var inversePropertyTranslation = propertyExprTranslator.translate(ope.getInverseProperty());
    var objectPropertyEdge = structuralEdgeFactory.getObjectPropertyEdge(mainNode, inversePropertyTranslation.getMainNode());
    return Translation.create(ope, mainNode,
        ImmutableList.of(objectPropertyEdge),
        ImmutableList.of(inversePropertyTranslation));
  }
}
