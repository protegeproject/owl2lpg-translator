MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument {ontologyDocumentId:$ontoDocId})
MATCH (o)<-[:ENTITY_SIGNATURE_OF]-(n:Class)
MATCH (o)<-[:ENTITY_SIGNATURE_OF]-(parent:Class {iri:$entityIri})
MATCH (n)-[:SUB_CLASS_OF]->(parent)
RETURN DISTINCT n
LIMIT 100