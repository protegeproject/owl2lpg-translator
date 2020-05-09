package edu.stanford.owl2lpg.client.read.frame;

import edu.stanford.owl2lpg.versioning.model.AxiomContext;
import org.neo4j.driver.Value;
import org.neo4j.driver.internal.value.MapValue;
import org.neo4j.driver.internal.value.StringValue;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Parameters {

  public static Value forSubject(@Nonnull AxiomContext context, @Nonnull OWLEntity subject) {
    return new MapValue(Map.of(
        "subjectIri", new StringValue(subject.getIRI().toString()),
        "projectId", new StringValue(context.getProjectId().toString()),
        "branchId", new StringValue(context.getBranchId().toString()),
        "ontoDocId", new StringValue(context.getOntologyDocumentId().toString())));
  }
}
