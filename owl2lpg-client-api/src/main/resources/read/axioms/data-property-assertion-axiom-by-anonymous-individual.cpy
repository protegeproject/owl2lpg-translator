CALL {
  MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument {ontologyDocumentId:$ontoDocId})
  MATCH (o)-[:AXIOM]->(n:DataPropertyAssertion)-[:AXIOM_SUBJECT]->(:AnonymousIndividual {nodeID:$nodeId})
  RETURN n
  UNION
  MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument {ontologyDocumentId:$ontoDocId})
  MATCH (o)-[:AXIOM]->(n:NegativeDataPropertyAssertion)-[:AXIOM_SUBJECT]->(:AnonymousIndividual {nodeID:$nodeId})
  RETURN n
}
MATCH p=(n)-[* {structuralSpec:true}]->(m)
RETURN p