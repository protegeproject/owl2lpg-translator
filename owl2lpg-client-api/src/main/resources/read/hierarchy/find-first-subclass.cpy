MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument {ontologyDocumentId:$ontoDocId})
MATCH (o)<-[:IN_ONTOLOGY_SIGNATURE]-(child:Class)
MATCH (o)<-[:IN_ONTOLOGY_SIGNATURE]-(parent:Class {iri:$entityIri})
MATCH (child)-[:SUB_CLASS_OF]->(parent)
RETURN child
LIMIT 1