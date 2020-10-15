MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument {ontologyDocumentId:$ontoDocId})
MATCH (o)<-[:IN_ONTOLOGY_SIGNATURE]-(n:ObjectProperty)
MATCH (o)<-[:IN_ONTOLOGY_SIGNATURE]-(parent:ObjectProperty {iri:$entityIri})
MATCH (n)-[:SUB_OBJECT_PROPERTY_OF]->(parent)
RETURN DISTINCT n