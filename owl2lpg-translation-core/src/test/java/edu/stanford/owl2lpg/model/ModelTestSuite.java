package edu.stanford.owl2lpg.model;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    NodeTest.class,
    EdgeTest.class,
    GraphFactoryTest.class,
    PropertiesTest.class
})
public class ModelTestSuite {
  // NO-OP
}
