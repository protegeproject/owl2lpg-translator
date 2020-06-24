CALL {
  MATCH (n:Axiom)-[:AXIOM_SUBJECT]->(:Class {iri:$subjectIri})
  RETURN n
  UNION
  MATCH (n:Axiom)-[:AXIOM_SUBJECT]->(:IRI {iri:$subjectIri})
  RETURN n
}
MATCH p=(n)-[* {structuralSpec:true}]->()
RETURN n, p