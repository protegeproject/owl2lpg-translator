MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument {ontologyDocumentId:$ontoDocId})
MATCH (o)<-[:IN_ONTOLOGY_SIGNATURE]-(child:Class {iri:$entityIri})
MATCH (o)<-[:IN_ONTOLOGY_SIGNATURE]-(n:Class)
MATCH (child)-[:SUB_CLASS_OF*]->(n:Class)
RETURN DISTINCT n