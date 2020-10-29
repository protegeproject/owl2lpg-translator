CALL db.index.fulltext.queryNodes("prefixed_name_index", $searchString) YIELD node AS entity
MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument)
MATCH (o)<-[:IN_ONTOLOGY_SIGNATURE]-(entity:Entity)
RETURN DISTINCT { type: "PrefixedName" } AS dictionaryLanguage, entity.prefixedName AS shortForm, entity