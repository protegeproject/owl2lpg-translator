MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument)
MATCH (o)-[:AXIOM]->(n:AnnotationAssertion)
MATCH (n)-[:ANNOTATION_SUBJECT]->(:IRI {iri:$entityIri})
MATCH (n)-[:ANNOTATION_VALUE]->(value:Literal {language:$language})
MATCH (n)-[:ANNOTATION_PROPERTY]->(:AnnotationProperty {iri:$propertyIri})
return value.lexicalForm as shortForm