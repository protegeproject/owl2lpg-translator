MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument {ontologyDocumentId:$ontoDocId})
MATCH (o)<-[:AXIOM_OF]-(n:SubDataPropertyOf)-[:SUB_DATA_PROPERTY_EXPRESSION]->(:DataProperty {iri:$entityIri})
MATCH p=(n)-[* {structuralSpec:true}]->(m)
RETURN p