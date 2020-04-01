package edu.stanford.owl2lpg.model;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class PropertiesTest {

  private Properties properties;

  @Before
  public void setUp() {

  }

  @Test
  public void shouldGetEmptyMap() {
    properties = Properties.empty();
    var map = properties.getMap();
    assertThat(map, equalTo(ImmutableMap.of()));
  }

  @Test
  public void shouldGetSuppliedMap() {
    var suppliedMap = ImmutableMap.<String, Object>of("key", "value");
    properties = Properties.create(suppliedMap);
    var map = properties.getMap();
    assertThat(map, equalTo(suppliedMap));
  }
}