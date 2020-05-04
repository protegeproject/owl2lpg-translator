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
  private final StorerFactory storerFactory;

  public DataStorer(@Nonnull Database database,
                    @Nonnull DatabaseConnection connection,
                    @Nonnull StorerFactory storerFactory) {
    this.database = checkNotNull(database);
    this.connection = checkNotNull(connection);
    this.storerFactory = checkNotNull(storerFactory);
  }

  public boolean add(AxiomContext context, Collection<OWLAxiom> axioms) {
    var storer = storerFactory.getStorer(OWLAxiom.class, Mode.CYPHER);
    return axioms.stream()
        .map(axiom -> storer
            .addParameter(context)
            .addParameter(axiom)
            .store())
        .reduce(Boolean::logicalAnd)
        .orElse(false);
  }

  @Override
  public void close() throws Exception {
    connection.close();
  }
}
