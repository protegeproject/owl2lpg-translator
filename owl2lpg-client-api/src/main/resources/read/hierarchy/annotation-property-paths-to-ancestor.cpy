MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument {ontologyDocumentId:$ontoDocId})
MATCH (o)<-[:IN_ONTOLOGY_SIGNATURE]-(child:AnnotationProperty {iri:$entityIri})
MATCH (o)<-[:IN_ONTOLOGY_SIGNATURE]-(parent:AnnotationProperty)
MATCH p=(child)-[:SUB_ANNOTATION_PROPERTY_OF*]->(parent)
WHERE NOT((parent)-[:SUB_ANNOTATION_PROPERTY_OF]->()) AND apoc.coll.duplicates(NODES(p)) = []
RETURN DISTINCT p