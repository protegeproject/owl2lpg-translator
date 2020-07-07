MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument)
MATCH (o)-[:AXIOM]->(n:AnnotationAssertion)-[:ANNOTATION_SUBJECT]->(:IRI {iri:$entityIri})
MATCH (n)-[:ANNOTATION_PROPERTY]->(annotationProperty:AnnotationProperty)
MATCH (n)-[:ANNOTATION_VALUE]->(value:Literal)
RETURN annotationProperty, value