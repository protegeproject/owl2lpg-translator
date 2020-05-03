package edu.stanford.owl2lpg.client.write;

import edu.stanford.owl2lpg.client.Database;
import edu.stanford.owl2lpg.client.DatabaseConnection;
import edu.stanford.owl2lpg.versioning.model.AxiomContext;
import org.semanticweb.owlapi.model.OWLAxiom;

import javax.annotation.Nonnull;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class DataStorer implements AutoCloseable {

  @Nonnull
  private final Database database;

  @Nonnull
  private final DatabaseConnection connection;

  @Nonnull
  private final AxiomDataStorer axiomDataStorer;

  public DataStorer(@Nonnull Database database,
                    @Nonnull DatabaseConnection connection,
                    @Nonnull AxiomDataStorer axiomDataStorer) {
    this.database = checkNotNull(database);
    this.connection = checkNotNull(connection);
    this.axiomDataStorer = checkNotNull(axiomDataStorer);
  }

  public boolean add(AxiomContext context, Collection<OWLAxiom> axioms) {
    return axioms.stream()
        .map(axiom -> axiomDataStorer.storeAxiom(context, axiom))
        .reduce(Boolean::logicalAnd)
        .orElse(false);
  }

  @Override
  public void close() throws Exception {
    connection.close();
  }
}
