CALL {
  MATCH (project)-[:BRANCH]->(branch)-[:ONTOLOGY_DOCUMENT]->(document)-[:AXIOM]->(axiom:AnnotationAxiom)
  MATCH (entity:AnnotationProperty { iri: $subjectIri })-[:IS_SUBJECT_OF]->(axiom)
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
OPTIONAL MATCH (entity)-[:SUB_ANNOTATION_PROPERTY_OF]->(parent:AnnotationProperty)
OPTIONAL MATCH (entity)-[:DOMAIN]->(domain:IRI)
OPTIONAL MATCH (entity)-[:RANGE]->(range:IRI)
OPTIONAL MATCH (entity)-[property:RELATED_TO]->(object)

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