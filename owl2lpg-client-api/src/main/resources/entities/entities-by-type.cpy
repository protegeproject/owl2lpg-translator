MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument)
MATCH (o)<-[:ENTITY_SIGNATURE_OF]-(n:Entity)
WHERE $entityType in LABELS(n)
RETURN n