package edu.stanford.owl2lpg.exporter;

import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.Policy;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.google.common.base.Stopwatch;
import com.google.common.io.CountingInputStream;
import edu.stanford.bmir.protege.web.server.util.Counter;
import edu.stanford.owl2lpg.exporter.csv.CsvExporter;
import edu.stanford.owl2lpg.exporter.csv.DaggerCsvExporterComponent;
import edu.stanford.owl2lpg.exporter.csv.NoOpWriter;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.obolibrary.obo2owl.OWLAPIObo2Owl;
import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.FrameMergeException;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.parser.OBOFormatParser;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Stream;

public class Scratch {

    private static final Logger logger = LoggerFactory.getLogger("OboStreaming");
    public static void main(String[] args) throws IOException {
        File file = new File(args[0]);
//        for (int i = 0; i < 1000; i++) {
        parse(file);
//        }
    }
    private static void parse(File file) throws IOException {
        var in = new CountingInputStream(new FileInputStream(file));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        var sw = Stopwatch.createStarted();
        OBOFormatParser parser = new OBOFormatParser();
        replaceStringCacheWithNoOpCache(parser);
        parser.setReader(bufferedReader);
        CsvExporter csvExporter = DaggerCsvExporterComponent.create().getCsvExporterFactory()
            .create(new NoOpWriter(), new NoOpWriter());
        var translator = new MinimalObo2Owl(in, file.length(), csvExporter);
        var obodoc = new MinimalOboDoc();
        // Rather ugly dep!
        obodoc.setTranslator(translator);
        translator.setObodoc(obodoc);
        parser.parseOBODoc(obodoc);
        System.out.printf("Time: %,dms\n", sw.elapsed().toMillis());
        System.out.printf("Axioms: %,d\n", + translator.getAxiomsCount());
        dump(csvExporter, new PrintWriter(System.out));
        bufferedReader.close();
    }
    private static void dump(CsvExporter exporter, PrintWriter console) {
        console.printf("\nNodes: %,d\n\n", exporter.getNodeCount());
        var nodeLabelsMultiset = exporter.getNodeLabelsMultiset();
        nodeLabelsMultiset
            .forEachEntry((nodeLabels, count) -> {
                console.printf("    Node   %-60s %,10d\n", nodeLabels.printLabels(), count);
            });
        console.printf("\nRelationships: %,d\n\n", exporter.getEdgeCount());
        var edgeLabelMultiset = exporter.getEdgeLabelMultiset();
        edgeLabelMultiset
            .forEachEntry((edgeLabel, count) -> {
                console.printf("    Rel    %-36s %,10d\n", edgeLabel.printLabel(), count);
            });
        console.flush();
    }
    private static void replaceStringCacheWithNoOpCache(OBOFormatParser parser) {
        try {
            Class<? extends OBOFormatParser> parserClass = parser.getClass();
            Field stringCache = Stream.of(parserClass.getDeclaredFields())
                .filter(field -> field.getName().equals("stringCache"))
                .findFirst()
                .orElseThrow();
            stringCache.setAccessible(true);
            stringCache.set(parser, new NoOpLoadingCache());
        } catch (IllegalAccessException e) {
            logger.warn("Unable to replace LoadingCache with No-op Cache.  This will cause the loading to be slower.");
        }
    }
    private static class MinimalObo2Owl extends OWLAPIObo2Owl {
        private final Counter counter = new Counter();
        private final CountingInputStream countingInputStream;
        private long fileSize;
        private CsvExporter csvExporter;
        private long ts = ManagementFactory.getThreadMXBean().getCurrentThreadUserTime();
        private OntologyDocumentId ontDocId = OntologyDocumentId.create();
        private long startTime = System.currentTimeMillis();
        public MinimalObo2Owl(CountingInputStream countingInputStream,
                              long fileSize,
                              CsvExporter csvExporter) {
            super(OWLManager.createOWLOntologyManager());
            this.countingInputStream = countingInputStream;
            this.fileSize = fileSize;
            this.csvExporter = csvExporter;
        }
        @Override
        protected void add(@Nullable OWLAxiom axiom) {
            counter.increment();
            logParsed();
            try {
                csvExporter.write(ontDocId, axiom);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        @Override
        protected void add(@Nullable Set<OWLAxiom> axioms) {
            for(OWLAxiom ax : axioms) {
                add(ax);
            }
        }
        private void logParsed() {
            var c = counter.getCounter();
            if(c % 1_000_000 == 0) {
                long read = countingInputStream.getCount() / (1024 * 1024);
                long ts1 = ManagementFactory.getThreadMXBean().getCurrentThreadUserTime();
                long delta = (ts1 - ts) / 1000_000;
                ts = ts1;
                double percentage = (countingInputStream.getCount() * 100.0) / fileSize;
                long elapsedWallClockTime = System.currentTimeMillis() - startTime;
                long timeRemaining = 0;
                if(percentage != 0) {
                    long millisPerPercent = (long) (elapsedWallClockTime / percentage);
                    timeRemaining = (long) ((100 - percentage) * millisPerPercent) / 1_000;
                }
                int percent = (int) percentage;
                var runtime = Runtime.getRuntime();
                var totalMemory = runtime.totalMemory();
                var freeMemory = runtime.freeMemory();
                var consumedMemory = (totalMemory - freeMemory) / (1024 * 1024);
                System.out.printf("%,9d axioms (Read %,4d Mb [%3d%%]  Delta: %,5d ms  Remaining: %,3ds) (RAM: %,d MB)\n", c, read, percent, delta, timeRemaining, consumedMemory);
            }
        }
        public int getAxiomsCount() {
            return counter.getCounter();
        }
        @Nonnull
        @Override
        public IRI oboIdToIRI(@Nonnull String id) {
            return oboIdToIRI_load(id);
        }
    }
    private static class MinimalOboDoc extends OBODoc {
        private OWLAPIObo2Owl translator;
        public void setTranslator(OWLAPIObo2Owl translator) {
            this.translator = translator;
        }
        @Override
        public void addFrame(@Nonnull Frame f) throws FrameMergeException {
            if (f.getType().equals(Frame.FrameType.TYPEDEF)) {
                super.addFrame(f);
            }
            if (f.getType().equals(Frame.FrameType.TERM)) {
                translator.trTermFrame(f);
            }
        }
    }
    private static class NoOpLoadingCache implements LoadingCache<String, String> {
        @org.checkerframework.checker.nullness.qual.Nullable
        @Override
        public String get(@NonNull String key) {
            return key;
        }
        @Override
        public @NonNull Map<String, String> getAll(@NonNull Iterable<? extends String> keys) {
            return null;
        }
        @Override
        public void refresh(@NonNull String key) {
        }
        @org.checkerframework.checker.nullness.qual.Nullable
        @Override
        public String getIfPresent(@NonNull Object key) {
            return null;
        }
        @org.checkerframework.checker.nullness.qual.Nullable
        @Override
        public String get(@NonNull String key, @NonNull Function<? super String, ? extends String> mappingFunction) {
            return null;
        }
        @Override
        public @NonNull Map<String, String> getAllPresent(@NonNull Iterable<?> keys) {
            return null;
        }
        @Override
        public void put(@NonNull String key, @NonNull String value) {
        }
        @Override
        public void putAll(@NonNull Map<? extends String, ? extends String> map) {
        }
        @Override
        public void invalidate(@NonNull Object key) {
        }
        @Override
        public void invalidateAll(@NonNull Iterable<?> keys) {
        }
        @Override
        public void invalidateAll() {
        }
        @Override
        public @NonNegative long estimatedSize() {
            return 0;
        }
        @Override
        public @NonNull CacheStats stats() {
            return null;
        }
        @Override
        public @NonNull ConcurrentMap<String, String> asMap() {
            return null;
        }
        @Override
        public void cleanUp() {
        }
        @Override
        public @NonNull Policy<String, String> policy() {
            return null;
        }
    }
}