MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument {ontologyDocumentId:$ontoDocId})
MATCH (o)<-[:IN_ONTOLOGY_SIGNATURE]-(n:AnnotationProperty)
MATCH (o)<-[:IN_ONTOLOGY_SIGNATURE]-(parent:AnnotationProperty {iri:$entityIri})
MATCH (n)-[:SUB_ANNOTATION_PROPERTY_OF]->(parent)
RETURN DISTINCT n