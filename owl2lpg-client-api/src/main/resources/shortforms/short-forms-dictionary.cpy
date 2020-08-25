MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument)
MATCH (o)<-[:AXIOM_OF]-(:AnnotationAssertion)-[:ANNOTATION_SUBJECT]->(:IRI {iri:$entityIri})-[relatedTo:RELATED_TO]->(value:Literal)
RETURN DISTINCT { type: "AnnotationAssertion",
         propertyIri: relatedTo.iri,
         lang: value.lang
       } AS dictionaryLanguage, value.lexicalForm AS shortForm