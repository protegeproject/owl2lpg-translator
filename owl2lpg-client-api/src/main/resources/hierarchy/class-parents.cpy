MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument {ontologyDocumentId:$ontoDocId})
MATCH (o)<-[:ENTITY_SIGNATURE_OF]-(child:Class {iri:$entityIri})
MATCH (o)<-[:ENTITY_SIGNATURE_OF]-(n:Class)
MATCH (child)-[:SUB_CLASS_OF]->(n)
RETURN DISTINCT n