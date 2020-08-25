MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument {ontologyDocumentId:$ontoDocId})
MATCH (o)<-[:ENTITY_SIGNATURE_OF]-(child:ObjectProperty {iri:$entityIri})
MATCH (o)<-[:ENTITY_SIGNATURE_OF]-(n:ObjectProperty)
MATCH (child)-[:SUB_OBJECT_PROPERTY_OF]->(n)
RETURN DISTINCT n