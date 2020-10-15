MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument {ontologyDocumentId:$ontoDocId})
CALL {
  MATCH (o)-[:AXIOM]->(n:DataPropertyAssertion)-[:AXIOM_SUBJECT]->(:NamedIndividual {iri:$entityIri})
  RETURN n
  UNION
  MATCH (o)-[:AXIOM]->(n:NegativeDataPropertyAssertion)-[:AXIOM_SUBJECT]->(:NamedIndividual {iri:$entityIri})
  RETURN n
}
MATCH p=(n)-[* {structuralSpec:true}]->(m)
RETURN p