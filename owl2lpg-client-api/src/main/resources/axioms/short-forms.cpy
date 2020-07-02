MATCH (:OntologyDocument {ontologyDocumentId:$ontoDocId})-[:AXIOM]->(n:AnnotationAssertion)
MATCH (n)-[:ANNOTATION_SUBJECT]->(:IRI {iri:$entityIri})
MATCH (n)-[:ANNOTATION_PROPERTY]->(annotationProperty:AnnotationProperty)
MATCH (n)-[:ANNOTATION_VALUE]->(value:Literal)
RETURN annotationProperty, value