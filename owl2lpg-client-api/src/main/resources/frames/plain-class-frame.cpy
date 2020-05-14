MATCH (project)-[:BRANCH]->(branch)-[:ONTOLOGY_DOCUMENT]->(document)-[:AXIOM]->(axiom)
MATCH (entity:Class)-[:IS_SUBJECT_OF]->(axiom)
MATCH (entity)-[:SUB_CLASS_OF]->(parent:Class)
MATCH (entity)-[property:RELATED_TO]->(object)
WHERE entity.iri = 'http://purl.obolibrary.org/obo/CIDO_0000002'
AND project.projectId = 'c5d78f23-5992-4a9b-9a36-9567059209b0'
AND branch.branchId = 'b6dbf7a0-79cc-4776-afd8-7d1502fc63a6'
AND document.ontologyDocumentId = 'd6d0a42c-fe98-424f-9791-65cea5ef8261'


RETURN { type: "ClassFrame",
       subject: { type: "owl:Class", iri: entity.iri },
       parents: COLLECT(DISTINCT({ type: "owl:Class", iri: parent.iri })),
       propertyValues:
       COLLECT(DISTINCT(
          CASE WHEN 'Class' IN LABELS(object) AND property.type = 'ObjectProperty' THEN
             { type: "PropertyClassValue",
               property: { type: "owl:ObjectProperty", iri: property.iri },
               value: { type: "owl:Class", iri: object.iri }
             }
          WHEN 'NamedIndividual' IN LABELS(object) AND property.type = 'ObjectProperty' THEN
             { type: "PropertyClassValue",
               property: { type: "owl:ObjectProperty", iri: property.iri },
               value: { type: "owl:NamedIndividual", iri: object.iri }
             }
          WHEN 'Datatype' IN LABELS(object) AND property.type = 'DataProperty' THEN
             { type: "PropertyClassValue",
               property: { type: "owl:DataProperty", iri: property.iri },
               value: { type: "owl:Datatype", iri: object.iri }
             }
          WHEN 'Literal' IN LABELS(object) AND property.type = 'DataProperty' THEN
             { type: "PropertyClassValue",
               property: { type: "owl:DataProperty", iri: property.iri },
               value: { type: object.datatype, value: object.lexicalForm, lang: object.language }
             }
          WHEN 'Literal' IN LABELS(object) AND property.type = 'AnnotationProperty' THEN
             { type: "PropertyAnnotationValue",
               property: { type: "owl:AnnotationProperty", iri: property.iri },
               value: { type: object.datatype, value: object.lexicalForm, lang: object.language }
             }
          WHEN 'IRI' IN LABELS(object) AND property.type = 'AnnotationProperty' THEN
             { type: "PropertyAnnotationValue",
               property: { type: "owl:AnnotationProperty", iri: property.iri },
               value: { iri: object.iri }
             }
          END
       ))} AS result