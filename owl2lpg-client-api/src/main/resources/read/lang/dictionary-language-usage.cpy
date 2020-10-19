MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument {ontologyDocumentId:$ontoDocId})
MATCH (o)-[:AXIOM]->(n:AnnotationAssertion)
MATCH (n)-[:ANNOTATION_PROPERTY]-(a:AnnotationProperty)
MATCH (n)-[:ANNOTATION_VALUE]-(l:Literal)
WHERE l.language IS NOT null
WITH a.iri AS propertyIri, l.language as language, size(collect(l.language)) as count
RETURN { type: "AnnotationAssertion",
         propertyIri: propertyIri,
         lang: language
       } AS dictionaryLanguage, count