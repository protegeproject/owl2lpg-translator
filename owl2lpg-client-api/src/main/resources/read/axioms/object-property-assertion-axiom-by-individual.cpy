MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument {ontologyDocumentId:$ontoDocId})
CALL {
  MATCH (o)<-[:AXIOM_OF]-(n:ObjectPropertyAssertion)-[:AXIOM_SUBJECT]->(:NamedIndividual {iri:$entityIri})
  RETURN n
  UNION
  MATCH (o)<-[:AXIOM_OF]-(n:NegativeObjectPropertyAssertion)-[:AXIOM_SUBJECT]->(:NamedIndividual {iri:$entityIri})
  RETURN n
}
MATCH p=(n)-[* {structuralSpec:true}]->(m)
RETURN p