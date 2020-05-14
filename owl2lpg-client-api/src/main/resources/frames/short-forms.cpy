MATCH (entity:IRI)-[property:RELATED_TO {type:'AnnotationProperty'}]->(property_label:Literal)
WHERE entity.iri IN $entityIriList
AND property.iri IN $annotationPropertyIriList

RETURN COLLECT({
      iri: entity.iri,
      shortForm: {
          dictionaryLanguage: { propertyIri: property.iri, lang: property_label.language },
          shortForm: property_label.lexicalForm }
      }) AS result