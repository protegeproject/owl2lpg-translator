MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument {ontologyDocumentId:$ontoDocId})
MATCH (o)<-[:IN_ONTOLOGY_SIGNATURE]-(child:DataProperty {iri:$entityIri})
MATCH (o)<-[:IN_ONTOLOGY_SIGNATURE]-(parent:DataProperty)
MATCH p=(child)-[:SUB_DATA_PROPERTY_OF*]->(parent)
WHERE NOT((parent)-[:SUB_DATA_PROPERTY_OF]->()) AND apoc.coll.duplicates(NODES(p)) = []
RETURN DISTINCT p