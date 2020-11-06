MATCH (:Project {projectId:$projectId})-[:BRANCH]->(n:Branch)
RETURN n