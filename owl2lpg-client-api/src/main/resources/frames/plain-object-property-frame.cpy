MATCH (project)-[:BRANCH]->(branch)-[:ONTOLOGY_DOCUMENT]->(document)-[:AXIOM]->(axiom:ObjectPropertyAxiom)
MATCH (axiom)-[:OBJECT_PROPERTY_EXPRESSION]->(entity:ObjectProperty { iri: $subjectIri })
WHERE project.projectId = $projectId
AND branch.branchId = $branchId
AND document.ontologyDocumentId = $ontoDocId
WITH DISTINCT LABELS(axiom) as axiomLabels
WITH
   CASE WHEN 'FunctionalObjectProperty' IN axiomLabels THEN 'Functional'
   WHEN 'InverseFunctionalObjectProperty' IN axiomLabels THEN 'InverseFunctional'
   WHEN 'ReflexiveObjectProperty' IN axiomLabels THEN 'Reflexive'
   WHEN 'IrreflexiveObjectProperty' IN axiomLabels THEN 'Irreflexive'
   WHEN 'SymmetricObjectProperty' IN axiomLabels THEN 'Symmetric'
   WHEN 'AsymmetricObjectProperty' IN axiomLabels THEN 'Asymmetric'
   WHEN 'TransitiveObjectProperty' IN axiomLabels THEN 'Transitive'
   END as characteristic
WITH COLLECT(characteristic) AS characteristics

MATCH (project)-[:BRANCH]->(branch)-[:ONTOLOGY_DOCUMENT]->(document)-[:AXIOM]->(axiom)
MATCH (entity:ObjectProperty { iri: $subjectIri })-[:IS_SUBJECT_OF]->(axiom)
OPTIONAL MATCH (entity)-[:SUB_OBJECT_PROPERTY_OF]->(parent:ObjectProperty)
OPTIONAL MATCH (entity)-[:DOMAIN]->(domain:Class)
OPTIONAL MATCH (entity)-[:RANGE]->(range:Class)
OPTIONAL MATCH (entity)-[:INVERSE_OF]->(inverse_property:ObjectProperty)
OPTIONAL MATCH (entity)-[property:RELATED_TO]->(object)
WHERE project.projectId = $projectId
AND branch.branchId = $branchId
AND document.ontologyDocumentId = $ontoDocId

RETURN { type: "ObjectPropertyFrame",
         subject: { type: "owl:ObjectProperty", iri: entity.iri },
         annotationValues:
         COLLECT(DISTINCT( CASE WHEN object IS NOT NULL THEN
            CASE WHEN 'Literal' IN LABELS(object) AND property.type = 'AnnotationProperty' THEN
               { type: "PropertyAnnotationValue",
                 property: { type: "owl:AnnotationProperty", iri: property.iri },
                 value:
                 CASE WHEN object.datatype = 'http://www.w3.org/1999/02/22-rdf-syntax-ns#PlainLiteral' THEN
                    { value: object.lexicalForm, lang: object.language }
                 ELSE
                    { type: object.datatype, value: object.lexicalForm }
                 END
               }
            WHEN 'IRI' IN LABELS(object) AND property.type = 'AnnotationProperty' THEN
               { type: "PropertyAnnotationValue",
                 property: { type: "owl:AnnotationProperty", iri: property.iri },
                 value: object.iri
               }
            END
         END )),
         characteristics: characteristics,
         domains: COLLECT(DISTINCT( CASE WHEN domain IS NOT NULL THEN { type: "owl:Class", iri: domain.iri } END )),
         ranges: COLLECT(DISTINCT( CASE WHEN range IS NOT NULL THEN { type: "owl:Class", iri: range.iri } END )),
         inverses: COLLECT(DISTINCT( CASE WHEN inverse_property IS NOT NULL THEN { type: "owl:ObjectPoperty", iri: inverse_property.iri } END ))
       } AS result