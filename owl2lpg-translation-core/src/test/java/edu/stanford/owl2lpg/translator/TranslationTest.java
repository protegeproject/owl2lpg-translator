package edu.stanford.owl2lpg.translator;

import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(MockitoJUnitRunner.class)
public class TranslationTest {

  private Translation translation;

  @Mock
  private ImmutableList<Edge> edges;

  @Mock
  private ImmutableList<Translation> nestedTranslations;

  @Mock
  private Node mainNode;

  @Before
  public void setUp() {
    translation = Translation.create(mainNode, edges, nestedTranslations);
  }

  @Test
  public void shouldCreateTranslation() {
    assertThat(translation, is(notNullValue()));
  }

  @Test
  public void shouldCreateTranslationWithEmptyNestedTranslations() {
    translation = Translation.create(mainNode, edges);
    assertThat(translation, is(notNullValue()));
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenMainNodeNull() {
    Translation.create(null, edges, nestedTranslations);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenEdgesNull() {
    Translation.create(mainNode, null, nestedTranslations);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenNestedTranslationsNull() {
    Translation.create(mainNode, edges, null);
  }

  @Test
  public void shouldGetMainNode() {
    var actualMainNode = translation.getMainNode();
    assertThat(actualMainNode, equalTo(mainNode));
  }

  @Test
  public void shouldGetMainNodeStatically() {
    var actualMainNode = Translation.MainNode(translation);
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