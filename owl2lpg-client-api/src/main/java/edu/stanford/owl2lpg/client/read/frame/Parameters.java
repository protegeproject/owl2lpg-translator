package edu.stanford.owl2lpg.client.read.frame;

import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.model.AxiomContext;
import org.neo4j.driver.Value;
import org.neo4j.driver.internal.value.ListValue;
import org.neo4j.driver.internal.value.MapValue;
import org.neo4j.driver.internal.value.StringValue;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Parameters {

  public static Value forEntityIri(@Nonnull AxiomContext context, @Nonnull IRI entityIri) {
    return new MapValue(Map.of(
        "entityIri", new StringValue(entityIri.toString()),
        "projectId", new StringValue(context.getProjectId().toString()),
        "branchId", new StringValue(context.getBranchId().toString()),
        "ontoDocId", new StringValue(context.getOntologyDocumentId().getIdentifier())));
  }

  public static Value forSubject(@Nonnull AxiomContext context, @Nonnull OWLEntity subject) {
    return new MapValue(Map.of(
        "subjectIri", new StringValue(subject.getIRI().toString()),
        "projectId", new StringValue(context.getProjectId().toString()),
        "branchId", new StringValue(context.getBranchId().toString()),
        "ontoDocId", new StringValue(context.getOntologyDocumentId().getIdentifier())));
  }

  public static MapValue forShortForm(@Nonnull ImmutableList<IRI> entities,
                                      @Nonnull ImmutableList<IRI> annotationProperties) {
    return new MapValue(Map.of(
        "entityIriList", toListValue(entities),
        "annotationPropertyIriList", toListValue(annotationProperties)));
  }

  public static MapValue forShortForm(@Nonnull IRI entity,
                                      @Nonnull ImmutableList<IRI> annotationProperties) {
    return forShortForm(ImmutableList.of(entity), annotationProperties);
  }

  private static ListValue toListValue(ImmutableList<IRI> list) {
    var values = list
        .stream()
        .map(IRI::toString)
        .map(StringValue::new)
        .toArray(Value[]::new);
    return new ListValue(values);
  }
}
