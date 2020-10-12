MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument)
MATCH (o)<-[:IN_ONTOLOGY_SIGNATURE]-(n:Entity)
WHERE $entityType in LABELS(n)
RETURN n