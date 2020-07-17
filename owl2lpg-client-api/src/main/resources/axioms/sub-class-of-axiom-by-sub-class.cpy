MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument {ontologyDocumentId:$ontoDocId})
MATCH (o)-[:AXIOM]->(n:SubClassOf)
MATCH p=(m)<-[* {structuralSpec:true}]-(n)-[:SUB_CLASS_EXPRESSION]->(:Class {iri:$entityIri})
RETURN p