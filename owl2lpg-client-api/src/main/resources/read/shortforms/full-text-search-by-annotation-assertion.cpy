CALL db.index.fulltext.queryNodes("annotation_assertion_index", $searchString) YIELD node AS value
MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument)
MATCH (o)-[:AXIOM]->(n:AnnotationAssertion)
MATCH (n)-[:ANNOTATION_SUBJECT]->(:IRI)<-[:ENTITY_IRI]-(entity:Entity)
MATCH (n)-[:ANNOTATION_VALUE]->(value:Literal)
MATCH (n)-[:ANNOTATION_PROPERTY]->(property:AnnotationProperty)
RETURN DISTINCT { type: "AnnotationAssertion",
         propertyIri: property.iri,
         lang: value.lang
       } AS dictionaryLanguage,
       value.lexicalForm AS shortForm,
       entity