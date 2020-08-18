CALL db.index.fulltext.queryNodes("annotation_assertion_index", $searchString) YIELD node AS value
MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument)
MATCH (o)<-[:AXIOM_OF]-(:AnnotationAssertion)-[:ANNOTATION_SUBJECT]->(subject:IRI)-[relatedTo:RELATED_TO]->(value:Literal)
MATCH (subject)<-[:ENTITY_IRI]-(entity:Entity)
RETURN DISTINCT { type: "AnnotationAssertion",
         propertyIri: relatedTo.iri,
         lang: value.lang
       } AS dictionaryLanguage,
       value.lexicalForm AS shortForm,
       entity
LIMIT 250