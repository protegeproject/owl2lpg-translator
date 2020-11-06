MATCH (:Project {projectId:$projectId})-[:BRANCH]->(n:Branch)
SET n.default = CASE WHEN n.branchId = $branchId THEN true ELSE false END