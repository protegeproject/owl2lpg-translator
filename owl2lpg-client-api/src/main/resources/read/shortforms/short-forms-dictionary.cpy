CALL {
  MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument)
  MATCH (o)-[:AXIOM]->(:AnnotationAssertion)-[:ANNOTATION_SUBJECT]->(:IRI {iri:$entityIri})-[relatedTo:RELATED_TO]->(value:Literal)
  RETURN DISTINCT { type: "AnnotationAssertion",
      	 propertyIri: relatedTo.iri,
         lang: value.lang
   	   } AS dictionaryLanguage, value.lexicalForm AS shortForm
  UNION
  MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument)
  MATCH (o)<-[:IN_ONTOLOGY_SIGNATURE]-(entity:Entity {iri:$entityIri})
  RETURN DISTINCT { type: "LocalName" } AS dictionaryLanguage, entity.localName AS shortForm
  UNION
  MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument)
  MATCH (o)<-[:IN_ONTOLOGY_SIGNATURE]-(entity:Entity {iri:$entityIri})
  RETURN DISTINCT { type: "PrefixedName" } AS dictionaryLanguage, entity.prefixedName AS shortForm
  UNION
  MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument)
  MATCH (o)<-[:IN_ONTOLOGY_SIGNATURE]-(entity:Entity {iri:$entityIri})
  RETURN DISTINCT { type: "OboId" } AS dictionaryLanguage, entity.oboId AS shortForm
}
RETURN dictionaryLanguage, shortForm
