MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument {ontologyDocumentId:$ontoDocId})
MATCH (o)<-[:IN_ONTOLOGY_SIGNATURE]-(child:Class {iri:$entityIri})
MATCH (o)<-[:IN_ONTOLOGY_SIGNATURE]-(parent:Class)
MATCH p=(child)-[:SUB_CLASS_OF*]->(parent)
WHERE NOT((parent)-[:SUB_CLASS_OF]->()) AND apoc.coll.duplicates(NODES(p)) = []
RETURN DISTINCT p