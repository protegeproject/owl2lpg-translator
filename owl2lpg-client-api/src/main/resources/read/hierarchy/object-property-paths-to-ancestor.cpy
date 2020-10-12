MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument {ontologyDocumentId:$ontoDocId})
MATCH (o)<-[:IN_ONTOLOGY_SIGNATURE]-(child:ObjectProperty {iri:$entityIri})
MATCH (o)<-[:IN_ONTOLOGY_SIGNATURE]-(parent:ObjectProperty)
MATCH p=(child)-[:SUB_OBJECT_PROPERTY_OF*]->(parent)
WHERE NOT((parent)-[:SUB_OBJECT_PROPERTY_OF]->()) AND apoc.coll.duplicates(NODES(p)) = []
RETURN DISTINCT p