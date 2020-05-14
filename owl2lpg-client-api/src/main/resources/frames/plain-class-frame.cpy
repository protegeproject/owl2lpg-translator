MATCH (project)-[:BRANCH]->(branch)-[:ONTOLOGY_DOCUMENT]->(document)-[:AXIOM]->(axiom)
MATCH (entityIri:IRI)<-[:ENTITY_IRI]-(entity:Class)-[:IS_SUBJECT_OF]->(axiom)
MATCH (entity)-[:SUB_CLASS_OF]->(parent:Class)
MATCH (entity)-[property:RELATED_TO]->(filler)
MATCH (entityIri)-[annotation_property:RELATED_TO {type:'AnnotationProperty'}]->(value)
WHERE entity.iri = $subjectIri
AND entityIri.iri = $subjectIri
AND project.projectId = $projectId
AND branch.branchId = $branchId
AND document.ontologyDocumentId = $ontoDocId

RETURN { type: "ClassFrame",
       subject: { type: "owl:Class", iri: entity.iri },
       parents: COLLECT(DISTINCT({ type: "owl:Class", iri: parent.iri })),
       propertyValues:
       COLLECT(DISTINCT(
          CASE WHEN 'Class' IN LABELS(filler) THEN
             { type: "PropertyClassValue",
               property: { type: "owl:ObjectProperty", iri: property.iri },
               value: { type: "owl:Class", iri: filler.iri }
             }
          WHEN 'NamedIndividual' IN LABELS(filler) THEN
             { type: "PropertyClassValue",
               property: { type: "owl:ObjectProperty", iri: property.iri },
               value: { type: "owl:NamedIndividual", iri: filler.iri }
             }
          WHEN 'Datatype' IN LABELS(filler) THEN
             { type: "PropertyClassValue",
               property: { type: "owl:DataProperty", iri: property.iri },
               value: { type: "owl:Datatype", iri: filler.iri }
             }
          WHEN 'Literal' IN LABELS(filler) THEN
             { type: "PropertyClassValue",
               property: { type: "owl:DataProperty", iri: property.iri },
               value: { type: filler.datatype, value: filler.lexicalForm, lang: filler.language }
             }
          END
       )) +
       COLLECT(DISTINCT(
          CASE WHEN 'Literal' IN LABELS(value) THEN
             { type: "PropertyAnnotationValue",
               property: { type: "owl:AnnotationProperty", iri: annotation_property.iri },
               value: { type: value.datatype, value: value.lexicalForm, lang: value.language }
             }
          WHEN 'IRI' IN LABELS(value) THEN
             { type: "PropertyAnnotationValue",
               property: { type: "owl:AnnotationProperty", iri: annotation_property.iri },
               value: { iri: value.iri }
             }
          END
       ))} AS result