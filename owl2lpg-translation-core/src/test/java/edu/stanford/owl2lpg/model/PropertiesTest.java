package edu.stanford.owl2lpg.model;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PropertiesTest {

  private Properties properties;

  @Mock
  private ImmutableMap<String, Object> map;

  @Before
  public void setUp() {
    when(map.get("key")).thenReturn("value");
  }

  @Test
  public void shouldCreateEmptyProperties() {
    properties = Properties.empty();
    assertThat(properties, is(notNullValue()));
  }

  @Test
  public void shouldCreateProperties() {
    properties = Properties.create(map);
    assertThat(properties, is(notNullValue()));
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenMapNull() {
    properties.create(null);
  }

  @Test
  public void shouldGetEmptyMap() {
    properties = Properties.empty();
    var actualMap = properties.getMap();
    assertThat(actualMap, equalTo(ImmutableMap.of()));
  }

  @Test
  public void shouldGetSuppliedMap() {
    properties = Properties.create(map);
    var actualMap = properties.getMap();
    assertThat(actualMap, equalTo(map));
  }

  @Test
  public void shouldGetValueFromKey() {
    properties = Properties.create(map);
    var actualValue = properties.get("key");
    assertThat(actualValue, equalTo("value"));
  }
}