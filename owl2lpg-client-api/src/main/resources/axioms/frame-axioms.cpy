CALL {
  MATCH (:OntologyDocument {ontologyDocumentId:$ontoDocId})-[:AXIOM]->(n:Axiom)-[:AXIOM_SUBJECT]->(:Class {iri:$subjectIri})
  RETURN n
  UNION
  MATCH (:OntologyDocument {ontologyDocumentId:$ontoDocId})-[:AXIOM]->(n:Axiom)-[:AXIOM_SUBJECT]->(:IRI {iri:$subjectIri})
  RETURN n
}
MATCH p=(n)-[* {structuralSpec:true}]->(m)
RETURN p