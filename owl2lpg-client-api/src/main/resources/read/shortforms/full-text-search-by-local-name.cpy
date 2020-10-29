CALL db.index.fulltext.queryNodes("local_name_index", $searchString) YIELD node AS entity
MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument)
MATCH (o)<-[:IN_ONTOLOGY_SIGNATURE]-(entity:Entity)
RETURN DISTINCT { type: "LocalName" } AS dictionaryLanguage, entity.localName AS shortForm, entity