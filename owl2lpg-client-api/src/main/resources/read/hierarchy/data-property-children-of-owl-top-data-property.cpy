MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch {branchId:$branchId})-[:ONTOLOGY_DOCUMENT]->(o:OntologyDocument {ontologyDocumentId:$ontoDocId})
MATCH (o)<-[:ENTITY_SIGNATURE_OF]-(n:DataProperty)
WHERE (n)-[:SUB_DATA_PROPERTY_OF]->(:DataProperty {iri:"http://www.w3.org/2002/07/owl#topDataProperty"})
   OR (NOT((n)-[:SUB_DATA_PROPERTY_OF]->()) AND n.iri <> "http://www.w3.org/2002/07/owl#topDataProperty")
RETURN DISTINCT n