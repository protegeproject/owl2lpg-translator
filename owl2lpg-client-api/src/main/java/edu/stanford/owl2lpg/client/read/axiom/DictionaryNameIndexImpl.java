package edu.stanford.owl2lpg.client.read.axiom;

import com.google.common.collect.*;
import edu.stanford.bmir.protege.web.shared.shortform.DictionaryLanguage;
import edu.stanford.bmir.protege.web.shared.shortform.ShortForm;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabel;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;
import org.neo4j.driver.types.Relationship;
import org.semanticweb.owlapi.model.IRI;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.NoSuchElementException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class DictionaryNameIndexImpl implements DictionaryNameIndex {

  @Nonnull
  private final ImmutableSetMultimap<IRI, ShortForm> nameIndex;

  public DictionaryNameIndexImpl(@Nonnull ImmutableSetMultimap<IRI, ShortForm> nameIndex) {
    this.nameIndex = checkNotNull(nameIndex);
  }

  @Override
  public Collection<ShortForm> getShortForms(IRI entityIri) {
    return nameIndex.get(entityIri);
  }

  @Override
  public Collection<String> getDisplayNames(IRI entityIri, IRI annotationPropertyIri, String language) {
    return nameIndex.get(entityIri)
        .stream()
        .filter(shortForm -> shortForm.getDictionaryLanguage().matches(annotationPropertyIri, language))
        .map(ShortForm::getShortForm)
        .collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public String getFirstDisplayName(IRI entityIri, IRI annotationPropertyIri, String language) {
    return getDisplayNames(entityIri, annotationPropertyIri, language)
        .stream()
        .findFirst()
        .orElseThrow(() -> new NoSuchElementException(
            "No display name for " + entityIri + " present " +
                "using [" + annotationPropertyIri + "] " +
                "in language [" + language + "]."));
  }

  @Override
  public Multimap<IRI, ShortForm> asMultimap() {
    return nameIndex;
  }

  public static class Builder {

    private final SetMultimap<IRI, ShortForm> mutableNameIndex = HashMultimap.create();

    public Builder add(Path.Segment segment) {
      var startNode = segment.start();
      var edge = segment.relationship();
      var endNode = segment.end();
      if (startNode.hasLabel(NodeLabels.IRI.getMainLabel())
          && edge.hasType(EdgeLabel.RELATED_TO.name())
          && endNode.hasLabel(NodeLabels.LITERAL.getMainLabel())) {
        add(startNode, edge, endNode);
      }
      return this;
    }

    private void add(Node iriNode, Relationship relatedToEdge, Node literalNode) {
      var entityIri = IRI.create(iriNode.get(PropertyFields.IRI).asString());
      var propertyIri = IRI.create(relatedToEdge.get(PropertyFields.IRI).asString());
      var language = literalNode.hasLabel(PropertyFields.LANGUAGE)
          ? literalNode.get(PropertyFields.LANGUAGE).asString()
          : "";
      var dictionaryLanguage = DictionaryLanguage.create(propertyIri, language);
      var literalValue = literalNode.get(PropertyFields.LEXICAL_FORM).asString();
      var shortForm = ShortForm.get(dictionaryLanguage, literalValue);
      mutableNameIndex.put(entityIri, shortForm);
    }

    public DictionaryNameIndex build() {
      return new DictionaryNameIndexImpl(
          ImmutableSetMultimap.copyOf(mutableNameIndex));
    }
  }
}
