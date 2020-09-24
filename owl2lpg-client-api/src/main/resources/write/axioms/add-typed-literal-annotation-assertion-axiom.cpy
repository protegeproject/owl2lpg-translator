MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument {ontologyDocumentId:$ontoDocId})
CREATE (n:AnnotationAssertion:AnnotationAxiom:Axiom {digest:$digest})
CREATE (l:Literal {lexicalForm:$lexicalForm, datatype:$datatype})
WITH o, n, l
MERGE (s:IRI {iri:$annotationSubjectIri})
CREATE (o)<-[:AXIOM_OF]-(n)-[:AXIOM_SUBJECT]->(s)-[:RELATED_TO {iri:$annotationPropertyIri, type:"AnnotationProperty"}]->(l)
CREATE (o)-[:AXIOM {structuralSpec:true}]->(n)-[:ANNOTATION_SUBJECT {structuralSpec:true}]->(s)
WITH n, l
MERGE (p:AnnotationProperty:Entity {iri:$annotationPropertyIri})
CREATE (p)<-[:ANNOTATION_PROPERTY {structuralSpec:true}]-(n)-[:ANNOTATION_VALUE {structuralSpec:true}]->(l)