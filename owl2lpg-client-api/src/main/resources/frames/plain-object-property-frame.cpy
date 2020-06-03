CALL {
   MATCH (document:OntologyDocument {ontologyDocumentId: $ontoDocId})-[:AXIOM]->(axiom:ClassAxiom)
   MATCH (entity:ObjectProperty { iri: $subjectIri })<-[:AXIOM_SUBJECT]-(axiom)
   OPTIONAL MATCH (entity)-[property:RELATED_TO]->(object)
   RETURN entity, property, object
   UNION
   MATCH (document:OntologyDocument {ontologyDocumentId: $ontoDocId})-[:AXIOM]->(annotation:AnnotationAssertion)
   MATCH (entity:ObjectProperty)-[:ENTITY_IRI]->(:IRI { iri: $subjectIri })<-[:AXIOM_SUBJECT]-(annotation)
   OPTIONAL MATCH (iri)-[property:RELATED_TO]->(object)
   RETURN entity, property, object
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