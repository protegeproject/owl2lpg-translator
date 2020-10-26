MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument {ontologyDocumentId:$ontoDocId})
MATCH (o)<-[:IN_ONTOLOGY_SIGNATURE]-(n:Class)
MATCH (o)<-[:IN_ONTOLOGY_SIGNATURE]-(parent:Class {iri:$entityIri})
MATCH (n)-[:SUB_CLASS_OF]->(parent)
RETURN DISTINCT n