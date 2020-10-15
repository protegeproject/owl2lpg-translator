MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument {ontologyDocumentId:$ontoDocId})
MATCH (o)<-[:IN_ONTOLOGY_SIGNATURE]-(child:DataProperty {iri:$entityIri})
MATCH (o)<-[:IN_ONTOLOGY_SIGNATURE]-(n:DataProperty)
MATCH (child)-[:SUB_DATA_PROPERTY_OF*]->(n:DataProperty)
RETURN DISTINCT n