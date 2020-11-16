CALL {
  MATCH (n:ObjectPropertyAssertion)-[:SOURCE_INDIVIDUAL]->(:AnonymousIndividual {nodeID:$nodeId})
  RETURN n
  UNION
  MATCH (n:NegativeObjectPropertyAssertion)-[:SOURCE_INDIVIDUAL]->(:AnonymousIndividual {nodeID:$nodeId})
  RETURN n
}
MATCH (o:OntologyDocument {ontologyDocumentId:$ontoDocId})-[:AXIOM]->(n)
MATCH p=(n)-[:ONTOLOGY_ANNOTATION|AXIOM_ANNOTATION|ANNOTATION_ANNOTATION|AXIOM|ENTITY|ENTITY_IRI|OBJECT_PROPERTY|INDIVIDUAL|LITERAL|CLASS|CLASS_EXPRESSION|SUB_CLASS_EXPRESSION|SUPER_CLASS_EXPRESSION|DISJOINT_CLASS_EXPRESSION|OBJECT_PROPERTY_EXPRESSION|INVERSE_OBJECT_PROPERTY_EXPRESSION|SUB_OBJECT_PROPERTY_EXPRESSION|SUPER_OBJECT_PROPERTY_EXPRESSION|DATA_PROPERTY_EXPRESSION|SUB_DATA_PROPERTY_EXPRESSION|SUPER_DATA_PROPERTY_EXPRESSION|ANNOTATION_PROPERTY|SUB_ANNOTATION_PROPERTY|SUPER_ANNOTATION_PROPERTY|ANNOTATION_SUBJECT|ANNOTATION_VALUE|DATA_RANGE|DATATYPE|RESTRICTION|CONSTRAINING_FACET|RESTRICTION_VALUE|DOMAIN|RANGE|SOURCE_INDIVIDUAL|TARGET_INDIVIDUAL|TARGET_VALUE*]->(m)
RETURN p