package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;
import edu.stanford.owl2lpg.model.*;
import edu.stanford.owl2lpg.translator.*;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabel;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLClassExpressionVisitorAdapter;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.ANNOTATION_PROPERTY;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.CLASS;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.CLASS_EXPRESSION;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.DATATYPE;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.DATA_RANGE;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.ENTITY;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.INDIVIDUAL;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.LITERAL;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.OBJECT_PROPERTY_EXPRESSION;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.SAME_INDIVIDUAL;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.SUB_ANNOTATION_PROPERTY_OF;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.SUB_DATA_PROPERTY_OF;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.SUB_OBJECT_PROPERTY_OF;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.*;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.SUB_CLASS_OF;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.*;

/**
 * A visitor that contains the implementation to translate the OWL 2 axioms.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@TranslationSessionScope
public class AxiomVisitor implements OWLAxiomVisitorEx<Translation> {

    @Nonnull
    private final NodeFactory nodeFactory;

    @Nonnull
    private final EdgeFactory edgeFactory;

    @Nonnull
    private final EntityTranslator entityTranslator;

    @Nonnull
    private final ClassExpressionTranslator classExprTranslator;

    @Nonnull
    private final PropertyExpressionTranslator propertyExprTranslator;

    @Nonnull
    private final DataRangeTranslator dataRangeTranslator;

    @Nonnull
    private final LiteralTranslator literalTranslator;

    @Nonnull
    private final IndividualTranslator individualTranslator;

    @Nonnull
    private final AnnotationObjectTranslator annotationTranslator;

    @Nonnull
    private final AnnotationSubjectTranslator annotationSubjectTranslator;

    @Nonnull
    private final AnnotationValueTranslator annotationValueTranslator;

    @Inject
    public AxiomVisitor(@Nonnull NodeFactory nodeFactory,
                        @Nonnull EdgeFactory edgeFactory,
                        @Nonnull EntityTranslator entityTranslator,
                        @Nonnull ClassExpressionTranslator classExprTranslator,
                        @Nonnull PropertyExpressionTranslator propertyExprTranslator,
                        @Nonnull DataRangeTranslator dataRangeTranslator,
                        @Nonnull LiteralTranslator literalTranslator,
                        @Nonnull IndividualTranslator individualTranslator,
                        @Nonnull AnnotationObjectTranslator annotationTranslator,
                        @Nonnull AnnotationSubjectTranslator annotationSubjectTranslator,
                        @Nonnull AnnotationValueTranslator annotationValueTranslator) {
        this.nodeFactory = checkNotNull(nodeFactory);
        this.edgeFactory = checkNotNull(edgeFactory);
        this.entityTranslator = checkNotNull(entityTranslator);
        this.classExprTranslator = checkNotNull(classExprTranslator);
        this.propertyExprTranslator = checkNotNull(propertyExprTranslator);
        this.dataRangeTranslator = checkNotNull(dataRangeTranslator);
        this.literalTranslator = checkNotNull(literalTranslator);
        this.individualTranslator = checkNotNull(individualTranslator);
        this.annotationTranslator = checkNotNull(annotationTranslator);
        this.annotationSubjectTranslator = checkNotNull(annotationSubjectTranslator);
        this.annotationValueTranslator = checkNotNull(annotationValueTranslator);
    }

    @Nonnull
    @Override
    public Translation visit(@Nonnull OWLDeclarationAxiom axiom) {
        var axiomNode = createAxiomNode(axiom, DECLARATION);
        var translations = newTranslationBuilder();
        var edges = newEdgesBuilder();
        var entityNode = addEntityTranslationAndEdge(axiomNode,
            ENTITY, axiom.getEntity(),
            translations, edges);
        addAxiomSubjectRelation(axiomNode, entityNode, edges);
        addAxiomAnnotations(axiomNode, axiom, translations, edges);
        return buildTranslation(axiom, axiomNode, translations, edges);
    }

    @Nonnull
    @Override
    public Translation visit(@Nonnull OWLDatatypeDefinitionAxiom axiom) {
        var axiomNode = createAxiomNode(axiom, DATATYPE_DEFINITION);
        var translations = newTranslationBuilder();
        var edges = newEdgesBuilder();
        var entityNode = addEntityTranslationAndEdge(axiomNode,
            DATATYPE, axiom.getDatatype(),
            translations, edges);
        addAxiomSubjectRelation(axiomNode, entityNode, edges);
        addDataRangeTranslationAndEdge(axiomNode,
            DATA_RANGE, axiom.getDataRange(),
            translations, edges);
        addAxiomAnnotations(axiomNode, axiom, translations, edges);
        return buildTranslation(axiom, axiomNode, translations, edges);
    }

    @Nonnull
    @Override
    public Translation visit(@Nonnull OWLSubClassOfAxiom axiom) {
        var axiomNode = createAxiomNode(axiom, SUB_CLASS_OF);
        var translations = newTranslationBuilder();
        var edges = newEdgesBuilder();
        var subClassNode = addClassExprTranslationAndEdge(axiomNode,
            SUB_CLASS_EXPRESSION, axiom.getSubClass(),
            translations, edges);
        var superClassExpresion = axiom.getSuperClass();
        var superClassNode = addClassExprTranslationAndEdge(axiomNode,
            SUPER_CLASS_EXPRESSION, superClassExpresion,
            translations, edges);
        addAugmentedEdge(subClassNode, superClassNode, EdgeLabel.SUB_CLASS_OF, edges);
        addAxiomSubjectRelation(axiomNode, subClassNode, edges);
        addSubClassNodeRelatedToEdges(subClassNode, superClassExpresion, edges);
        addAxiomAnnotations(axiomNode, axiom, translations, edges);
        return buildTranslation(axiom, axiomNode, translations, edges);
    }

    private void addAxiomSubjectRelation(Node axiomNode, Node subjectNode, Builder<Edge> edges) {
        addAugmentedEdge(axiomNode, subjectNode, AXIOM_SUBJECT, edges);
    }

    private void addAxiomSubjectRelations(Node axiomNode, List<Node> subjectNodes, Builder<Edge> edges) {
        subjectNodes.forEach(subjectNode -> addAxiomSubjectRelation(axiomNode, subjectNode, edges));
    }

    private void addSubClassNodeRelatedToEdges(@Nonnull Node subClassNode,
                                               @Nonnull OWLClassExpression superClassExpresion,
                                               @Nonnull Builder<Edge> edges) {
        /* Add extra augmented edges when the superclass is an expression */
        superClassExpresion.accept(new OWLClassExpressionVisitorAdapter() {
            @Override
            public void visit(OWLObjectIntersectionOf ce) {
                var translation = classExprTranslator.translate(ce);
                translation.getDirectNodes()
                    .forEach(ceNode -> addAugmentedEdge(
                        subClassNode, ceNode, EdgeLabel.SUB_CLASS_OF, edges));
            }

            @Override
            public void visit(OWLObjectSomeValuesFrom ce) {
                addEdge(subClassNode, ce,
                    OBJECT_PROPERTY_EXPRESSION,
                    CLASS_EXPRESSION,
                    edges);
            }

            @Override
            public void visit(OWLDataSomeValuesFrom ce) {
                addEdge(subClassNode, ce,
                    DATA_PROPERTY_EXPRESSION,
                    DATA_RANGE,
                    edges);
            }

            @Override
            public void visit(OWLObjectHasValue ce) {
                addEdge(subClassNode, ce,
                    OBJECT_PROPERTY_EXPRESSION,
                    INDIVIDUAL,
                    edges);
            }

            @Override
            public void visit(OWLDataHasValue ce) {
                addEdge(subClassNode, ce,
                    DATA_PROPERTY_EXPRESSION,
                    LITERAL,
                    edges);
            }

            @Override
            public void visit(OWLDataMinCardinality ce) {
                visitDataCardinalityRestriction(ce);
            }

            @Override
            public void visit(OWLDataExactCardinality ce) {
                visitDataCardinalityRestriction(ce);
            }

            @Override
            public void visit(OWLDataMaxCardinality ce) {
                visitDataCardinalityRestriction(ce);
            }

            private void visitDataCardinalityRestriction(OWLDataCardinalityRestriction ce) {
                var cardinality = ce.getCardinality();
                if (cardinality > 0) {
                    addEdge(subClassNode, ce,
                        DATA_PROPERTY_EXPRESSION,
                        DATA_RANGE,
                        edges);
                }
            }

            @Override
            public void visit(OWLObjectMinCardinality ce) {
                visitObjectCardinalityRestriction(ce);
            }

            @Override
            public void visit(OWLObjectExactCardinality ce) {
                visitObjectCardinalityRestriction(ce);
            }

            @Override
            public void visit(OWLObjectMaxCardinality ce) {
                visitObjectCardinalityRestriction(ce);
            }

            private void visitObjectCardinalityRestriction(OWLObjectCardinalityRestriction ce) {
                var cardinality = ce.getCardinality();
                if (cardinality > 0) {
                    addEdge(subClassNode, ce,
                        OBJECT_PROPERTY_EXPRESSION,
                        CLASS_EXPRESSION,
                        edges);
                }
            }

            private void addEdge(@Nonnull Node subjectNode,
                                 @Nonnull OWLRestriction restriction,
                                 @Nonnull EdgeLabel propertyEdgeLabel,
                                 @Nonnull EdgeLabel fillerEdgeLabel,
                                 @Nonnull Builder<Edge> edges) {
                var property = restriction.getProperty();
                if (!property.isNamed()) {
                    return;
                }
                var entity = (OWLEntity) property;
                var translation = classExprTranslator.translate(restriction);
                var propertyNode = translation.findFirstDirectNodeFrom(propertyEdgeLabel);
                var fillerNode = translation.findFirstDirectNodeFrom(fillerEdgeLabel);
                if (propertyNode.isEmpty()) {
                    return;
                } else if (fillerNode.isEmpty()) {
                    return;
                }
                addRelatedToEdge(subjectNode,
                    fillerNode.get(),
                    Properties.create(ImmutableMap.of(
                        PropertyFields.IRI, propertyNode.get().getProperty(PropertyFields.IRI),
                        PropertyFields.TYPE, entity.getEntityType().getName())),
                    edges);
            }
        });
    }

    @Nonnull
    @Override
    public Translation visit(@Nonnull OWLNegativeObjectPropertyAssertionAxiom axiom) {
        var axiomNode = createAxiomNode(axiom, NEGATIVE_OBJECT_PROPERTY_ASSERTION);
        var translations = newTranslationBuilder();
        var edges = newEdgesBuilder();
        var subjectNode = addIndividualTranslationAndEdge(axiomNode,
            SOURCE_INDIVIDUAL, axiom.getSubject(),
            translations, edges);
        addPropertyExprTranslationAndEdge(axiomNode,
            OBJECT_PROPERTY_EXPRESSION, axiom.getProperty(),
            translations, edges);
        addIndividualTranslationAndEdge(axiomNode,
            TARGET_INDIVIDUAL, axiom.getObject(),
            translations, edges);
        addAxiomSubjectRelation(axiomNode, subjectNode, edges);
        addAxiomAnnotations(axiomNode, axiom, translations, edges);
        return buildTranslation(axiom, axiomNode, translations, edges);
    }

    @Nonnull
    @Override
    public Translation visit(@Nonnull OWLFunctionalObjectPropertyAxiom axiom) {
        return translateObjectPropertyCharacteristic(
            axiom, NodeLabels.FUNCTIONAL_OBJECT_PROPERTY);
    }

    @Nonnull
    @Override
    public Translation visit(@Nonnull OWLInverseFunctionalObjectPropertyAxiom axiom) {
        return translateObjectPropertyCharacteristic(
            axiom, NodeLabels.INVERSE_FUNCTIONAL_OBJECT_PROPERTY);
    }

    @Nonnull
    @Override
    public Translation visit(@Nonnull OWLSymmetricObjectPropertyAxiom axiom) {
        return translateObjectPropertyCharacteristic(
            axiom, NodeLabels.SYMMETRIC_OBJECT_PROPERTY);
    }

    @Nonnull
    @Override
    public Translation visit(@Nonnull OWLAsymmetricObjectPropertyAxiom axiom) {
        return translateObjectPropertyCharacteristic(
            axiom, NodeLabels.ASYMMETRIC_OBJECT_PROPERTY);
    }

    @Nonnull
    @Override
    public Translation visit(@Nonnull OWLTransitiveObjectPropertyAxiom axiom) {
        return translateObjectPropertyCharacteristic(
            axiom, NodeLabels.TRANSITIVE_OBJECT_PROPERTY);
    }

    @Nonnull
    @Override
    public Translation visit(@Nonnull OWLReflexiveObjectPropertyAxiom axiom) {
        return translateObjectPropertyCharacteristic(
            axiom, NodeLabels.REFLEXIVE_OBJECT_PROPERTY);
    }

    @Nonnull
    @Override
    public Translation visit(@Nonnull OWLIrreflexiveObjectPropertyAxiom axiom) {
        return translateObjectPropertyCharacteristic(
            axiom, NodeLabels.IRREFLEXIVE_OBJECT_PROPERTY);
    }

    private Translation
    translateObjectPropertyCharacteristic(@Nonnull OWLObjectPropertyCharacteristicAxiom axiom,
                                          @Nonnull NodeLabels nodeLabels) {
        var axiomNode = createAxiomNode(axiom, nodeLabels);
        var translations = newTranslationBuilder();
        var edges = newEdgesBuilder();
        var propertyNode = addPropertyExprTranslationAndEdge(axiomNode,
            OBJECT_PROPERTY_EXPRESSION, axiom.getProperty(),
            translations, edges);
        addAxiomSubjectRelation(axiomNode, propertyNode, edges);
        addAxiomAnnotations(axiomNode, axiom, translations, edges);
        return buildTranslation(axiom, axiomNode, translations, edges);
    }

    @Nonnull
    @Override
    public Translation visit(@Nonnull OWLDisjointClassesAxiom axiom) {
        var axiomNode = createAxiomNode(axiom, DISJOINT_CLASSES);
        var translations = newTranslationBuilder();
        var edges = newEdgesBuilder();
        var classExprNodes = addClassExprTranslationsAndEdges(axiomNode,
            CLASS_EXPRESSION, axiom.getClassExpressions(),
            translations, edges);
        addAxiomSubjectRelations(axiomNode, classExprNodes, edges);
        addAxiomAnnotations(axiomNode, axiom, translations, edges);
        return buildTranslation(axiom, axiomNode, translations, edges);
    }

    @Nonnull
    @Override
    public Translation visit(@Nonnull OWLDataPropertyDomainAxiom axiom) {
        return translatePropertyDomain(axiom,
            DATA_PROPERTY_DOMAIN,
            DATA_PROPERTY_EXPRESSION);
    }

    @Nonnull
    @Override
    public Translation visit(@Nonnull OWLObjectPropertyDomainAxiom axiom) {
        return translatePropertyDomain(axiom,
            OBJECT_PROPERTY_DOMAIN,
            OBJECT_PROPERTY_EXPRESSION);
    }

    @SuppressWarnings("rawtypes")
    private Translation translatePropertyDomain(@Nonnull OWLPropertyDomainAxiom axiom,
                                                @Nonnull NodeLabels nodeLabels,
                                                @Nonnull EdgeLabel propertyEdgeLabel) {
        var axiomNode = createAxiomNode(axiom, nodeLabels);
        var translations = newTranslationBuilder();
        var edges = newEdgesBuilder();
        var propertyExprNode = addPropertyExprTranslationAndEdge(axiomNode,
            propertyEdgeLabel, axiom.getProperty(),
            translations, edges);
        var domainNode = addClassExprTranslationAndEdge(axiomNode,
            DOMAIN, axiom.getDomain(),
            translations, edges);
        addAugmentedEdge(propertyExprNode, domainNode, DOMAIN, edges);
        addAxiomSubjectRelation(axiomNode, propertyExprNode, edges);
        addAxiomAnnotations(axiomNode, axiom, translations, edges);
        return buildTranslation(axiom, axiomNode, translations, edges);
    }

    @Nonnull
    @Override
    public Translation visit(@Nonnull OWLEquivalentObjectPropertiesAxiom axiom) {
        var axiomNode = createAxiomNode(axiom, EQUIVALENT_OBJECT_PROPERTIES);
        var translations = newTranslationBuilder();
        var edges = newEdgesBuilder();
        var propertyExprNodes = addPropertyExprTranslationsAndEdges(axiomNode,
            OBJECT_PROPERTY_EXPRESSION, axiom.getProperties(), translations, edges);
        addAxiomSubjectRelations(axiomNode, propertyExprNodes, edges);
        addReciprocalEdges(propertyExprNodes, SUB_OBJECT_PROPERTY_EXPRESSION, edges);
        addAxiomAnnotations(axiomNode, axiom, translations, edges);
        return buildTranslation(axiom, axiomNode, translations, edges);
    }

    @Nonnull
    @Override
    public Translation visit(@Nonnull OWLNegativeDataPropertyAssertionAxiom axiom) {
        var axiomNode = createAxiomNode(axiom, NEGATIVE_DATA_PROPERTY_ASSERTION);
        var translations = newTranslationBuilder();
        var edges = newEdgesBuilder();
        var subjectNode = addIndividualTranslationAndEdge(axiomNode,
            SOURCE_INDIVIDUAL, axiom.getSubject(),
            translations, edges);
        addPropertyExprTranslationAndEdge(axiomNode,
            DATA_PROPERTY_EXPRESSION, axiom.getProperty(),
            translations, edges);
        addLiteralTranslationAndEdge(axiomNode,
            axiom.getObject(),
            translations, edges);
        addAxiomSubjectRelation(axiomNode, subjectNode, edges);
        addAxiomAnnotations(axiomNode, axiom, translations, edges);
        return buildTranslation(axiom, axiomNode, translations, edges);
    }

    @Nonnull
    @Override
    public Translation visit(@Nonnull OWLDifferentIndividualsAxiom axiom) {
        var axiomNode = createAxiomNode(axiom, DIFFERENT_INDIVIDUALS);
        var translations = newTranslationBuilder();
        var edges = newEdgesBuilder();
        var individualNodes = addIndividualTranslationsAndEdges(axiomNode,
            INDIVIDUAL, axiom.getIndividuals(),
            translations, edges);
        addAxiomSubjectRelations(axiomNode, individualNodes, edges);
        addAxiomAnnotations(axiomNode, axiom, translations, edges);
        return buildTranslation(axiom, axiomNode, translations, edges);
    }

    @Nonnull
    @Override
    public Translation visit(@Nonnull OWLDisjointDataPropertiesAxiom axiom) {
        var axiomNode = createAxiomNode(axiom, DISJOINT_DATA_PROPERTIES);
        var translations = newTranslationBuilder();
        var edges = newEdgesBuilder();
        var propertyExprNodes = addPropertyExprTranslationsAndEdges(axiomNode,
            DATA_PROPERTY_EXPRESSION, axiom.getProperties(),
            translations, edges);
        addAxiomSubjectRelations(axiomNode, propertyExprNodes, edges);
        addAxiomAnnotations(axiomNode, axiom, translations, edges);
        return buildTranslation(axiom, axiomNode, translations, edges);
    }

    @Nonnull
    @Override
    public Translation visit(@Nonnull OWLDisjointObjectPropertiesAxiom axiom) {
        var axiomNode = createAxiomNode(axiom, DISJOINT_OBJECT_PROPERTIES);
        var translations = newTranslationBuilder();
        var edges = newEdgesBuilder();
        var propertyExprNodes = addPropertyExprTranslationsAndEdges(axiomNode,
            OBJECT_PROPERTY_EXPRESSION, axiom.getProperties(),
            translations, edges);
        addAxiomSubjectRelations(axiomNode, propertyExprNodes, edges);
        addAxiomAnnotations(axiomNode, axiom, translations, edges);
        return buildTranslation(axiom, axiomNode, translations, edges);
    }

    @Nonnull
    @Override
    public Translation visit(@Nonnull OWLObjectPropertyRangeAxiom axiom) {
        var axiomNode = createAxiomNode(axiom, OBJECT_PROPERTY_RANGE);
        var translations = newTranslationBuilder();
        var edges = newEdgesBuilder();
        var propertyExprNode = addPropertyExprTranslationAndEdge(axiomNode,
            OBJECT_PROPERTY_EXPRESSION, axiom.getProperty(),
            translations, edges);
        var rangeNode = addClassExprTranslationAndEdge(axiomNode, RANGE, axiom.getRange(),
            translations, edges);
        addAugmentedEdge(propertyExprNode, rangeNode, RANGE, edges);
        addAxiomSubjectRelation(axiomNode, propertyExprNode, edges);
        addAxiomAnnotations(axiomNode, axiom, translations, edges);
        return buildTranslation(axiom, axiomNode, translations, edges);
    }

    @Nonnull
    @Override
    public Translation visit(@Nonnull OWLObjectPropertyAssertionAxiom axiom) {
        var axiomNode = createAxiomNode(axiom, OBJECT_PROPERTY_ASSERTION);
        var translations = newTranslationBuilder();
        var edges = newEdgesBuilder();
        var individualSubjectNode = addIndividualTranslationAndEdge(axiomNode,
            SOURCE_INDIVIDUAL, axiom.getSubject(),
            translations, edges);
        addPropertyExprTranslationAndEdge(axiomNode,
            OBJECT_PROPERTY_EXPRESSION, axiom.getProperty(),
            translations, edges);
        var individualObjectNode = addIndividualTranslationAndEdge(axiomNode,
            TARGET_INDIVIDUAL, axiom.getObject(),
            translations, edges);
        addRelatedToEdge(individualSubjectNode, individualObjectNode, axiom.getProperty(), edges);
        addAxiomSubjectRelation(axiomNode, individualSubjectNode, edges);
        addAxiomAnnotations(axiomNode, axiom, translations, edges);
        return buildTranslation(axiom, axiomNode, translations, edges);
    }

    @Nonnull
    @Override
    public Translation visit(@Nonnull OWLSubObjectPropertyOfAxiom axiom) {
        var axiomNode = createAxiomNode(axiom, NodeLabels.SUB_OBJECT_PROPERTY_OF);
        var translations = newTranslationBuilder();
        var edges = newEdgesBuilder();
        var subPropertyNode = addPropertyExprTranslationAndEdge(axiomNode,
            SUB_OBJECT_PROPERTY_EXPRESSION, axiom.getSubProperty(),
            translations, edges);
        var superPropertyNode = addPropertyExprTranslationAndEdge(axiomNode,
            SUPER_OBJECT_PROPERTY_EXPRESSION, axiom.getSuperProperty(),
            translations,
            edges);
        addAugmentedEdge(subPropertyNode, superPropertyNode, SUB_OBJECT_PROPERTY_OF, edges);
        addAugmentedEdge(subPropertyNode, axiomNode, SUB_OBJECT_PROPERTY_OF, edges);
        addAxiomSubjectRelation(axiomNode, subPropertyNode, edges);
        addAxiomAnnotations(axiomNode, axiom, translations, edges);
        return buildTranslation(axiom, axiomNode, translations, edges);
    }

    @Nonnull
    @Override
    public Translation visit(@Nonnull OWLDisjointUnionAxiom axiom) {
        var axiomNode = createAxiomNode(axiom, DISJOINT_UNION);
        var translations = newTranslationBuilder();
        var edges = newEdgesBuilder();
        addEntityTranslationAndEdge(axiomNode,
            CLASS, axiom.getOWLClass(),
            translations, edges);
        var classExprNodes = addClassExprTranslationsAndEdges(axiomNode,
            DISJOINT_CLASS_EXPRESSION, axiom.getClassExpressions(),
            translations, edges);
        addAxiomSubjectRelations(axiomNode, classExprNodes, edges);
        addAxiomAnnotations(axiomNode, axiom, translations, edges);
        return buildTranslation(axiom, axiomNode, translations, edges);
    }

    @Nonnull
    @Override
    public Translation visit(@Nonnull OWLDataPropertyRangeAxiom axiom) {
        var axiomNode = createAxiomNode(axiom, DATA_PROPERTY_RANGE);
        var translations = newTranslationBuilder();
        var edges = newEdgesBuilder();
        var propertyExprNode = addPropertyExprTranslationAndEdge(axiomNode,
            DATA_PROPERTY_EXPRESSION, axiom.getProperty(),
            translations, edges);
        var rangeNode = addDataRangeTranslationAndEdge(axiomNode,
            RANGE, axiom.getRange(),
            translations, edges);
        addAugmentedEdge(propertyExprNode, rangeNode, RANGE, edges);
        addAxiomSubjectRelation(axiomNode, propertyExprNode, edges);
        addAxiomAnnotations(axiomNode, axiom, translations, edges);
        return buildTranslation(axiom, axiomNode, translations, edges);
    }

    @Nonnull
    @Override
    public Translation visit(@Nonnull OWLFunctionalDataPropertyAxiom axiom) {
        var axiomNode = createAxiomNode(axiom, FUNCTIONAL_DATA_PROPERTY);
        var translations = newTranslationBuilder();
        var edges = newEdgesBuilder();
        var propertyNode = addPropertyExprTranslationAndEdge(axiomNode,
            DATA_PROPERTY_EXPRESSION, axiom.getProperty(),
            translations, edges);
        addAxiomSubjectRelation(axiomNode, propertyNode, edges);
        addAxiomAnnotations(axiomNode, axiom, translations, edges);
        return buildTranslation(axiom, axiomNode, translations, edges);
    }

    @Nonnull
    @Override
    public Translation visit(@Nonnull OWLEquivalentDataPropertiesAxiom axiom) {
        var axiomNode = createAxiomNode(axiom, EQUIVALENT_DATA_PROPERTIES);
        var translations = newTranslationBuilder();
        var edges = newEdgesBuilder();
        var propertyExprList = addPropertyExprTranslationsAndEdges(axiomNode,
            DATA_PROPERTY_EXPRESSION, axiom.getProperties(),
            translations, edges);
        addAxiomSubjectRelations(axiomNode, propertyExprList, edges);
        addReciprocalEdges(propertyExprList, SUB_DATA_PROPERTY_OF, edges);
        addAxiomAnnotations(axiomNode, axiom, translations, edges);
        return buildTranslation(axiom, axiomNode, translations, edges);
    }

    @Nonnull
    @Override
    public Translation visit(@Nonnull OWLClassAssertionAxiom axiom) {
        var axiomNode = createAxiomNode(axiom, CLASS_ASSERTION);
        var translations = newTranslationBuilder();
        var edges = newEdgesBuilder();
        var individualNode = addIndividualTranslationAndEdge(axiomNode,
            INDIVIDUAL, axiom.getIndividual(),
            translations, edges);
        var classExprNode = addClassExprTranslationAndEdge(axiomNode,
            CLASS_EXPRESSION, axiom.getClassExpression(),
            translations, edges);
        addAugmentedEdge(individualNode, classExprNode, TYPE, edges);
        addAxiomSubjectRelation(axiomNode, individualNode, edges);
        addAxiomAnnotations(axiomNode, axiom, translations, edges);
        return buildTranslation(axiom, axiomNode, translations, edges);
    }

    @Nonnull
    @Override
    public Translation visit(@Nonnull OWLEquivalentClassesAxiom axiom) {
        var axiomNode = createAxiomNode(axiom, EQUIVALENT_CLASSES);
        var translations = newTranslationBuilder();
        var edges = newEdgesBuilder();
        var classList = addClassExprTranslationsAndEdges(axiomNode,
            CLASS_EXPRESSION, axiom.getClassExpressions(),
            translations, edges);
        addAxiomSubjectRelations(axiomNode, classList, edges);
        addReciprocalEdges(classList, EdgeLabel.SUB_CLASS_OF, edges);
        addAxiomAnnotations(axiomNode, axiom, translations, edges);
        return buildTranslation(axiom, axiomNode, translations, edges);
    }

    @Nonnull
    @Override
    public Translation visit(@Nonnull OWLDataPropertyAssertionAxiom axiom) {
        var axiomNode = createAxiomNode(axiom, DATA_PROPERTY_ASSERTION);
        var translations = newTranslationBuilder();
        var edges = newEdgesBuilder();
        var individualNode = addIndividualTranslationAndEdge(axiomNode,
            SOURCE_INDIVIDUAL, axiom.getSubject(),
            translations, edges);
        addPropertyExprTranslationAndEdge(axiomNode,
            DATA_PROPERTY_EXPRESSION, axiom.getProperty(),
            translations, edges);
        var literalNode = addLiteralTranslationAndEdge(axiomNode,
            axiom.getObject(), translations, edges);
        addRelatedToEdge(individualNode, literalNode, axiom.getProperty(), edges);
        addAxiomSubjectRelation(axiomNode, individualNode, edges);
        addAxiomAnnotations(axiomNode, axiom, translations, edges);
        return buildTranslation(axiom, axiomNode, translations, edges);
    }

    @Nonnull
    @Override
    public Translation visit(@Nonnull OWLSubDataPropertyOfAxiom axiom) {
        var axiomNode = createAxiomNode(axiom, NodeLabels.SUB_DATA_PROPERTY_OF);
        var translations = newTranslationBuilder();
        var edges = newEdgesBuilder();
        var subPropertyNode = addPropertyExprTranslationAndEdge(axiomNode,
            SUB_DATA_PROPERTY_EXPRESSION, axiom.getSubProperty(),
            translations, edges);
        var superPropertyNode = addPropertyExprTranslationAndEdge(axiomNode,
            SUPER_DATA_PROPERTY_EXPRESSION, axiom.getSuperProperty(),
            translations, edges);
        addAugmentedEdge(subPropertyNode, superPropertyNode, SUB_DATA_PROPERTY_OF, edges);
        addAxiomSubjectRelation(axiomNode, subPropertyNode, edges);
        addAxiomAnnotations(axiomNode, axiom, translations, edges);
        return buildTranslation(axiom, axiomNode, translations, edges);
    }

    @Nonnull
    @Override
    public Translation visit(@Nonnull OWLSameIndividualAxiom axiom) {
        var axiomNode = createAxiomNode(axiom, NodeLabels.SAME_INDIVIDUAL);
        var translations = newTranslationBuilder();
        var edges = newEdgesBuilder();
        var individualList = addIndividualTranslationsAndEdges(axiomNode,
            INDIVIDUAL, axiom.getIndividuals(), translations, edges);
        addAxiomSubjectRelations(axiomNode, individualList, edges);
        addReciprocalEdges(individualList, SAME_INDIVIDUAL, edges);
        addAxiomAnnotations(axiomNode, axiom, translations, edges);
        return buildTranslation(axiom, axiomNode, translations, edges);
    }

    @Nonnull
    @Override
    public Translation visit(@Nonnull OWLSubPropertyChainOfAxiom axiom) {
        var axiomNode = createAxiomNode(axiom, NodeLabels.SUB_OBJECT_PROPERTY_OF);
        var translations = newTranslationBuilder();
        var edges = newEdgesBuilder();
        var firstChainNode = addPropertyChainTranslationAndEdge(axiom.getPropertyChain(),
            axiomNode, translations, edges);
        addPropertyExprTranslationAndEdge(axiomNode,
            SUPER_OBJECT_PROPERTY_EXPRESSION, axiom.getSuperProperty(),
            translations, edges);
        addAxiomSubjectRelation(axiomNode, firstChainNode, edges);
        addAxiomAnnotations(axiomNode, axiom, translations, edges);
        return buildTranslation(axiom, axiomNode, translations, edges);
    }

    private Node addPropertyChainTranslationAndEdge(@Nonnull List<OWLObjectPropertyExpression> propertyChain,
                                                    @Nonnull Node axiomNode,
                                                    @Nonnull Builder<Translation> translations,
                                                    @Nonnull Builder<Edge> edges) {
        var firstChainNode = addPropertyExprTranslationAndEdge(axiomNode,
            EdgeLabel.SUB_OBJECT_PROPERTY_EXPRESSION, propertyChain.get(0),
            translations, edges);
        if (propertyChain.size() > 1) {
            addPropertyChainRecursively(propertyChain.subList(1, propertyChain.size()),
                firstChainNode, translations, edges);
        }
        return firstChainNode;
    }

    private void addPropertyChainRecursively(@Nonnull List<OWLObjectPropertyExpression> propertyChain,
                                             @Nonnull Node mainNode,
                                             @Nonnull Builder<Translation> translations,
                                             @Nonnull Builder<Edge> edges) {
        if (!propertyChain.isEmpty()) {
            var nextMainNode = addPropertyExprTranslationAndEdge(mainNode,
                NEXT, propertyChain.get(0),
                translations, edges);
            addPropertyChainRecursively(propertyChain.subList(1, propertyChain.size()),
                nextMainNode, translations, edges);
        }
    }

    @Nonnull
    @Override
    public Translation visit(@Nonnull OWLInverseObjectPropertiesAxiom axiom) {
        var axiomNode = createAxiomNode(axiom, INVERSE_OBJECT_PROPERTIES);
        var translations = newTranslationBuilder();
        var edges = newEdgesBuilder();
        var ope1Node = addPropertyExprTranslationAndEdge(axiomNode,
            OBJECT_PROPERTY_EXPRESSION, axiom.getFirstProperty(),
            translations, edges);
        var ope2Node = addPropertyExprTranslationAndEdge(axiomNode,
            OBJECT_PROPERTY_EXPRESSION, axiom.getSecondProperty(),
            translations, edges);
        addAugmentedEdge(ope1Node, ope2Node, INVERSE_OF, edges);
        addAugmentedEdge(ope2Node, ope1Node, INVERSE_OF, edges);
        addAxiomSubjectRelation(axiomNode, ope1Node, edges);
        addAxiomAnnotations(axiomNode, axiom, translations, edges);
        return buildTranslation(axiom, axiomNode, translations, edges);
    }

    @Nonnull
    @Override
    public Translation visit(@Nonnull OWLHasKeyAxiom axiom) {
        var axiomNode = createAxiomNode(axiom, HAS_KEY);
        var translations = newTranslationBuilder();
        var edges = newEdgesBuilder();
        var classExprNode = addClassExprTranslationAndEdge(axiomNode,
            CLASS_EXPRESSION, axiom.getClassExpression(),
            translations, edges);
        addPropertyExprTranslationsAndEdges(axiomNode,
            OBJECT_PROPERTY_EXPRESSION, axiom.getObjectPropertyExpressions(),
            translations, edges);
        addPropertyExprTranslationsAndEdges(axiomNode,
            DATA_PROPERTY_EXPRESSION, axiom.getDataPropertyExpressions(),
            translations, edges);
        addAxiomSubjectRelation(axiomNode, classExprNode, edges);
        addAxiomAnnotations(axiomNode, axiom, translations, edges);
        return buildTranslation(axiom, axiomNode, translations, edges);
    }

    @Nonnull
    @Override
    public Translation visit(@Nonnull OWLAnnotationAssertionAxiom axiom) {
        var axiomNode = createAxiomNode(axiom, ANNOTATION_ASSERTION);
        var translations = newTranslationBuilder();
        var edges = newEdgesBuilder();
        var annotationSubjectNode = addAnnotationSubjectTranslationAndEdge(axiomNode,
            axiom.getSubject(),
            translations, edges);
        addPropertyExprTranslationAndEdge(axiomNode,
            ANNOTATION_PROPERTY, axiom.getProperty(),
            translations, edges);
        var annotationValueNode = addAnnotationValueTranslationAndEdge(axiomNode,
            axiom.getValue(),
            translations, edges);
        addRelatedToEdge(annotationSubjectNode, annotationValueNode,
            Properties.create(ImmutableMap.of(
                PropertyFields.IRI, String.valueOf(axiom.getProperty().getIRI()),
                PropertyFields.TYPE, axiom.getProperty().getEntityType().getName())),
            edges);
        addAxiomSubjectRelation(axiomNode, annotationSubjectNode, edges);
        addAxiomAnnotations(axiomNode, axiom, translations, edges);
        return buildTranslation(axiom, axiomNode, translations, edges);
    }

    @Nonnull
    @Override
    public Translation visit(@Nonnull OWLSubAnnotationPropertyOfAxiom axiom) {
        var axiomNode = createAxiomNode(axiom, NodeLabels.SUB_ANNOTATION_PROPERTY_OF);
        var translations = newTranslationBuilder();
        var edges = newEdgesBuilder();
        var subPropertyNode = addPropertyExprTranslationAndEdge(axiomNode,
            SUB_ANNOTATION_PROPERTY, axiom.getSubProperty(),
            translations, edges);
        var superPropertyNode = addPropertyExprTranslationAndEdge(axiomNode,
            SUPER_ANNOTATION_PROPERTY, axiom.getSuperProperty(),
            translations, edges);
        addAugmentedEdge(subPropertyNode, superPropertyNode, SUB_ANNOTATION_PROPERTY_OF, edges);
        addAxiomSubjectRelation(axiomNode, subPropertyNode, edges);
        addAxiomAnnotations(axiomNode, axiom, translations, edges);
        return buildTranslation(axiom, axiomNode, translations, edges);
    }

    @Nonnull
    @Override
    public Translation visit(@Nonnull OWLAnnotationPropertyDomainAxiom axiom) {
        var axiomNode = createAxiomNode(axiom, ANNOTATION_PROPERTY_DOMAIN);
        var translations = newTranslationBuilder();
        var edges = newEdgesBuilder();
        var propertyNode = addPropertyExprTranslationAndEdge(axiomNode,
            ANNOTATION_PROPERTY, axiom.getProperty(),
            translations, edges);
        var domainNode = addIriTranslationAndEdge(axiomNode,
            DOMAIN, axiom.getDomain(),
            translations, edges);
        addAugmentedEdge(propertyNode, domainNode, DOMAIN, edges);
        addAxiomSubjectRelation(axiomNode, propertyNode, edges);
        addAxiomAnnotations(axiomNode, axiom, translations, edges);
        return buildTranslation(axiom, axiomNode, translations, edges);
    }

    @Nonnull
    @Override
    public Translation visit(@Nonnull OWLAnnotationPropertyRangeAxiom axiom) {
        var axiomNode = createAxiomNode(axiom, ANNOTATION_PROPERTY_RANGE);
        var translations = newTranslationBuilder();
        var edges = newEdgesBuilder();
        var propertyExprTranslation = propertyExprTranslator.translate(axiom.getProperty());
        var propertyExprNode = propertyExprTranslation.getMainNode();
        translations.add(propertyExprTranslation);
        edges.add(edgeFactory.createEdge(axiomNode,
            propertyExprNode,
            ANNOTATION_PROPERTY));
        var rangeNode = addIriTranslationAndEdge(axiomNode,
            RANGE, axiom.getRange(),
            translations, edges);
        addAugmentedEdge(propertyExprNode, rangeNode, RANGE, edges);
        addAxiomSubjectRelation(axiomNode, propertyExprNode, edges);
        addAxiomAnnotations(axiomNode, axiom, translations, edges);
        return buildTranslation(axiom, axiomNode, translations, edges);
    }

    @Nonnull
    @Override
    public Translation visit(@Nonnull SWRLRule rule) {
        return Translation.create(rule,
            Node.create(NodeId.create(0), SWRL_RULE),
            ImmutableList.of(),
            ImmutableList.of());
    }

    @Nonnull
    private Node createAxiomNode(OWLAxiom axiom, NodeLabels nodeLabels) {
        return nodeFactory.createNode(axiom, nodeLabels);
    }

    @Nonnull
    private static Builder<Translation> newTranslationBuilder() {
        return new Builder<>();
    }

    @Nonnull
    private static Builder<Edge> newEdgesBuilder() {
        return new Builder<>();
    }

    private Node addIriTranslationAndEdge(@Nonnull Node mainNode,
                                          @Nonnull EdgeLabel edgeLabel,
                                          @Nonnull IRI iri,
                                          @Nonnull Builder<Translation> translations,
                                          @Nonnull Builder<Edge> edges) {
        return addTranslationAndEdge(
            annotationValueTranslator.translate(iri),
            mainNode, edgeLabel, translations, edges);
    }

    private Node addEntityTranslationAndEdge(@Nonnull Node mainNode,
                                             @Nonnull EdgeLabel edgeLabel,
                                             @Nonnull OWLEntity entity,
                                             @Nonnull Builder<Translation> translations,
                                             @Nonnull Builder<Edge> edges) {
        return addTranslationAndEdge(
            entityTranslator.translate(entity),
            mainNode, edgeLabel, translations, edges);
    }

    private List<Node> addIndividualTranslationsAndEdges(@Nonnull Node axiomNode,
                                                         @Nonnull EdgeLabel edgeLabel,
                                                         @Nonnull Set<OWLIndividual> individuals,
                                                         @Nonnull Builder<Translation> translations,
                                                         @Nonnull Builder<Edge> edges) {
        return individuals
            .stream()
            .map(individual -> addIndividualTranslationAndEdge(
                axiomNode, edgeLabel, individual, translations, edges))
            .collect(Collectors.toList());
    }

    private Node addIndividualTranslationAndEdge(@Nonnull Node mainNode,
                                                 @Nonnull EdgeLabel edgeLabel,
                                                 @Nonnull OWLIndividual individual,
                                                 @Nonnull Builder<Translation> translations,
                                                 @Nonnull Builder<Edge> edges) {
        return addTranslationAndEdge(
            individualTranslator.translate(individual),
            mainNode, edgeLabel, translations, edges);
    }

    private Node addLiteralTranslationAndEdge(@Nonnull Node mainNode,
                                              @Nonnull OWLLiteral literal,
                                              @Nonnull Builder<Translation> translations,
                                              @Nonnull Builder<Edge> edges) {
        return addTranslationAndEdge(
            literalTranslator.translate(literal),
            mainNode, EdgeLabel.TARGET_VALUE, translations, edges);
    }

    private List<Node> addClassExprTranslationsAndEdges(@Nonnull Node axiomNode,
                                                        @Nonnull EdgeLabel edgeLabel,
                                                        @Nonnull Set<OWLClassExpression> classExprs,
                                                        @Nonnull Builder<Translation> translations,
                                                        @Nonnull Builder<Edge> edges) {
        return classExprs
            .stream()
            .map(classExpr -> addClassExprTranslationAndEdge(
                axiomNode, edgeLabel, classExpr, translations, edges))
            .collect(Collectors.toList());
    }

    private Node addClassExprTranslationAndEdge(@Nonnull Node mainNode,
                                                @Nonnull EdgeLabel edgeLabel,
                                                @Nonnull OWLClassExpression classExpr,
                                                @Nonnull Builder<Translation> translations,
                                                @Nonnull Builder<Edge> edges) {
        return addTranslationAndEdge(
            classExprTranslator.translate(classExpr),
            mainNode, edgeLabel, translations, edges);
    }

    private List<Node> addPropertyExprTranslationsAndEdges(@Nonnull Node axiomNode,
                                                           @Nonnull EdgeLabel edgeLabel,
                                                           @Nonnull Set<? extends OWLPropertyExpression> propertyExprs,
                                                           @Nonnull Builder<Translation> translations,
                                                           @Nonnull Builder<Edge> edges) {
        return propertyExprs
            .stream()
            .map(propertyExpr -> addPropertyExprTranslationAndEdge(
                axiomNode, edgeLabel, propertyExpr, translations, edges))
            .collect(Collectors.toList());
    }

    private Node addPropertyExprTranslationAndEdge(@Nonnull Node mainNode,
                                                   @Nonnull EdgeLabel edgeLabel,
                                                   @Nonnull OWLPropertyExpression propertyExpr,
                                                   @Nonnull Builder<Translation> translations,
                                                   @Nonnull Builder<Edge> edges) {
        return addTranslationAndEdge(
            propertyExprTranslator.translate(propertyExpr),
            mainNode, edgeLabel, translations, edges);
    }

    private Node addDataRangeTranslationAndEdge(@Nonnull Node mainNode,
                                                @Nonnull EdgeLabel edgeLabel,
                                                @Nonnull OWLDataRange dataRange,
                                                @Nonnull Builder<Translation> translations,
                                                @Nonnull Builder<Edge> edges) {
        return addTranslationAndEdge(
            dataRangeTranslator.translate(dataRange),
            mainNode, edgeLabel, translations, edges);
    }

    private void addAnnotationTranslationAndEdge(@Nonnull Node mainNode,
                                                 @Nonnull OWLAnnotation annotation,
                                                 @Nonnull Builder<Translation> translations,
                                                 @Nonnull Builder<Edge> edges) {
        addTranslationAndEdge(
            annotationTranslator.translate(annotation),
            mainNode, EdgeLabel.AXIOM_ANNOTATION, translations, edges);
    }

    private Node addAnnotationSubjectTranslationAndEdge(@Nonnull Node mainNode,
                                                        @Nonnull OWLAnnotationSubject subject,
                                                        @Nonnull Builder<Translation> translations,
                                                        @Nonnull Builder<Edge> edges) {
        return addTranslationAndEdge(
            annotationSubjectTranslator.translate(subject),
            mainNode, EdgeLabel.ANNOTATION_SUBJECT, translations, edges);
    }

    private Node addAnnotationValueTranslationAndEdge(@Nonnull Node mainNode,
                                                      @Nonnull OWLAnnotationValue value,
                                                      @Nonnull Builder<Translation> translations,
                                                      @Nonnull Builder<Edge> edges) {
        return addTranslationAndEdge(
            annotationValueTranslator.translate(value),
            mainNode, EdgeLabel.ANNOTATION_VALUE, translations, edges);
    }

    private Node addTranslationAndEdge(@Nonnull Translation translation,
                                       @Nonnull Node mainNode,
                                       @Nonnull EdgeLabel edgeLabel,
                                       @Nonnull Builder<Translation> translations,
                                       @Nonnull Builder<Edge> edges) {
        var translationMainNode = translation.getMainNode();
        translations.add(translation);
        edges.add(edgeFactory.createEdge(mainNode,
            translationMainNode,
            edgeLabel));
        return translationMainNode;
    }

    private void addAxiomAnnotations(@Nonnull Node mainNode,
                                     @Nonnull OWLAxiom axiom,
                                     @Nonnull Builder<Translation> translations,
                                     @Nonnull Builder<Edge> edges) {
        axiom.getAnnotations()
            .forEach(ann -> addAnnotationTranslationAndEdge(
                mainNode, ann, translations, edges));
    }

    private void addRelatedToEdge(@Nonnull Node fromNode,
                                  @Nonnull Node toNode,
                                  @Nonnull Properties edgeProperties,
                                  @Nonnull Builder<Edge> edges) {
        edges.add(edgeFactory.createEdge(
            fromNode, toNode, EdgeLabel.RELATED_TO, edgeProperties));
    }

    private void addAugmentedEdge(@Nonnull Node fromNode,
                                  @Nonnull Node toNode,
                                  @Nonnull EdgeLabel edgeLabel,
                                  @Nonnull Builder<Edge> edges) {
        edges.add(edgeFactory.createEdge(fromNode, toNode, edgeLabel));
    }

    private void addRelatedToEdge(@Nonnull Node subjectNode,
                                  @Nonnull Node fillerNode,
                                  @Nonnull OWLPropertyExpression propertyExpr,
                                  @Nonnull Builder<Edge> edges) {
        if (propertyExpr.isNamed()) {
            addRelatedToEdge(subjectNode, fillerNode, (OWLEntity) propertyExpr, edges);
        }
    }

    private void addRelatedToEdge(@Nonnull Node subjectNode,
                                  @Nonnull Node fillerNode,
                                  @Nonnull OWLEntity entity,
                                  @Nonnull Builder<Edge> edges) {
        addRelatedToEdge(subjectNode, fillerNode,
            Properties.create(ImmutableMap.of(
                PropertyFields.IRI, String.valueOf(entity.getIRI()),
                PropertyFields.TYPE, entity.getEntityType().getName())),
            edges);
    }

    private void addReciprocalEdges(List<Node> nodes, EdgeLabel edgeLabel, Builder<Edge> edges) {
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = 0; j < nodes.size(); j++) {
                if (i != j) {
                    addAugmentedEdge(nodes.get(i), nodes.get(j), edgeLabel, edges);
                }
            }
        }
    }

    private static Translation buildTranslation(@Nonnull OWLAxiom axiom,
                                                @Nonnull Node mainNode,
                                                @Nonnull Builder<Translation> translations,
                                                @Nonnull Builder<Edge> edges) {
        return Translation.create(axiom, mainNode, edges.build(), translations.build());
    }
}
