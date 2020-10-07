MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument {ontologyDocumentId:$ontoDocId})
MATCH (o)-[:AXIOM]->(n:ClassAssertion)-[:AXIOM_SUBJECT]->(:NamedIndividual {iri:$entityIri})
MATCH p=(n)-[* {structuralSpec:true}]->(m)
RETURN p