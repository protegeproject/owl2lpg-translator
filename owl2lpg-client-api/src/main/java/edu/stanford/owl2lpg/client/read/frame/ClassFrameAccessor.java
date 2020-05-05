package edu.stanford.owl2lpg.client.read.frame;

import edu.stanford.bmir.protege.web.shared.frame.ClassFrame;
import edu.stanford.owl2lpg.client.read.FrameAccessor;
import edu.stanford.owl2lpg.client.read.statement.GraphResult;
import edu.stanford.owl2lpg.client.shared.Arguments;
import edu.stanford.owl2lpg.versioning.model.AxiomContext;
import org.semanticweb.owlapi.model.OWLClass;

import static java.lang.String.format;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ClassFrameAccessor extends FrameAccessor<ClassFrame> {

  @Override
  protected String getCypherQuery(Arguments arguments) {
    var context = arguments.<AxiomContext>get("context");
    var subject = arguments.<OWLClass>get("subject");
    return format(
        "MATCH (subject:Class)-[:IS_SUBJECT_OF]->(axiom)-[* {primary: true}]->(iri:IRI)\n" +
            "MATCH (project)-[:BRANCH]->(branch)-[:ONTOLOGY_DOCUMENT]->(document)-[:AXIOM]->(axiom)\n" +
            "MATCH g=(subject)-[:SUB_CLASS_OF|:RELATED_TO]->(:Class)\n" +
            "WHERE project.projectId = '%s'\n" +
            "  AND branch.branchId = '%s'\n" +
            "  AND document.ontologyDocumentId = '%s'\n" +
            "  AND subject.iri = '%s'\n" +
            "  AND subject.iri <> iri.iri \n" +
            "return g",
        context.getProjectId(),
        context.getBranchId(),
        context.getOntologyDocumentId(),
        subject.getIRI());
  }

  @Override
  protected ClassFrame getFrame(GraphResult result) {
    return null;
  }
}
