MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument {ontologyDocumentId:$ontoDocId})
MATCH (o)<-[:IN_ONTOLOGY_SIGNATURE]-(child:AnnotationProperty {iri:$entityIri})
MATCH (o)<-[:IN_ONTOLOGY_SIGNATURE]-(n:AnnotationProperty)
MATCH (child)-[:SUB_ANNOTATION_PROPERTY_OF*]->(n)
RETURN DISTINCT n