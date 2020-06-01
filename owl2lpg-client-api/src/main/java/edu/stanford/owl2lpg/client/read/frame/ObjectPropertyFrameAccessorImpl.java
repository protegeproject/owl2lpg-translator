package edu.stanford.owl2lpg.client.read.frame;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import edu.stanford.bmir.protege.web.shared.frame.ObjectPropertyFrame;
import edu.stanford.bmir.protege.web.shared.frame.PlainObjectPropertyFrame;
import edu.stanford.bmir.protege.web.shared.frame.PropertyValue;
import edu.stanford.owl2lpg.model.AxiomContext;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLNamedObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ObjectPropertyFrameAccessorImpl implements ObjectPropertyFrameAccessor {

  @Nonnull
  private final PlainObjectPropertyFrameAccessor delegate;

  @Nonnull
  private final IriShortFormsAccessor shortFormsAccessor;

  @Nonnull
  private final SimpleFrameComponentRendererFactory rendererFactory;

  @Nonnull
  private final Comparator<PropertyValue> propertyValueComparator;

  @Inject
  public ObjectPropertyFrameAccessorImpl(@Nonnull PlainObjectPropertyFrameAccessor delegate,
                                         @Nonnull IriShortFormsAccessor shortFormsAccessor,
                                         @Nonnull SimpleFrameComponentRendererFactory rendererFactory,
                                         @Nonnull Comparator<PropertyValue> propertyValueComparator) {
    this.delegate = checkNotNull(delegate);
    this.shortFormsAccessor = checkNotNull(shortFormsAccessor);
    this.rendererFactory = checkNotNull(rendererFactory);
    this.propertyValueComparator = checkNotNull(propertyValueComparator);
  }

  @Nonnull
  @Override
  public Optional<ObjectPropertyFrame> getFrame(@Nonnull AxiomContext context,
                                                @Nonnull OWLObjectProperty subject) {
    return delegate.getFrame(context, subject)
        .map(this::toObjectPropertyFrame);
  }

  private ObjectPropertyFrame toObjectPropertyFrame(PlainObjectPropertyFrame plainFrame) {
    var rdfsLabel = ImmutableList.of(OWLRDFVocabulary.RDFS_LABEL.getIRI());
    var entityIris = getEntityIrisFrom(plainFrame);
    var dictionaryMap = shortFormsAccessor.getDictionaryMap(entityIris, rdfsLabel);
    var frameComponentRenderer = rendererFactory.get(dictionaryMap);
    return plainFrame.toEntityFrame(frameComponentRenderer, propertyValueComparator);
  }

  private ImmutableList<IRI> getEntityIrisFrom(PlainObjectPropertyFrame plainFrame) {
    var entityIriList = Lists.<IRI>newArrayList();
    entityIriList.add(plainFrame.getSubject().getIRI());
    entityIriList.addAll(
        plainFrame.getPropertyValues()
            .stream()
            .map(p -> p.getProperty().getIRI())
            .collect(Collectors.toList()));
    entityIriList.addAll(
        plainFrame.getDomains()
            .stream()
            .map(OWLNamedObject::getIRI)
            .collect(Collectors.toList()));
    entityIriList.addAll(
        plainFrame.getRanges()
            .stream()
            .map(OWLNamedObject::getIRI)
            .collect(Collectors.toList()));
    entityIriList.addAll(
        plainFrame.getInverseProperties()
            .stream()
            .map(OWLNamedObject::getIRI)
            .collect(Collectors.toList()));
    return ImmutableList.copyOf(entityIriList);
  }
}
