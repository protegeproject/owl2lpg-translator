CALL db.index.fulltext.queryNodes($annotationValueFullTextIndexName, $searchString) YIELD node AS value
MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument)
MATCH (o)<-[:AXIOM_OF]-(n:AnnotationAssertion)-[:ANNOTATION_SUBJECT]->(:IRI)<-[:ENTITY_IRI]-(entity:Entity)
MATCH (n)-[:ANNOTATION_PROPERTY]->(annotationProperty:AnnotationProperty)
MATCH (n)-[:ANNOTATION_VALUE]->(value:Literal)
RETURN entity, annotationProperty, value
SKIP $offset
LIMIT $limit