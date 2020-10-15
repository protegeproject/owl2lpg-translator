MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument {ontologyDocumentId:$ontoDocId})
CALL {
  MATCH (o)-[:AXIOM]->(n:Axiom)-[:AXIOM_SUBJECT]->(:Datatype {iri:$entityIri})
  RETURN n
  UNION
  MATCH (o)-[:AXIOM]->(n:Axiom)-[:AXIOM_SUBJECT]->(:IRI)<-[:ENTITY_IRI]-(:Datatype {iri:$entityIri})
  RETURN n
}
MATCH p=(n)-[* {structuralSpec:true}]->(m)
RETURN p