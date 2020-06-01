CALL {
   MATCH (document:OntologyDocument {ontologyDocumentId: $ontoDocId})-[:AXIOM]->(axiom:ClassAxiom)
   MATCH (entity:AnnotationProperty { iri: $subjectIri })-[:IS_SUBJECT_OF]->(axiom)
   OPTIONAL MATCH (entity)-[property:RELATED_TO]->(object)
   RETURN entity, property, object
   UNION
   MATCH (document:OntologyDocument {ontologyDocumentId: $ontoDocId})-[:AXIOM]->(annotation:AnnotationAssertion)
   MATCH (entity:AnnotationProperty)-[:ENTITY_IRI]->(:IRI { iri: $subjectIri })-[:IS_SUBJECT_OF]->(annotation)
   OPTIONAL MATCH (iri)-[property:RELATED_TO]->(object)
   RETURN entity, property, object
}
OPTIONAL MATCH (entity)-[:SUB_ANNOTATION_PROPERTY_OF]->(parent:AnnotationProperty)
OPTIONAL MATCH (entity)-[:DOMAIN]->(domain:IRI)
OPTIONAL MATCH (entity)-[:RANGE]->(range:IRI)

RETURN { type: "AnnotationPropertyFrame",
         subject: { type: "owl:AnnotationProperty", iri: entity.iri },
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
         domains: COLLECT(DISTINCT( CASE WHEN domain IS NOT NULL THEN domain.iri END )),
         ranges: COLLECT(DISTINCT( CASE WHEN range IS NOT NULL THEN range.iri END ))
       } AS result