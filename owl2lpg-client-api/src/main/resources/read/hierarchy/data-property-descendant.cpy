MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument {ontologyDocumentId:$ontoDocId})
MATCH (o)<-[:ENTITY_SIGNATURE_OF]-(n:DataProperty)
MATCH (o)<-[:ENTITY_SIGNATURE_OF]-(parent:DataProperty {iri:$entityIri})
MATCH (n)-[:SUB_DATA_PROPERTY_OF*]->(parent)
RETURN DISTINCT n