MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument {ontologyDocumentId:$ontoDocId})
MATCH (o)<-[:ENTITY_SIGNATURE_OF]-(child:DataProperty {iri:$entityIri})
MATCH (o)<-[:ENTITY_SIGNATURE_OF]-(n:DataProperty)
MATCH (child)-[:SUB_DATA_PROPERTY_OF*]->(n:DataProperty)
RETURN DISTINCT n