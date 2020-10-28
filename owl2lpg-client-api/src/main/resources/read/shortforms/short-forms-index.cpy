CALL {
  MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument)
  MATCH (o)-[:AXIOM]->(:AnnotationAssertion)-[:ANNOTATION_SUBJECT]->(subject:IRI)-[relatedTo:RELATED_TO]->(value:Literal {lexicalForm:$entityName})
  MATCH (subject)<-[:ENTITY_IRI]-(entity:Entity)
  RETURN DISTINCT { type: "AnnotationAssertion",
      	 propertyIri: relatedTo.iri,
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