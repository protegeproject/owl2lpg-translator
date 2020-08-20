package edu.stanford.owl2lpg.client.read.hierarchy;

import org.semanticweb.owlapi.model.OWLDataProperty;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface DataPropertyHierarchyAccessor extends HierarchyAccessor<OWLDataProperty> {

  OWLDataProperty getOwlTopDataProperty();
}
