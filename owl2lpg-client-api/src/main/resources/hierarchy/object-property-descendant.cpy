MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument {ontologyDocumentId:$ontoDocId})
MATCH (o)<-[:ENTITY_SIGNATURE_OF]-(child:ObjectProperty)
MATCH (o)<-[:ENTITY_SIGNATURE_OF]-(parent:ObjectProperty {iri:$entityIri})
MATCH p=(parent)<-[:SUB_OBJECT_PROPERTY_OF*]-(child)
WHERE apoc.coll.duplicates(NODES(p)) = []
RETURN p