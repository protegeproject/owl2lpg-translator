MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(n:OntologyDocument)
SET n.isDefault = CASE WHEN n.ontologyDocumentId = $ontoDocId THEN true ELSE false END
