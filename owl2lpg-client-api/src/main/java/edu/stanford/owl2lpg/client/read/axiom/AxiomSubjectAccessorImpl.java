package edu.stanford.owl2lpg.client.read.axiom;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import edu.stanford.owl2lpg.client.read.frame.Parameters;
import edu.stanford.owl2lpg.model.AxiomContext;
import org.neo4j.driver.Session;
import org.neo4j.driver.types.Path;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.client.read.axiom.AxiomQueries.AXIOM_SUBJECT_QUERY;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.AXIOM;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AxiomSubjectAccessorImpl implements AxiomSubjectAccessor {

  @Nonnull
  private final Session session;

  @Nonnull
  private final NodeMapper nodeMapper;

  private final
  Table<AxiomContext, OWLClass, IndexBundle> indexBundleCache = HashBasedTable.create();

  @Inject
  public AxiomSubjectAccessorImpl(@Nonnull Session session,
                                  @Nonnull NodeMapper nodeMapper) {
    this.session = checkNotNull(session);
    this.nodeMapper = checkNotNull(nodeMapper);
  }

  @Override
  public AxiomSubject getAxiomSubject(AxiomContext context, OWLClass subject) {
    var indexBundle = getIndexBundle(context, subject);
    var nodeIndex = indexBundle.getNodeIndex();
    var axioms = nodeIndex.getNodes(AXIOM.getMainLabel())
        .stream()
        .map(axiomNode -> nodeMapper.toObject(axiomNode, nodeIndex, OWLAxiom.class))
        .collect(Collectors.toSet());
    var dictionaryNameIndex = indexBundle.getDictionaryNameIndex();
    return AxiomSubject.create(axioms, dictionaryNameIndex);
  }

  private IndexBundle getIndexBundle(AxiomContext context, OWLClass subject) {
    var indexBundle = indexBundleCache.get(context, subject);
    if (indexBundle == null) {
      indexBundle = buildAxiomSubjectBundle(context, subject);
      indexBundleCache.put(context, subject, indexBundle);
    }
    return indexBundle;
  }

  private IndexBundle buildAxiomSubjectBundle(AxiomContext context, OWLClass subject) {
    var args = Parameters.forSubject(context, subject);
    return session.readTransaction(tx -> {
      var result = tx.run(AXIOM_SUBJECT_QUERY, args);
      var nodeIndexBuilder = new NodeIndexImpl.Builder();
      var dictionaryNameIndexBuilder = new DictionaryNameIndexImpl.Builder();
      while (result.hasNext()) {
        var row = result.next().asMap();
        for (var column : row.entrySet()) {
          if (column.getKey().equals("p")) {
            var path = (Path) column.getValue();
            if (path != null) {
              path.spliterator().forEachRemaining(nodeIndexBuilder::add);
            }
          }
          if (column.getKey().equals("q")) {
            var path = (Path) column.getValue();
            if (path != null) {
              path.spliterator().forEachRemaining(dictionaryNameIndexBuilder::add);
            }
          }
        }
      }
      return IndexBundle.create(nodeIndexBuilder.build(), dictionaryNameIndexBuilder.build());
    });
  }
}
