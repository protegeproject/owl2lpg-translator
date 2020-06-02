package edu.stanford.owl2lpg.translator;

public interface TranslationSessionUniqueNodeChecker {

    /**
     * Determines whether the specified node will only be encountered once
     * in a translation session
     * @param o The node object.
     * @return true if the object will not be encountered more than once in a translation
     * session, otherwise false.
     */
    boolean isTranslationSessionUniqueNode(Object o);
}
