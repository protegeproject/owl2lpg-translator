MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument)
CALL {
    MATCH (o)<-[:AXIOM_OF]-(:AnnotationAssertion)-[:ANNOTATION_SUBJECT]->(subject:IRI)-[relatedTo:RELATED_TO]->(value:Literal {lexicalForm:$entityName})
    MATCH (subject)<-[:ENTITY_IRI]-(entity:Entity)
    RETURN DISTINCT { type: "AnnotationAssertion",
         	 propertyIri: relatedTo.iri,
             lang: value.lang
       	   } as dictionaryLanguage, value.lexicalForm AS shortForm, entity
    UNION
    MATCH (o)<-[:ENTITY_SIGNATURE_OF]-(entity:Entity {iriSuffix:$entityName})
    RETURN DISTINCT { type: "LocalName" } as dictionaryLanguage, entity.iriSuffix AS shortForm, entity
    UNION
    MATCH (o)<-[:ENTITY_SIGNATURE_OF]-(entity:Entity {oboId:$entityName})
    RETURN DISTINCT { type: "OboId" } as dictionaryLanguage, entity.obiId AS shortForm, entity
}
return dictionaryLanguage, shortForm, entity