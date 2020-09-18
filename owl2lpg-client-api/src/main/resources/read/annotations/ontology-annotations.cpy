MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument {ontologyDocumentId:$ontoDocId})
MATCH (o)-[:ONTOLOGY_ANNOTATION]->(n:Annotation)
MATCH p=(n)-[* {structuralSpec:true}]->(m)
RETURN p