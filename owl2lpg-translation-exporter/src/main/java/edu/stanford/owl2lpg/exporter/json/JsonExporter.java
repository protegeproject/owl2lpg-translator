package edu.stanford.owl2lpg.exporter.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.stanford.owl2lpg.model.AxiomContext;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.VersionedOntologyTranslator;
import org.semanticweb.owlapi.model.OWLAxiom;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

public class JsonExporter {

    private final VersionedOntologyTranslator translator;

    private AxiomContext context;

    private final ObjectMapper objectMapper;

    private final Writer writer;

    private Set<Long> writtenNodes = new HashSet<>();

    public JsonExporter(VersionedOntologyTranslator translator,
                        AxiomContext context,
                        ObjectMapper objectMapper,
                        Writer writer) {
        this.translator = translator;
        this.context = context;
        this.objectMapper = objectMapper;
        this.writer = writer;
    }

    public void export(@Nonnull OWLAxiom axiom) throws IOException {
        var translation = translator.translate(context, axiom);
        export(translation);
    }

    private void export(Translation translation) throws IOException {
        var mainNode = translation.getMainNode();
        writeNode(mainNode);
        for(var nestedTranslation : translation.getNestedTranslations()) {
            export(nestedTranslation);
        }
        for(var edge : translation.getEdges()) {
            objectMapper.writeValue(writer, edge);
            writer.append('\n');
        }
    }

    private void writeNode(Node mainNode) throws IOException {
        if(writtenNodes.contains(mainNode.getNodeId().getId())) {
            return;
        }
        writtenNodes.add(mainNode.getNodeId().getId());
        objectMapper.writeValue(writer, mainNode);
        writer.append('\n');
    }
}
