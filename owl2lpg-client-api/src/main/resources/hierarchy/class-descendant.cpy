MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument {ontologyDocumentId:$ontoDocId})
MATCH (o)-[:AXIOM]->(:Declaration)-[:ENTITY]->(child:Class)
MATCH (o)-[:AXIOM]->(:Declaration)-[:ENTITY]->(parent:Class {iri:$entityIri})
// MATCH p=(parent)<-[:SUB_CLASS_OF*]-(child)
// MATCH p=(child)-[:SUB_CLASS_OF*]->(parent)
MATCH p=(parent)<-[:SUB_CLASS_OF]-(child)
// WHERE NOT(()-[:SUB_CLASS_OF]->(child))
RETURN p
LIMIT 100