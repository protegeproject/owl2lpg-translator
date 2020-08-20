MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument {ontologyDocumentId:$ontoDocId})
MATCH (o)<-[:ENTITY_SIGNATURE_OF]-(child:AnnotationProperty)
MATCH (o)<-[:ENTITY_SIGNATURE_OF]-(parent:AnnotationProperty {iri:$entityIri})
MATCH p=(parent)<-[:SUB_ANNOTATION_PROPERTY_OF*]-(child)
RETURN p