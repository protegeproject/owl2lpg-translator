CALL db.index.fulltext.queryNodes("obo_id_index", $searchString) YIELD node AS entity
MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument)
MATCH (o)<-[:IN_ONTOLOGY_SIGNATURE]-(entity:Entity)
RETURN DISTINCT { type: "OboId" } AS dictionaryLanguage, entity.oboId AS shortForm, entity