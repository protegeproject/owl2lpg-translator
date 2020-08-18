CALL db.index.fulltext.queryNodes("annotation_value_index", $searchString) YIELD node AS value
MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument)
MATCH (o)<-[:AXIOM_OF]-(n:AnnotationAssertion)-[:ANNOTATION_SUBJECT]->(:IRI)<-[:ENTITY_IRI]-(entity:Entity)
MATCH (n)-[:ANNOTATION_PROPERTY]->(annotationProperty:AnnotationProperty)
MATCH (n)-[:ANNOTATION_VALUE]->(value:Literal)
RETURN { type: "AnnotationAssertion",
         propertyIri: annotationProperty.iri,
         lang: value.lang
       } AS dictionaryLanguage,
       value.lexicalForm AS shortForm,
       entity
LIMIT 100