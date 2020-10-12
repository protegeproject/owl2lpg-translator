MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument {ontologyDocumentId:$ontoDocId})
MATCH (o)-[:AXIOM]->(n:Axiom)<-[:IN_AXIOM_SIGNATURE]-(e:Entity {iri:$entityIri})
MATCH (o)<-[:IN_ONTOLOGY_SIGNATURE]-(e)
MATCH p=(n)-[* {structuralSpec:true}]->(m)
RETURN p