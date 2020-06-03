CALL {
   MATCH (document:OntologyDocument {ontologyDocumentId: $ontoDocId})-[:AXIOM]->(axiom:ClassAxiom)
   MATCH (entity:DataProperty { iri: $subjectIri })<-[:AXIOM_SUBJECT]-(axiom)
   OPTIONAL MATCH (entity)-[property:RELATED_TO]->(object)
   RETURN entity, property, object
   UNION
   MATCH (document:OntologyDocument {ontologyDocumentId: $ontoDocId})-[:AXIOM]->(annotation:AnnotationAssertion)
   MATCH (entity:DataProperty)-[:ENTITY_IRI]->(:IRI { iri: $subjectIri })<-[:AXIOM_SUBJECT]-(annotation)
   OPTIONAL MATCH (iri)-[property:RELATED_TO]->(object)
   RETURN entity, property, object
}
CALL {
   MATCH (project)-[:BRANCH]->(branch)-[:ONTOLOGY_DOCUMENT]->(document)-[:AXIOM]->(axiom:DataPropertyAxiom)
   MATCH (entity:DataProperty { iri: $subjectIri })<-[:DATA_PROPERTY_EXPRESSION]-(axiom)
   WHERE project.projectId = $projectId
   AND branch.branchId = $branchId
   AND document.ontologyDocumentId = $ontoDocId
   WITH DISTINCT LABELS(axiom) as axiomLabels
   WITH CASE WHEN 'FunctionalDataProperty' IN axiomLabels THEN 'Functional' END as characteristic
   RETURN COLLECT(characteristic) AS characteristics
}

OPTIONAL MATCH (entity)-[:SUB_DATA_PROPERTY_OF]->(parent:DataProperty)
OPTIONAL MATCH (entity)-[:DOMAIN]->(domain:Class)
OPTIONAL MATCH (entity)-[:RANGE]->(range:Datatype)

RETURN { type: "DataPropertyFrame",
         subject: { type: "owl:DataProperty", iri: entity.iri },
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
         functional: CASE WHEN 'Functional' IN characteristics THEN true ELSE false END,
         domains: COLLECT(DISTINCT( CASE WHEN domain IS NOT NULL THEN { type: "owl:Class", iri: domain.iri } END )),
         ranges: COLLECT(DISTINCT( CASE WHEN range IS NOT NULL THEN { type: "owl:Datatype", iri: range.iri } END ))
       } AS result