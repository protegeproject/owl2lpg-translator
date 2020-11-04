MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument {ontologyDocumentId:$ontoDocId})
MATCH (o)<-[:IN_ONTOLOGY_SIGNATURE]-(n:AnnotationProperty {iri:$entityIri})
MATCH p=(n)<-[:SUB_ANNOTATION_PROPERTY_OF]-()
RETURN COUNT(p) AS count
