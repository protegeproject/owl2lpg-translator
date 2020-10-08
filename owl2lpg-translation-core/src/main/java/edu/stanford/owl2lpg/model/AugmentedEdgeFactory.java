package edu.stanford.owl2lpg.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import edu.stanford.owl2lpg.translator.ClassExpressionTranslator;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabel;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLRestriction;
import org.semanticweb.owlapi.util.OWLClassExpressionVisitorExAdapter;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.AXIOM_SUBJECT;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.CLASS_EXPRESSION;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.DATA_PROPERTY_EXPRESSION;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.DATA_RANGE;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.HAS_DOMAIN;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.HAS_RANGE;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.INDIVIDUAL;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.INVERSE_OF;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.IN_AXIOM_SIGNATURE;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.IN_ONTOLOGY_SIGNATURE;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.LITERAL;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.OBJECT_PROPERTY_EXPRESSION;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.RELATED_TO;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.SAME_INDIVIDUAL;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.SUB_ANNOTATION_PROPERTY_OF;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.SUB_CLASS_OF;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.SUB_DATA_PROPERTY_OF;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.SUB_OBJECT_PROPERTY_OF;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.TYPE;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AugmentedEdgeFactory {

  @Nonnull
  private final EdgeFactory edgeFactory;

  @Nonnull
  private final ClassExpressionTranslator classExprTranslator;

  @Nonnull
  private final AugmentedEdgeInclusionChecker augmentedEdgeInclusionChecker;

  @Inject
  public AugmentedEdgeFactory(@Nonnull EdgeFactory edgeFactory,
                              @Nonnull ClassExpressionTranslator classExprTranslator,
                              @Nonnull AugmentedEdgeInclusionChecker augmentedEdgeInclusionChecker) {
    this.edgeFactory = checkNotNull(edgeFactory);
    this.classExprTranslator = checkNotNull(classExprTranslator);
    this.augmentedEdgeInclusionChecker = checkNotNull(augmentedEdgeInclusionChecker);
  }

  @Nonnull
  public Optional<Edge> getInOntologySignatureEdge(@Nonnull Node entityNode,
                                                   @Nonnull Node ontoDocNode) {
    return (augmentedEdgeInclusionChecker.allows(IN_ONTOLOGY_SIGNATURE))
        ? Optional.of(getAugmentedEdge(entityNode, ontoDocNode, IN_ONTOLOGY_SIGNATURE))
        : Optional.empty();
  }

  @Nonnull
  public Optional<Edge> getInAxiomSignatureEdge(@Nonnull Node entityNode,
                                                @Nonnull Node axiomNode) {
    return (augmentedEdgeInclusionChecker.allows(IN_AXIOM_SIGNATURE))
        ? Optional.of(getAugmentedEdge(entityNode, axiomNode, IN_AXIOM_SIGNATURE))
        : Optional.empty();
  }

  @Nonnull
  public Optional<Edge> getAxiomSubjectEdge(@Nonnull Node axiomNode,
                                            @Nonnull Node subjectNode) {
    return (augmentedEdgeInclusionChecker.allows(AXIOM_SUBJECT))
        ? Optional.of(getAugmentedEdge(axiomNode, subjectNode, AXIOM_SUBJECT))
        : Optional.empty();
  }

  @Nonnull
  public Optional<List<Edge>> getAxiomSubjectEdges(@Nonnull Node axiomNode,
                                                   @Nonnull List<Node> subjectNodes) {
    var edges = subjectNodes
        .stream()
        .map(subjectNode -> getAxiomSubjectEdge(axiomNode, subjectNode))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(ImmutableList.toImmutableList());
    return Optional.of(edges);
  }

  @Nonnull
  public Optional<Edge> getSubClassOfEdge(@Nonnull Node subClassNode,
                                          @Nonnull Node superClassNode) {
    return (augmentedEdgeInclusionChecker.allows(SUB_CLASS_OF))
        ? Optional.of(getAugmentedEdge(subClassNode, superClassNode, SUB_CLASS_OF))
        : Optional.empty();
  }

  @Nonnull
  public Optional<List<Edge>> getSymmetricalSubClassOfEdges(@Nonnull List<Node> classNodes) {
    return (augmentedEdgeInclusionChecker.allows(SUB_CLASS_OF))
        ? Optional.of(getSymmetricalAugmentedEdges(classNodes, SUB_CLASS_OF))
        : Optional.empty();
  }

  @Nonnull
  public Optional<Edge> getSubObjectPropertyOfEdge(@Nonnull Node subPropertyNode,
                                                   @Nonnull Node superPropertyNode) {
    return (augmentedEdgeInclusionChecker.allows(SUB_OBJECT_PROPERTY_OF))
        ? Optional.of(getAugmentedEdge(subPropertyNode, superPropertyNode, SUB_OBJECT_PROPERTY_OF))
        : Optional.empty();
  }

  @Nonnull
  public Optional<List<Edge>> getSymmetricalSubObjectPropertyOfEdges(@Nonnull List<Node> objectPropertyNodes) {
    return (augmentedEdgeInclusionChecker.allows(SUB_OBJECT_PROPERTY_OF))
        ? Optional.of(getSymmetricalAugmentedEdges(objectPropertyNodes, SUB_OBJECT_PROPERTY_OF))
        : Optional.empty();
  }

  @Nonnull
  public Optional<Edge> getSubDataPropertyOfEdge(@Nonnull Node subPropertyNode,
                                                 @Nonnull Node superPropertyNode) {
    return (augmentedEdgeInclusionChecker.allows(SUB_DATA_PROPERTY_OF))
        ? Optional.of(getAugmentedEdge(subPropertyNode, superPropertyNode, SUB_DATA_PROPERTY_OF))
        : Optional.empty();
  }

  @Nonnull
  public Optional<List<Edge>> getSymmetricalSubDataPropertyOfEdges(@Nonnull List<Node> dataPropertyNodes) {
    return (augmentedEdgeInclusionChecker.allows(SUB_DATA_PROPERTY_OF))
        ? Optional.of(getSymmetricalAugmentedEdges(dataPropertyNodes, SUB_DATA_PROPERTY_OF))
        : Optional.empty();
  }

  @Nonnull
  public Optional<Edge> getSubAnnotationPropertyOfEdge(@Nonnull Node subPropertyNode,
                                                       @Nonnull Node superPropertyNode) {
    return (augmentedEdgeInclusionChecker.allows(SUB_ANNOTATION_PROPERTY_OF))
        ? Optional.of(getAugmentedEdge(subPropertyNode, superPropertyNode, SUB_ANNOTATION_PROPERTY_OF))
        : Optional.empty();
  }

  @Nonnull
  public Optional<Edge> getHasDomainEdge(@Nonnull Node propertyNode,
                                         @Nonnull Node domainNode) {
    return (augmentedEdgeInclusionChecker.allows(HAS_DOMAIN))
        ? Optional.of(getAugmentedEdge(propertyNode, domainNode, HAS_DOMAIN))
        : Optional.empty();
  }

  @Nonnull
  public Optional<Edge> getHasRangeEdge(@Nonnull Node propertyNode,
                                        @Nonnull Node rangeNode) {
    return (augmentedEdgeInclusionChecker.allows(HAS_RANGE))
        ? Optional.of(getAugmentedEdge(propertyNode, rangeNode, HAS_RANGE))
        : Optional.empty();
  }

  @Nonnull
  public Optional<Edge> getInverseOfEdge(@Nonnull Node node1,
                                         @Nonnull Node node2) {
    return (augmentedEdgeInclusionChecker.allows(INVERSE_OF))
        ? Optional.of(getAugmentedEdge(node1, node2, INVERSE_OF))
        : Optional.empty();
  }

  @Nonnull
  public Optional<List<Edge>> getSameIndividualEdges(@Nonnull List<Node> individualNodes) {
    return (augmentedEdgeInclusionChecker.allows(SAME_INDIVIDUAL))
        ? Optional.of(getSymmetricalAugmentedEdges(individualNodes, SAME_INDIVIDUAL))
        : Optional.empty();
  }

  @Nonnull
  public Optional<List<Edge>> getRelatedToEdges(@Nonnull Node subClassNode,
                                                @Nonnull OWLClassExpression superClassExpresion) {
    if (augmentedEdgeInclusionChecker.allows(RELATED_TO)) {
      /* Add extra augmented edges when the superclass is an expression */
      var edgeList = superClassExpresion.accept(new OWLClassExpressionVisitorExAdapter<List<Edge>>(ImmutableList.of()) {
        @Override
        public List<Edge> visit(OWLObjectIntersectionOf ce) {
          var translation = classExprTranslator.translate(ce);
          return translation.getDirectNodes()
              .stream()
              .map(ceNode -> getAugmentedEdge(subClassNode, ceNode, SUB_CLASS_OF))
              .collect(ImmutableList.toImmutableList());
        }

        @Override
        public List<Edge> visit(OWLObjectSomeValuesFrom ce) {
          return getNestedRelatedToAugmentedEdge(subClassNode, ce, OBJECT_PROPERTY_EXPRESSION, CLASS_EXPRESSION)
              .map(ImmutableList::of)
              .orElseGet(ImmutableList::of);
        }

        @Override
        public List<Edge> visit(OWLDataSomeValuesFrom ce) {
          return getNestedRelatedToAugmentedEdge(subClassNode, ce, DATA_PROPERTY_EXPRESSION, DATA_RANGE)
              .map(ImmutableList::of)
              .orElseGet(ImmutableList::of);
        }

        @Override
        public List<Edge> visit(OWLObjectHasValue ce) {
          return getNestedRelatedToAugmentedEdge(subClassNode, ce, OBJECT_PROPERTY_EXPRESSION, INDIVIDUAL)
              .map(ImmutableList::of)
              .orElseGet(ImmutableList::of);
        }

        @Override
        public List<Edge> visit(OWLDataHasValue ce) {
          return getNestedRelatedToAugmentedEdge(subClassNode, ce, DATA_PROPERTY_EXPRESSION, LITERAL)
              .map(ImmutableList::of)
              .orElseGet(ImmutableList::of);
        }

        @Override
        public List<Edge> visit(OWLDataMinCardinality ce) {
          return visitDataCardinalityRestriction(ce);
        }

        @Override
        public List<Edge> visit(OWLDataExactCardinality ce) {
          return visitDataCardinalityRestriction(ce);
        }

        @Override
        public List<Edge> visit(OWLDataMaxCardinality ce) {
          return visitDataCardinalityRestriction(ce);
        }

        private List<Edge> visitDataCardinalityRestriction(OWLDataCardinalityRestriction ce) {
          var cardinality = ce.getCardinality();
          return (cardinality > 0)
              ? getNestedRelatedToAugmentedEdge(subClassNode, ce, DATA_PROPERTY_EXPRESSION, DATA_RANGE)
              .map(ImmutableList::of)
              .orElseGet(ImmutableList::of)
              : ImmutableList.of();
        }

        @Override
        public List<Edge> visit(OWLObjectMinCardinality ce) {
          return visitObjectCardinalityRestriction(ce);
        }

        @Override
        public List<Edge> visit(OWLObjectExactCardinality ce) {
          return visitObjectCardinalityRestriction(ce);
        }

        @Override
        public List<Edge> visit(OWLObjectMaxCardinality ce) {
          return visitObjectCardinalityRestriction(ce);
        }

        private List<Edge> visitObjectCardinalityRestriction(OWLObjectCardinalityRestriction ce) {
          var cardinality = ce.getCardinality();
          return (cardinality > 0)
              ? getNestedRelatedToAugmentedEdge(subClassNode, ce, OBJECT_PROPERTY_EXPRESSION, CLASS_EXPRESSION)
              .map(ImmutableList::of)
              .orElseGet(ImmutableList::of)
              : ImmutableList.of();
        }

        public Optional<Edge> getNestedRelatedToAugmentedEdge(@Nonnull Node subjectNode,
                                                              @Nonnull OWLRestriction restriction,
                                                              @Nonnull EdgeLabel propertyEdgeLabel,
                                                              @Nonnull EdgeLabel fillerEdgeLabel) {
          var property = restriction.getProperty();
          if (property.isNamed()) {
            var entity = (OWLEntity) property;
            var translation = classExprTranslator.translate(restriction);
            var propertyNode = translation.findFirstDirectNodeFrom(propertyEdgeLabel);
            var fillerNode = translation.findFirstDirectNodeFrom(fillerEdgeLabel);
            if (propertyNode.isPresent() && fillerNode.isPresent()) {
              return Optional.of(getRelatedToEdge(subjectNode,
                  fillerNode.get(),
                  Properties.create(ImmutableMap.of(
                      PropertyFields.IRI, propertyNode.get().getProperty(PropertyFields.IRI),
                      PropertyFields.TYPE, entity.getEntityType().getName()))));
            }
          }
          return Optional.empty();
        }
      });
      return Optional.of(edgeList);
    } else {
      return Optional.empty();
    }

  }

  @Nonnull
  public Optional<Edge> getRelatedToEdge(@Nonnull Node subjectNode,
                                         @Nonnull Node fillerNode,
                                         @Nonnull OWLPropertyExpression propertyExpr) {
    return (propertyExpr.isNamed() && augmentedEdgeInclusionChecker.allows(RELATED_TO))
        ? Optional.of(getRelatedToEdge(subjectNode, fillerNode, (OWLEntity) propertyExpr))
        : Optional.empty();
  }

  @Nonnull
  public Optional<Edge> getTypeEdge(Node subjectNode, Node fillerNode) {
    return (augmentedEdgeInclusionChecker.allows(TYPE))
        ? Optional.of(getAugmentedEdge(subjectNode, fillerNode, TYPE))
        : Optional.empty();
  }

  @Nonnull
  private Edge getRelatedToEdge(Node subjectNode, Node fillerNode, OWLEntity entity) {
    return getRelatedToEdge(subjectNode,
        fillerNode,
        Properties.create(ImmutableMap.of(
            PropertyFields.IRI, String.valueOf(entity.getIRI()),
            PropertyFields.TYPE, entity.getEntityType().getName())));
  }

  @Nonnull
  private Edge getRelatedToEdge(Node fromNode, Node toNode, Properties properties) {
    return getAugmentedEdge(fromNode, toNode, RELATED_TO, properties);
  }

  @Nonnull
  private List<Edge> getSymmetricalAugmentedEdges(List<Node> nodes, EdgeLabel edgeLabel) {
    var edgeList = Lists.<Edge>newArrayList();
    for (int i = 0; i < nodes.size(); i++) {
      for (int j = 0; j < nodes.size(); j++) {
        if (i != j) {
          var edge = getAugmentedEdge(nodes.get(i), nodes.get(j), edgeLabel);
          edgeList.add(edge);
        }
      }
    }
    return ImmutableList.copyOf(edgeList);
  }

  @Nonnull
  private Edge getAugmentedEdge(Node fromNode, Node toNode, EdgeLabel edgeLabel) {
    return getAugmentedEdge(fromNode, toNode, edgeLabel, Properties.empty());
  }

  @Nonnull
  private Edge getAugmentedEdge(Node fromNode, Node toNode, EdgeLabel edgeLabel, Properties properties) {
    return edgeFactory.createEdge(fromNode, toNode, edgeLabel, properties);
  }
}
