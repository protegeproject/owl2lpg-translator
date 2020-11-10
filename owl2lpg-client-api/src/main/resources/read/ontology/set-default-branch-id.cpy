MATCH (:Project {projectId:$projectId})-[:BRANCH]->(n:Branch)
SET n.isDefault = CASE WHEN n.branchId = $branchId THEN true ELSE false END