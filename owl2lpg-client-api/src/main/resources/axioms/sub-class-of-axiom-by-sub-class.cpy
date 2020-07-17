MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument {ontologyDocumentId:$ontoDocId})
MATCH (o)-[:AXIOM]->(n:SubClassOf)-[:SUB_CLASS_EXPRESSION]->(:Class {iri:$entityIri})
MATCH p=(n)-[* {structuralSpec:true}]->(m)
RETURN p