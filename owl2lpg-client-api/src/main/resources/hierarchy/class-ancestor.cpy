MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument {ontologyDocumentId:$ontoDocId})
MATCH (o)-[:AXIOM]->(:Declaration)-[:ENTITY]->(child:Class {iri:$entityIri})
MATCH (o)-[:AXIOM]->(:Declaration)-[:ENTITY]->(parent:Class)
MATCH p=(child)-[:SUB_CLASS_OF*]->(parent)
WHERE NOT((parent)-[:SUB_CLASS_OF]->())
RETURN p