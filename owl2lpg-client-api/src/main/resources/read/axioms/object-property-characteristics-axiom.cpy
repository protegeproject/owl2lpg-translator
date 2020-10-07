MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument {ontologyDocumentId:$ontoDocId})
MATCH (o)-[:AXIOM]->(n)-[:AXIOM_SUBJECT]->(:ObjectProperty {iri:$entityIri})
WHERE $characteristicType in LABELS(n)
MATCH p=(n)-[* {structuralSpec:true}]->(m)
RETURN p