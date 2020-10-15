package edu.stanford.owl2lpg.translator;

import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.Translation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(MockitoJUnitRunner.class)
public class TranslationTest {

  private Translation translation;

  @Mock
  private ImmutableList<Edge> edges;

  @Mock
  private ImmutableList<Translation> nestedTranslations;

  @Mock
  private Node mainNode;

  @Mock
  private Object object;

  @Before
  public void setUp() {
    translation = Translation.create(object, mainNode, edges, nestedTranslations);
  }

  @Test
  public void shouldCreateTranslation() {
    assertThat(translation, is(notNullValue()));
  }

  @Test
  public void shouldCreateTranslationWithEmptyNestedTranslations() {
    translation = Translation.create(object, mainNode, edges);
    assertThat(translation, is(notNullValue()));
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenMainNodeNull() {
    Translation.create(object, null, edges, nestedTranslations);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenEdgesNull() {
    Translation.create(object, mainNode, null, nestedTranslations);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenNestedTranslationsNull() {
    Translation.create(object, mainNode, edges, null);
  }

  @Test
  public void shouldGetMainNode() {
    var actualMainNode = translation.getMainNode();
    assertThat(actualMainNode, equalTo(mainNode));
  }

  @Test
  public void shouldGetEdges() {
    var actualEdges = translation.getEdges();
    assertThat(actualEdges, equalTo(edges));
  }

  @Test
  public void getNestedTranslations() {
    var actualNestedTranslations = translation.getNestedTranslations();
    assertThat(actualNestedTranslations, equalTo(nestedTranslations));
  }

  @Test
  public void shouldGetEdgesStream() {

  }

  @Test
  public void shouldGetTranslationsStream() {

  }
}