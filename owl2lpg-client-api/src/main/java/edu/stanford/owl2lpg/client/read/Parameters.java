package edu.stanford.owl2lpg.client.read;

import edu.stanford.bmir.protege.web.server.shortform.SearchString;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.neo4j.driver.Value;
import org.neo4j.driver.internal.value.MapValue;
import org.neo4j.driver.internal.value.StringValue;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.NodeID;
import org.semanticweb.owlapi.model.OWLLiteral;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Parameters {

  private static final String PROJECT_ID = "projectId";
  private static final String BRANCH_ID = "branchId";
  private static final String ONTO_DOC_ID = "ontoDocId";
  private static final String ENTITY_IRI = "entityIri";
  private static final String ENTITY_NAME = "entityName";
  private static final String NODE_ID = "nodeId";
  private static final String SEARCH_STRING = "searchString";
  private static final String ENTITY_TYPE = "entityType";
  private static final String AXIOM_TYPE = "axiomType";
  private static final String CHARACTERISTIC_TYPE = "characteristicType";
  private static final String LEXICAL_FORM = "lexicalForm";
  private static final String DATATYPE = "datatype";
  private static final String IRI = "iri";

  public static Value forContext(@Nonnull ProjectId projectId,
                                 @Nonnull BranchId branchId) {
    return new MapValue(Map.of(
        PROJECT_ID, new StringValue(projectId.getIdentifier()),
        BRANCH_ID, new StringValue(branchId.getIdentifier())));
  }

  public static Value forContext(@Nonnull ProjectId projectId,
                                 @Nonnull BranchId branchId,
                                 @Nonnull OntologyDocumentId ontoDocId) {
    return new MapValue(Map.of(
        PROJECT_ID, new StringValue(projectId.getIdentifier()),
        BRANCH_ID, new StringValue(branchId.getIdentifier()),
        ONTO_DOC_ID, new StringValue(ontoDocId.getIdentifier())));
  }

  public static Value forEntityIri(@Nonnull IRI entityIri,
                                   @Nonnull ProjectId projectId,
                                   @Nonnull BranchId branchId,
                                   @Nonnull OntologyDocumentId ontologyDocumentId) {
    return new MapValue(Map.of(
        PROJECT_ID, new StringValue(projectId.getIdentifier()),
        BRANCH_ID, new StringValue(branchId.getIdentifier()),
        ONTO_DOC_ID, new StringValue(ontologyDocumentId.getIdentifier()),
        ENTITY_IRI, new StringValue(entityIri.toString())));
  }

  public static Value forEntityIri(@Nonnull IRI entityIri,
                                   @Nonnull ProjectId projectId,
                                   @Nonnull BranchId branchId) {
    return new MapValue(Map.of(
        PROJECT_ID, new StringValue(projectId.getIdentifier()),
        BRANCH_ID, new StringValue(branchId.getIdentifier()),
        ENTITY_IRI, new StringValue(entityIri.toString())));
  }

  public static Value forEntityName(@Nonnull String entityName,
                                    @Nonnull ProjectId projectId,
                                    @Nonnull BranchId branchId,
                                    @Nonnull OntologyDocumentId ontoDocId) {
    return new MapValue(Map.of(
        PROJECT_ID, new StringValue(projectId.getIdentifier()),
        BRANCH_ID, new StringValue(branchId.getIdentifier()),
        ONTO_DOC_ID, new StringValue(ontoDocId.getIdentifier()),
        ENTITY_NAME, new StringValue(entityName)));
  }

  public static Value forEntityName(@Nonnull String entityName,
                                    @Nonnull ProjectId projectId,
                                    @Nonnull BranchId branchId) {
    return new MapValue(Map.of(
        PROJECT_ID, new StringValue(projectId.getIdentifier()),
        BRANCH_ID, new StringValue(branchId.getIdentifier()),
        ENTITY_NAME, new StringValue(entityName)));
  }

  public static Value forEntityType(@Nonnull EntityType entityType,
                                    @Nonnull ProjectId projectId,
                                    @Nonnull BranchId branchId,
                                    @Nonnull OntologyDocumentId ontoDocId) {
    return new MapValue(Map.of(
        PROJECT_ID, new StringValue(projectId.getIdentifier()),
        BRANCH_ID, new StringValue(branchId.getIdentifier()),
        ONTO_DOC_ID, new StringValue(ontoDocId.getIdentifier()),
        ENTITY_TYPE, new StringValue(entityType.getName())));
  }

  public static Value forNodeId(@Nonnull NodeID nodeId,
                                @Nonnull ProjectId projectId,
                                @Nonnull BranchId branchId,
                                @Nonnull OntologyDocumentId ontoDocId) {
    return new MapValue(Map.of(
        PROJECT_ID, new StringValue(projectId.getIdentifier()),
        BRANCH_ID, new StringValue(branchId.getIdentifier()),
        ONTO_DOC_ID, new StringValue(ontoDocId.getIdentifier()),
        NODE_ID, new StringValue(nodeId.getID())));
  }

  public static Value forAxiomType(@Nonnull AxiomType axiomType,
                                   @Nonnull ProjectId projectId,
                                   @Nonnull BranchId branchId,
                                   @Nonnull OntologyDocumentId ontoDocId) {
    return new MapValue(Map.of(
        PROJECT_ID, new StringValue(projectId.getIdentifier()),
        BRANCH_ID, new StringValue(branchId.getIdentifier()),
        ONTO_DOC_ID, new StringValue(ontoDocId.getIdentifier()),
        AXIOM_TYPE, new StringValue(axiomType.getName())));
  }

  public static Value forPropertyWithCharacteristicType(@Nonnull IRI propertyIri,
                                                        @Nonnull String characteristicType,
                                                        @Nonnull ProjectId projectId,
                                                        @Nonnull BranchId branchId,
                                                        @Nonnull OntologyDocumentId ontoDocId) {
    return new MapValue(Map.of(
        PROJECT_ID, new StringValue(projectId.getIdentifier()),
        BRANCH_ID, new StringValue(branchId.getIdentifier()),
        ONTO_DOC_ID, new StringValue(ontoDocId.getIdentifier()),
        ENTITY_IRI, new StringValue(propertyIri.toString()),
        CHARACTERISTIC_TYPE, new StringValue(characteristicType)));
  }

  public static Value forLiteral(@Nonnull OWLLiteral literal,
                                 @Nonnull ProjectId projectId,
                                 @Nonnull BranchId branchId,
                                 @Nonnull OntologyDocumentId ontoDocId) {
    return new MapValue(Map.of(
        PROJECT_ID, new StringValue(projectId.getIdentifier()),
        BRANCH_ID, new StringValue(branchId.getIdentifier()),
        ONTO_DOC_ID, new StringValue(ontoDocId.getIdentifier()),
        LEXICAL_FORM, new StringValue(literal.getLiteral()),
        DATATYPE, new StringValue(literal.getDatatype().getIRI().toString())));
  }

  public static Value forValueIri(IRI iri,
                                  ProjectId projectId,
                                  BranchId branchId,
                                  OntologyDocumentId ontoDocId) {
    return new MapValue(Map.of(
        PROJECT_ID, new StringValue(projectId.getIdentifier()),
        BRANCH_ID, new StringValue(branchId.getIdentifier()),
        ONTO_DOC_ID, new StringValue(ontoDocId.getIdentifier()),
        IRI, new StringValue(iri.toString())));
  }

  public static Value forSearchStrings(@Nonnull List<SearchString> searchStrings,
                                       @Nonnull ProjectId projectId,
                                       @Nonnull BranchId branchId) {
    return new MapValue(Map.of(
        PROJECT_ID, new StringValue(projectId.getIdentifier()),
        BRANCH_ID, new StringValue(branchId.getIdentifier()),
        SEARCH_STRING, new StringValue(searchStrings.stream()
            .map(SearchString::getSearchString)
            .map(s -> s + "*")
            .collect(joining(" AND ")))));
  }
}
