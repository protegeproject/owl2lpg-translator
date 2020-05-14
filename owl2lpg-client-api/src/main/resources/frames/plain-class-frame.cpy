MATCH (project)-[:BRANCH]->(branch)-[:ONTOLOGY_DOCUMENT]->(document)-[:AXIOM]->(axiom)
MATCH (entity:Class)-[:IS_SUBJECT_OF]->(axiom)
MATCH (entity)-[:SUB_CLASS_OF]->(parent:Class)
MATCH (entity)-[object_property:RELATED_TO {type:'ObjectProperty'}]->(filler)
WHERE entity.iri = $subjectIri
AND project.projectId = $projectId
AND branch.branchId = $branchId
AND document.ontologyDocumentId = $ontoDocId

RETURN { type: "ClassFrame",
       subject: { type: "owl:Class", iri: entity.iri },
       parents: COLLECT(DISTINCT({ type: "owl:Class", iri: parent.iri })),
       propertyValues: COLLECT(DISTINCT({ type: "PropertyClassValue",
              property: { type: "owl:ObjectProperty", iri: object_property.iri },
              value: { type: "owl:Class", iri: filler.iri } })) } AS result