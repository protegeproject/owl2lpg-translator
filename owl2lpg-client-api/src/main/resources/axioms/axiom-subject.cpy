CALL {
  MATCH (n:Axiom)-[:AXIOM_SUBJECT]->(:Class {iri:$subjectIri})
  RETURN n
  UNION
  MATCH (n:Axiom)-[:AXIOM_SUBJECT]->(:IRI {iri:$subjectIri})
  RETURN n
}
MATCH p=(n)-[* {structuralSpec:true}]->(m)
OPTIONAL MATCH q=(m:IRI)-[:RELATED_TO {type:"AnnotationProperty"}]->(:Literal)
RETURN p, q