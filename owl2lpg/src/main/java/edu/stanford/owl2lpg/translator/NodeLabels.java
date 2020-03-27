package edu.stanford.owl2lpg.translator;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * A collection of node labels used to name the OWL 2 objects in the
 * specification.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class NodeLabels {

  // @formatter:off
  public static final List<String> IRI = Lists.newArrayList("IRI");
  public static final List<String> CLASS = Lists.newArrayList("Class", "ClassExpression", "Entity");
  public static final List<String> DATATYPE = Lists.newArrayList("Datatype", "Entity");
  public static final List<String> OBJECT_PROPERTY = Lists.newArrayList("ObjectProperty", "ObjectPropertyExpression", "Entity");
  public static final List<String> DATA_PROPERTY = Lists.newArrayList("DataProperty", "DataPropertyExpression", "Entity");
  public static final List<String> ANNOTATION_PROPERTY = Lists.newArrayList("AnnotationProperty", "Entity");
  public static final List<String> NAMED_INDIVIDUAL = Lists.newArrayList("NamedIndividual", "Individual", "Entity");
  public static final List<String> DECLARATION = Lists.newArrayList("Declaration", "Axiom");
  public static final List<String> SUBCLASSOF = Lists.newArrayList("SubClassOf", "ClassAxiom", "Axiom");
  public static final List<String> OBJECT_INTERSECTION_OF = Lists.newArrayList("ObjectIntersectionOf", "ClassExpression");
  // @formatter:on
}
