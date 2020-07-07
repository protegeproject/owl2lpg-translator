MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument)
MATCH (o)-[:AXIOM]->(n:AnnotationAssertion)-[:ANNOTATION_SUBJECT]->(:IRI)<-[:ENTITY_IRI]-(entity:Entity)
MATCH (n)-[:ANNOTATION_PROPERTY]->(annotationProperty:AnnotationProperty)
MATCH (n)-[:ANNOTATION_VALUE]->(value:Literal {lexicalForm:$lexicalForm})
RETURN entity, annotationProperty, value