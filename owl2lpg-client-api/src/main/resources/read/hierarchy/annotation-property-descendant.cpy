MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument {ontologyDocumentId:$ontoDocId})
MATCH (o)<-[:ENTITY_SIGNATURE_OF]-(n:AnnotationProperty)
MATCH (o)<-[:ENTITY_SIGNATURE_OF]-(parent:AnnotationProperty {iri:$entityIri})
MATCH (n)-[:SUB_ANNOTATION_PROPERTY_OF*]->(parent)
RETURN DISTINCT n