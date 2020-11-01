CREATE CONSTRAINT unique_project_id ON (n:Project) ASSERT n.projectId IS UNIQUE;
CREATE CONSTRAINT unique_branch_id ON (n:Branch) ASSERT n.branchId IS UNIQUE;
CREATE CONSTRAINT unique_document_id ON (n:OntologyDocument) ASSERT n.ontologyDocumentId IS UNIQUE;
CREATE CONSTRAINT unique_iri_iri ON (n:IRI) ASSERT n.iri IS UNIQUE;
CREATE CONSTRAINT unique_class_iri ON (n:Class) ASSERT n.iri IS UNIQUE;
CREATE CONSTRAINT unique_data_property_iri ON (n:DataProperty) ASSERT n.iri IS UNIQUE;
CREATE CONSTRAINT unique_object_property_iri ON (n:ObjectProperty) ASSERT n.iri IS UNIQUE;
CREATE CONSTRAINT unique_annotation_property_iri ON (n:AnnotationProperty) ASSERT n.iri IS UNIQUE;
CREATE CONSTRAINT unique_datatype_iri ON (n:Datatype) ASSERT n.iri IS UNIQUE;
CREATE CONSTRAINT unique_individual_iri ON (n:NamedIndividual) ASSERT n.iri IS UNIQUE;
CREATE CONSTRAINT unique_axiom_digest ON (n:Axiom) ASSERT n.digest IS UNIQUE;
CREATE INDEX entity_iri_index FOR (n:Entity) ON (n.iri);
CREATE INDEX entity_iriSuffix_index FOR (n:Entity) ON (n.iriSuffix);
CREATE INDEX class_iriSuffix_index FOR (n:Class) ON (n.iriSuffix);
CREATE INDEX data_property_iriSuffix_index FOR (n:DataProperty) ON (n.iriSuffix);
CREATE INDEX object_property_iriSuffix_index FOR (n:ObjectProperty) ON (n.iriSuffix);
CREATE INDEX annotation_property_iriSuffix_index FOR (n:AnnotationProperty) ON (n.iriSuffix);
CREATE INDEX datatype_iriSuffix_index FOR (n:Datatype) ON (n.iriSuffix);
CREATE INDEX individual_iriSuffix_index FOR (n:NamedIndividual) ON (n.iriSuffix);
CREATE INDEX entity_oboId_index FOR (n:Entity) ON (n.oboId);
CREATE INDEX class_oboId_index FOR (n:Class) ON (n.oboId);
CREATE INDEX data_property_oboId_index FOR (n:DataProperty) ON (n.oboId);
CREATE INDEX object_property_oboId_index FOR (n:ObjectProperty) ON (n.oboId);
CREATE INDEX annotation_property_oboId_index FOR (n:AnnotationProperty) ON (n.oboId);
CREATE INDEX datatype_oboId_index FOR (n:Datatype) ON (n.oboId);
CREATE INDEX individual_oboId_index FOR (n:NamedIndividual) ON (n.oboId);
CREATE INDEX literal_lexicalForm_index FOR (n:Literal) ON (n.lexicalForm);
CREATE INDEX literal_datatype_index FOR (n:Literal) ON (n.datatype);
CREATE INDEX literal_language_index FOR (n:Literal) ON (n.language);