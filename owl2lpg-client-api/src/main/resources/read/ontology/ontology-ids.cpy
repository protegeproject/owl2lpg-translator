MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument)
MATCH (o)-[:ONTOLOGY_IRI]->(i:IRI)
OPTIONAL MATCH (o)-[:VERSION_IRI]->(v:IRI)
RETURN o.ontologyDocumentId AS ontoDocId, i.iri as ontologyIri, v.iri as versionIri