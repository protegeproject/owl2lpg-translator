MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument {ontologyDocumentId:$ontoDocId})
MATCH (o)<-[:ENTITY_SIGNATURE_OF]-(child:AnnotationProperty {iri:$entityIri})
MATCH (o)<-[:ENTITY_SIGNATURE_OF]-(n:AnnotationProperty)
MATCH (child)-[:SUB_ANNOTATION_PROPERTY_OF*]->(n)
RETURN DISTINCT n