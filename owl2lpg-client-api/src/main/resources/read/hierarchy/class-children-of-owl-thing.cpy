MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument {ontologyDocumentId:$ontoDocId})
MATCH (o)<-[:IN_ONTOLOGY_SIGNATURE]-(n:Class)
MATCH (n)-[:SUB_CLASS_OF]->(:Class {iri:"http://www.w3.org/2002/07/owl#Thing"})
RETURN n