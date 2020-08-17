MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument)
CALL {
    MATCH (o)<-[:AXIOM_OF]-(n:AnnotationAssertion)-[:ANNOTATION_SUBJECT]->(:IRI)<-[:ENTITY_IRI]-(entity:Entity)
    MATCH (n)-[:ANNOTATION_PROPERTY]->(annotationProperty:AnnotationProperty)
    MATCH (n)-[:ANNOTATION_VALUE]->(value:Literal {lexicalForm:$entityName})
    RETURN { type: "AnnotationAssertion",
         	 propertyIri: annotationProperty.iri,
             lang: value.lang
       	   } as dictionaryLanguage, value.lexicalForm AS shortForm, entity
    UNION
    MATCH (o)<-[:ENTITY_SIGNATURE_OF]-(entity:Entity {iriSuffix:$entityName})
    RETURN { type: "LocalName" } as dictionaryLanguage, entity.iriSuffix AS shortForm, entity
    UNION
    MATCH (o)<-[:ENTITY_SIGNATURE_OF]-(entity:Entity {oboId:$entityName})
    RETURN { type: "OboId" } as dictionaryLanguage, entity.obiId AS shortForm, entity
}
return dictionaryLanguage, shortForm, entity