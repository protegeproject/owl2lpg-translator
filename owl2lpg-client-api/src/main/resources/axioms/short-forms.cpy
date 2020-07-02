MATCH (:OntologyDocument {ontologyDocumentId:$ontoDocId})-[:AXIOM]->(n:AnnotationAssertion)
MATCH (n)-[:ANNOTATION_SUBJECT]->(:IRI {iri:$entityIri})
MATCH (n)-[:ANNOTATION_PROPERTY]->(ap:AnnotationProperty)
MATCH (n)-[:ANNOTATION_VALUE]->(lit:Literal)
RETURN ap, lit