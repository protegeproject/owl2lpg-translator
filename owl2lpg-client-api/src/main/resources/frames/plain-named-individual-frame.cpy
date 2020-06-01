CALL {
   MATCH (document:OntologyDocument {ontologyDocumentId: $ontoDocId})-[:AXIOM]->(axiom:ClassAxiom)
   MATCH (entity:NamedIndividual { iri: $subjectIri })-[:IS_SUBJECT_OF]->(axiom)
   OPTIONAL MATCH (entity)-[property:RELATED_TO]->(object)
   RETURN entity, property, object
   UNION
   MATCH (document:OntologyDocument {ontologyDocumentId: $ontoDocId})-[:AXIOM]->(annotation:AnnotationAssertion)
   MATCH (entity:NamedIndividual)-[:ENTITY_IRI]->(:IRI { iri: $subjectIri })-[:IS_SUBJECT_OF]->(annotation)
   OPTIONAL MATCH (iri)-[property:RELATED_TO]->(object)
   RETURN entity, property, object
}
OPTIONAL MATCH (entity)-[:TYPE]->(parent:Class)
OPTIONAL MATCH (entity)-[:SAME_INDIVIDUAL]->(other:NamedIndividual)

RETURN { type: "IndividualFrame",
       subject: { type: "owl:NamedIndividual", iri: entity.iri },
       parents: COLLECT(DISTINCT( CASE WHEN parent IS NOT NULL THEN { type: "owl:Class", iri: parent.iri } END )),
       propertyValues:
       COLLECT(DISTINCT( CASE WHEN object IS NOT NULL THEN
          CASE WHEN 'NamedIndividual' IN LABELS(object) AND property.type = 'ObjectProperty' THEN
             { type: "PropertyIndividualValue",
               property: { type: "owl:ObjectProperty", iri: property.iri },
               value: { type: "owl:NamedIndividual", iri: object.iri }
             }
          WHEN 'Literal' IN LABELS(object) AND property.type = 'DataProperty' THEN
             { type: "PropertyLiteralValue",
               property: { type: "owl:DataProperty", iri: property.iri },
               value:
               CASE WHEN object.datatype = 'http://www.w3.org/1999/02/22-rdf-syntax-ns#PlainLiteral' THEN
                  { value: object.lexicalForm, lang: object.language }
               ELSE
                  { type: object.datatype, value: object.lexicalForm }
               END
             }
          WHEN 'Literal' IN LABELS(object) AND property.type = 'AnnotationProperty' THEN
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
       sameIndividual: COLLECT(DISTINCT( CASE WHEN other IS NOT NULL THEN { type: "owl:NamedIndividual", iri: other.iri } END ))
    } AS result