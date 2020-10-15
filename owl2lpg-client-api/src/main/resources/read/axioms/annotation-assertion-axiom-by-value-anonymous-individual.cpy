MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument {ontologyDocumentId:$ontoDocId})
MATCH (o)-[:AXIOM]->(n:AnnotationAssertion)-[:ANNOTATION_VALUE]->(:AnonymousIndividual {nodeID:$nodeId})
MATCH p=(n)-[* {structuralSpec:true}]->(m)
RETURN p