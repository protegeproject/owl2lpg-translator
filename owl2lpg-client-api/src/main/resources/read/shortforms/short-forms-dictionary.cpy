CALL {
  MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument)
  MATCH (o)-[:AXIOM]->(n:AnnotationAssertion)
  MATCH (n)-[:ANNOTATION_SUBJECT]->(:IRI {iri:$entityIri})
  MATCH (n)-[:ANNOTATION_VALUE]->(value:Literal)
  MATCH (n)-[:ANNOTATION_PROPERTY]->(property:AnnotationProperty)
  RETURN DISTINCT { type: "AnnotationAssertion",
      	 propertyIri: property.iri,
         lang: value.lang
   	   } AS dictionaryLanguage, value.lexicalForm AS shortForm
  UNION
  MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument)
  MATCH (o)<-[:IN_ONTOLOGY_SIGNATURE]-(entity:Entity {iri:$entityIri})
  WHERE entity.localName <> ""
  RETURN DISTINCT { type: "LocalName" } AS dictionaryLanguage, entity.localName AS shortForm
  UNION
  MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument)
  MATCH (o)<-[:IN_ONTOLOGY_SIGNATURE]-(entity:Entity {iri:$entityIri})
  WHERE entity.prefixedName <> ""
  RETURN DISTINCT { type: "PrefixedName" } AS dictionaryLanguage, entity.prefixedName AS shortForm
  UNION
  MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument)
  MATCH (o)<-[:IN_ONTOLOGY_SIGNATURE]-(entity:Entity {iri:$entityIri})
  WHERE entity.oboId <> ""
  RETURN DISTINCT { type: "OboId" } AS dictionaryLanguage, entity.oboId AS shortForm
}
RETURN dictionaryLanguage, shortForm
