MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument {ontologyDocumentId:$ontoDocId})
MATCH (o)<-[:ENTITY_SIGNATURE_OF]-(child:AnnotationProperty {iri:$entityIri})
MATCH (o)<-[:ENTITY_SIGNATURE_OF]-(parent:AnnotationProperty)
MATCH p=(child)-[:SUB_ANNOTATION_PROPERTY_OF*]->(parent)
WHERE NOT((parent)-[:SUB_ANNOTATION_PROPERTY_OF]->()) AND apoc.coll.duplicates(NODES(p)) = []
RETURN DISTINCT p