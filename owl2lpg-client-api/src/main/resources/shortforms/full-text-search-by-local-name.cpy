CALL db.index.fulltext.queryNodes("local_name_index", $searchString) YIELD node AS entity
MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument)
MATCH (o)<-[:ENTITY_SIGNATURE_OF]-(entity:Entity)
RETURN { type: "LocalName" } AS dictionaryLanguage, entity.iriSuffix AS shortForm, entity
LIMIT 100