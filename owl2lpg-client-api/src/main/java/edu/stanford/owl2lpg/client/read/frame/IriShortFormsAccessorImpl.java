package edu.stanford.owl2lpg.client.read.frame;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import edu.stanford.bmir.protege.web.shared.shortform.ShortForm;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.semanticweb.owlapi.model.IRI;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class IriShortFormsAccessorImpl implements IriShortFormsAccessor {

  @Nonnull
  private final Session session;

  @Nonnull
  private final ObjectMapper mapper;

  @Inject
  public IriShortFormsAccessorImpl(@Nonnull Session session,
                                   @Nonnull ObjectMapper mapper) {
    this.session = checkNotNull(session);
    this.mapper = checkNotNull(mapper);
  }

  @Override
  public Multimap<IRI, ShortForm> getDictionaryMap(@Nonnull ImmutableList<IRI> entityIris,
                                                   @Nonnull ImmutableList<IRI> annotationPropertyIris) {
    var args = Parameters.forShortForm(entityIris, annotationPropertyIris);
    return session.readTransaction(tx ->
        tx.run(FrameQueries.SHORT_FORMS_QUERY, args)
            .stream()
            .findFirst()
            .map(record -> getResultMap(record)
                .stream()
                .map(mapItem -> Maps.immutableEntry(
                    getEntityIri(mapItem),
                    getShortForm(mapItem)))
                .collect(HashMultimap::<IRI, ShortForm>create,
                    (map, entry) -> map.put(entry.getKey(), entry.getValue()),
                    Multimap::putAll))
            .orElse(HashMultimap.create()));
  }

  private List<Map<String, Object>> getResultMap(Record record) {
    return (List<Map<String, Object>>) record.asMap().get("result");
  }

  private IRI getEntityIri(Map<String, Object> mapItem) {
    return IRI.create(mapItem.get("iri").toString());
  }

  private ShortForm getShortForm(Map<String, Object> mapItem) {
    return mapper.convertValue(mapItem.get("shortForm"), ShortForm.class);
  }
}
