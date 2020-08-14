MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument {ontologyDocumentId:$ontoDocId})
MATCH (o)<-[:AXIOM_OF]-(n:SubObjectPropertyOf)-[:SUB_OBJECT_PROPERTY_EXPRESSION]->(:ObjectProperty {iri:$entityIri})
MATCH p=(n)-[* {structuralSpec:true}]->(m)
RETURN p