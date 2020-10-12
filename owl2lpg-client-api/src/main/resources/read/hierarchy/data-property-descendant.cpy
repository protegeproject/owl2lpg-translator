MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument {ontologyDocumentId:$ontoDocId})
MATCH (o)<-[:IN_ONTOLOGY_SIGNATURE]-(n:DataProperty)
MATCH (o)<-[:IN_ONTOLOGY_SIGNATURE]-(parent:DataProperty {iri:$entityIri})
MATCH (n)-[:SUB_DATA_PROPERTY_OF*]->(parent)
RETURN DISTINCT n