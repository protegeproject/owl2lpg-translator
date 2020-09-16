MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument {ontologyDocumentId:$ontoDocId})
MATCH (o)<-[:ENTITY_SIGNATURE_OF]-(n:Class)
WHERE (n)-[:SUB_CLASS_OF]->(:Class {iri:"http://www.w3.org/2002/07/owl#Thing"})
  OR (NOT((n)-[:SUB_CLASS_OF]->()) AND n.iri <> "http://www.w3.org/2002/07/owl#Thing")
RETURN DISTINCT n