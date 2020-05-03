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
public class AxiomStorer implements AutoCloseable {

  @Nonnull
  private final Database database;

  @Nonnull
  private final DatabaseConnection connection;

  @Nonnull
  private final AxiomToCypherQuery translator;

  public AxiomStorer(@Nonnull Database database,
                     @Nonnull DatabaseConnection connection,
                     @Nonnull AxiomToCypherQuery translator) {
    this.database = checkNotNull(database);
    this.connection = checkNotNull(connection);
    this.translator = checkNotNull(translator);
  }

  public boolean add(AxiomContext context, Collection<OWLAxiom> axioms) {
    return axioms.stream()
        .map(axiom -> AxiomBundle.create(context, axiom))
        .map(translator::translate)
        .map(connection::createStatement)
        .map(database::run)
        .reduce(Boolean::logicalAnd)
        .orElse(false);
  }

  @Override
  public void close() throws Exception {
    connection.close();
  }
}
