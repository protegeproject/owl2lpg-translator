package edu.stanford.owl2lpg.client.read.axiom;

import com.google.common.collect.Multimap;
import edu.stanford.bmir.protege.web.shared.shortform.ShortForm;
import org.semanticweb.owlapi.model.IRI;

import java.util.Collection;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface DictionaryNameIndex {

  Collection<String> getDisplayNames(IRI entityIri, IRI annotationPropertyIri, String language);

  String getFirstDisplayName(IRI entityIri, IRI annotationPropertyIri, String language);

  Collection<ShortForm> getShortForms(IRI entityIri);

  Multimap<IRI, ShortForm> asMultimap();
}
