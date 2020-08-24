MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument {ontologyDocumentId:$ontoDocId})
MATCH (o)<-[:ENTITY_SIGNATURE_OF]-(n:ObjectProperty)
WHERE NOT((n)-[:SUB_OBJECT_PROPERTY_OF]->()) AND n.iri <> "http://www.w3.org/2002/07/owl#topObjectProperty"
RETURN n