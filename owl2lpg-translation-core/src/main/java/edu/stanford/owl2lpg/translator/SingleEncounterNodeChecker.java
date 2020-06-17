package edu.stanford.owl2lpg.translator;

public interface SingleEncounterNodeChecker {

  /**
   * Determines whether the specified node object will only be encountered once
   * in a translation session
   *
   * @param o The node object.
   * @return true if the node object will not be encountered more than once in a translation
   * session, otherwise false.
   */
  boolean isSingleEncounterNodeObject(Object o);
}
