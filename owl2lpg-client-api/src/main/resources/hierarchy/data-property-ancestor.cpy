MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument {ontologyDocumentId:$ontoDocId})
MATCH (o)<-[:ENTITY_SIGNATURE_OF]-(child:DataProperty {iri:$entityIri})
MATCH (o)<-[:ENTITY_SIGNATURE_OF]-(parent:DataProperty)
MATCH p=(child)-[:SUB_DATA_PROPERTY_OF*]->(parent:DataProperty)
WHERE NOT((parent)-[:SUB_DATA_PROPERTY_OF]->()) AND apoc.coll.duplicates(NODES(p)) = []
RETURN p