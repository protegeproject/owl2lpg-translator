MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument {ontologyDocumentId:$ontoDocId})
CREATE (n:AnnotationAssertion:AnnotationAxiom:Axiom {digest:$digest})
WITH o, n
MERGE (i:IRI {iri:$iri})
MERGE (s:IRI {iri:$annotationSubjectIri})
CREATE (o)<-[:AXIOM_OF]-(n)-[:AXIOM_SUBJECT]->(s)-[:RELATED_TO {iri:$annotationPropertyIri, type:"AnnotationProperty"}]->(i)
CREATE (o)-[:AXIOM {structuralSpec:true}]->(n)-[:ANNOTATION_SUBJECT {structuralSpec:true}]->(s)
WITH n, i
MERGE (p:AnnotationProperty:Entity {iri:$annotationPropertyIri})
CREATE (p)<-[:ANNOTATION_PROPERTY {structuralSpec:true}]-(n)-[:ANNOTATION_VALUE {structuralSpec:true}]->(i)