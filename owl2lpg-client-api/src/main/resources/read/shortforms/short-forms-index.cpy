CALL {
  MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument)
  MATCH (o)-[:AXIOM]->(n:AnnotationAssertion)
  MATCH (n)-[:ANNOTATION_SUBJECT]->(:IRI)<-[:ENTITY_IRI]-(entity:Entity)
  MATCH (n)-[:ANNOTATION_VALUE]->(value:Literal {lexicalForm:$entityName})
  MATCH (n)-[:ANNOTATION_PROPERTY]->(property:AnnotationProperty)
  RETURN DISTINCT { type: "AnnotationAssertion",
      	 propertyIri: property.iri,
         lang: value.lang
 	   } AS dictionaryLanguage, entity
  UNION
  MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument)
  MATCH (o)<-[:IN_ONTOLOGY_SIGNATURE]-(entity:Entity {localName:$entityName})
  RETURN DISTINCT { type: "LocalName" } AS dictionaryLanguage, entity
  UNION
  MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument)
  MATCH (o)<-[:IN_ONTOLOGY_SIGNATURE]-(entity:Entity {prefixedName:$entityName})
  RETURN DISTINCT { type: "PrefixedName" } AS dictionaryLanguage, entity
  UNION
  MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument)
  MATCH (o)<-[:IN_ONTOLOGY_SIGNATURE]-(entity:Entity {oboId:$entityName})
  RETURN DISTINCT { type: "OboId" } AS dictionaryLanguage, entity
}
RETURN dictionaryLanguage, entity