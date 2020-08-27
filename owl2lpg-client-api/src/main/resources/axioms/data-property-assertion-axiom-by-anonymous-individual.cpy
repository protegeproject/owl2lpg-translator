MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument {ontologyDocumentId:$ontoDocId})
CALL {
  MATCH (o)<-[:AXIOM_OF]-(n:DataPropertyAssertion)-[:AXIOM_SUBJECT]->(:AnonymousIndividual {nodeID:$nodeId})
  RETURN n
  UNION
  MATCH (o)<-[:AXIOM_OF]-(n:NegativeDataPropertyAssertion)-[:AXIOM_SUBJECT]->(:AnonymousIndividual {nodeID:$nodeId})
  RETURN n
}
MATCH p=(n)-[* {structuralSpec:true}]->(m)
RETURN p