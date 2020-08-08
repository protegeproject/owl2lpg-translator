MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument)
CALL {
    // Match
    MATCH (o)<-[:AXIOM_OF]-(n:AnnotationAssertion)-[:ANNOTATION_SUBJECT]->(:IRI)<-[:ENTITY_IRI]-(entity:Entity)
    MATCH (n)-[:ANNOTATION_PROPERTY]->(annotationProperty:AnnotationProperty)
    MATCH (n)-[:ANNOTATION_VALUE]->(value:Literal {lexicalForm:$entityName})
    RETURN entity, annotationProperty, value
    UNION
    // Match the IRI suffix
    MATCH (o)<-[:ENTITY_SIGNATURE_OF]-(entity:Entity {iriSuffix:$entityName})
    RETURN entity, null AS annotationProperty, null AS value
}
return entity, annotationProperty, value