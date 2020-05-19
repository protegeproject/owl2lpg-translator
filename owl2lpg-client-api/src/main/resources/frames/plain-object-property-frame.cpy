CALL {
   MATCH (project)-[:BRANCH]->(branch)-[:ONTOLOGY_DOCUMENT]->(document)-[:AXIOM]->(axiom:ObjectPropertyAxiom)
   MATCH (entity:ObjectProperty { iri: $subjectIri })-[:IS_SUBJECT_OF]->(axiom)
   WHERE project.projectId = $projectId
   AND branch.branchId = $branchId
   AND document.ontologyDocumentId = $ontoDocId
   RETURN entity
   UNION
   MATCH (project)-[:BRANCH]->(branch)-[:ONTOLOGY_DOCUMENT]->(document)-[:AXIOM]->(annotation:AnnotationAssertion)
   MATCH (entity)-[:ENTITY_IRI]->(:IRI { iri: $subjectIri })-[:IS_SUBJECT_OF]->(annotation)
   WHERE project.projectId = $projectId
   AND branch.branchId = $branchId
   AND document.ontologyDocumentId = $ontoDocId
   RETURN entity
}
CALL {
   MATCH (project)-[:BRANCH]->(branch)-[:ONTOLOGY_DOCUMENT]->(document)-[:AXIOM]->(axiom:ObjectPropertyAxiom)
   MATCH (entity:ObjectProperty { iri: $subjectIri })<-[:OBJECT_PROPERTY_EXPRESSION]-(axiom)
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
   RETURN COLLECT(characteristic) AS characteristics
}
OPTIONAL MATCH (entity)-[:SUB_OBJECT_PROPERTY_OF]->(parent:ObjectProperty)
OPTIONAL MATCH (entity)-[:DOMAIN]->(domain:Class)
OPTIONAL MATCH (entity)-[:RANGE]->(range:Class)
OPTIONAL MATCH (entity)-[:INVERSE_OF]->(inverse_property:ObjectProperty)
OPTIONAL MATCH (entity)-[property:RELATED_TO]->(object)

RETURN { type: "ObjectPropertyFrame",
         subject: { type: "owl:ObjectProperty", iri: entity.iri },
         propertyValues:
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
         inverses: COLLECT(DISTINCT( CASE WHEN inverse_property IS NOT NULL THEN { type: "owl:ObjectProperty", iri: inverse_property.iri } END ))
       } AS result