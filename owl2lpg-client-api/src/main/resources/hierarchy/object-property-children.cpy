MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument {ontologyDocumentId:$ontoDocId})
MATCH (o)<-[:ENTITY_SIGNATURE_OF]-(n:ObjectProperty)
MATCH (o)<-[:ENTITY_SIGNATURE_OF]-(parent:ObjectProperty {iri:$entityIri})
MATCH (n)-[:SUB_OBJECT_PROPERTY_OF]->(parent)
RETURN DISTINCT n