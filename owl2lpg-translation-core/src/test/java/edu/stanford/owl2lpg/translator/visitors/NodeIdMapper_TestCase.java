package edu.stanford.owl2lpg.translator.visitors;

import edu.stanford.owl2lpg.translator.NumberIncrementIdProvider;
import edu.stanford.owl2lpg.translator.UniqueNodeCheckerImpl;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectPropertyImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectSomeValuesFromImpl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class NodeIdMapper_TestCase {

    private NodeIdMapper nodeIdMapper;

    @Before
    public void setUp() throws Exception {
        nodeIdMapper = new NodeIdMapper(new NumberIncrementIdProvider(), new UniqueNodeCheckerImpl());
    }

    @Test
    public void shouldMapToSameNodeIdForOWLEntity() {
        OWLClass entityA = new OWLClassImpl(IRI.create("A"));
        OWLClass entityB = new OWLClassImpl(IRI.create("A"));
        var nodeA = nodeIdMapper.get(entityA);
        var nodeB = nodeIdMapper.get(entityB);
        assertThat(nodeA, is(equalTo(nodeB)));
    }

    @Test
    public void shouldMapToDifferentNodesForStructuallyEquivalentButDifferentClassExpressions() {
        var filler = new OWLClassImpl(IRI.create("A"));
        var property = new OWLObjectPropertyImpl(IRI.create("p"));
        var someValuesFrom = new OWLObjectSomeValuesFromImpl(property, filler);
        var someValuesFrom2 = new OWLObjectSomeValuesFromImpl(property, filler);
        var nodeA = nodeIdMapper.get(someValuesFrom);
        var nodeB = nodeIdMapper.get(someValuesFrom2);
        assertThat(nodeA, is(not(equalTo(nodeB))));
    }

}