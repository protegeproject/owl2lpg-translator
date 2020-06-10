package edu.stanford.owl2lpg.exporter;

import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.Policy;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.google.common.base.Stopwatch;
import edu.stanford.bmir.protege.web.server.util.Counter;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.obolibrary.obo2owl.OWLAPIObo2Owl;
import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.FrameMergeException;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.parser.OBOFormatParser;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Stream;

public class Scratch {
    private static final Logger logger = LoggerFactory.getLogger("OboStreaming");
    public static void main(String[] args) throws IOException, NoSuchFieldException, IllegalAccessException {
        File file = new File(args[0]);
        for (int i = 0; i < 1000; i++) {
            parse(file);
        }
    }
    private static void parse(File file1) throws IOException, IllegalAccessException {
        var fileReader = new FileReader(file1);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        var sw = Stopwatch.createStarted();
        OBOFormatParser parser = new OBOFormatParser();
        replaceStringCacheWithNoOpCache(parser);
        parser.setReader(bufferedReader);
        var translator = new MinimalObo2Owl();
        var obodoc = new MinimalOboDoc();
        // Rather ugly dep!
        obodoc.setTranslator(translator);
        translator.setObodoc(obodoc);
        parser.parseOBODoc(obodoc);
        System.out.println("Time: " + sw.elapsed().toMillis());
        System.out.println("Axioms: " + translator.getAxiomsCount());
        bufferedReader.close();
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
            logger.warn("Unabled to replace LoadingCache with No-op Cache: " + e.getMessage());
        }
    }
    private static class MinimalObo2Owl extends OWLAPIObo2Owl {
        private final Counter counter = new Counter();
        public MinimalObo2Owl() {
            super(OWLManager.createOWLOntologyManager());
        }
        @Override
        protected void add(@Nullable OWLAxiom axiom) {
            counter.increment();
        }
        @Override
        protected void add(@Nullable Set<OWLAxiom> axioms) {
            for(int i = 0; i < axioms.size(); i++) {
                counter.increment();
            }
        }
        public int getAxiomsCount() {
            return counter.getCounter();
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