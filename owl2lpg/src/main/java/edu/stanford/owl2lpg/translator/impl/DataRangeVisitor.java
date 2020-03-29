package edu.stanford.owl2lpg.translator.impl;

import edu.stanford.owl2lpg.datastructure.Graph;
import org.semanticweb.owlapi.model.*;

/**
 * The translator sub-module for translating the OWL 2 data range to
 * labelled property graphs.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class DataRangeVisitor extends HasIriVisitor
    implements OWLDataRangeVisitorEx<Graph> {

  @Override
  public Graph visit(OWLDataComplementOf node) {
    return null;
  }

  @Override
  public Graph visit(OWLDataOneOf node) {
    return null;
  }

  @Override
  public Graph visit(OWLDataIntersectionOf node) {
    return null;
  }

  @Override
  public Graph visit(OWLDataUnionOf node) {
    return null;
  }

  @Override
  public Graph visit(OWLDatatypeRestriction node) {
    return null;
  }

  @Override
  public Graph visit(OWLDatatype node) {
    return null;
  }

  @Override
  public <T> Graph doDefault(T object) {
    return null;
  }
}
