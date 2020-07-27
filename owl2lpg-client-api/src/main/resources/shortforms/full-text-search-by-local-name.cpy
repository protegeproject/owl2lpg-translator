CALL db.index.fulltext.queryNodes($fullTextIndexName, $searchString) YIELD node AS entity
MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument)
MATCH (o)-[:AXIOM]->(:Declaration)-[:ENTITY]->(entity:Entity)
RETURN entity
LIMIT 100