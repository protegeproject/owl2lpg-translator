package edu.stanford.owl2lpg.exporter.csv;

import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.Policy;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Sets;
import com.google.common.io.CountingInputStream;
import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.OntologyDocumentId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
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
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@SuppressWarnings("UnstableApiUsage")
public class OboCsvExporter {

  @Nonnull
  private final PerAxiomCsvExporter csvExporter;

  @Inject
  public OboCsvExporter(@Nonnull PerAxiomCsvExporter csvExporter) {
    this.csvExporter = checkNotNull(csvExporter);
  }

  public void export(@Nonnull Path inputFile,
                     @Nonnull UUID projectUuid,
                     @Nonnull UUID branchUuid,
                     @Nonnull UUID ontDocUuid,
                     boolean isTrackingDeclaration) throws IOException {
    export(inputFile,
        ProjectId.get(projectUuid.toString()),
        BranchId.get(branchUuid.toString()),
        OntologyDocumentId.get(ontDocUuid.toString()),
        isTrackingDeclaration);
  }

  public void export(@Nonnull Path inputFile,
                     @Nonnull ProjectId projectId,
                     @Nonnull BranchId branchId,
                     @Nonnull OntologyDocumentId ontDocId,
                     boolean isTrackingDeclaration) throws IOException {
    var in = new CountingInputStream(Files.newInputStream(inputFile));

    var csvTranslator = new MinimalObo2Owl(in, csvExporter, Files.size(inputFile), isTrackingDeclaration);
    csvTranslator.translateProject(projectId, branchId, ontDocId);

    var sw = Stopwatch.createStarted();

    var oboParser = new OBOFormatParser();
    replaceStringCacheWithNoOpCache(oboParser);
    var bufferedReader = new BufferedReader(new InputStreamReader(in));
    oboParser.setReader(bufferedReader);

    var obodoc = new MinimalOboDoc();
    obodoc.setTranslator(csvTranslator);
    csvTranslator.setObodoc(obodoc);
    oboParser.parseOBODoc(obodoc);

    System.out.printf("Time: %,dms\n", sw.elapsed().toMillis());
    System.out.printf("Axioms: %,d\n", +csvTranslator.getAxiomsCount());

    csvExporter.printReport();

    bufferedReader.close();
  }

  private void replaceStringCacheWithNoOpCache(OBOFormatParser parser) {
    try {
      var parserClass = parser.getClass();
      var stringCache = Stream.of(parserClass.getDeclaredFields())
          .filter(field -> field.getName().equals("stringCache"))
          .findFirst()
          .orElseThrow();
      stringCache.setAccessible(true);
      stringCache.set(parser, new NoOpLoadingCache());
    } catch (IllegalAccessException e) {
      System.out.print("WARN: Unable to replace LoadingCache with No-op Cache. This will cause the loading to be slower.");
    }
  }

  private static class MinimalObo2Owl extends OWLAPIObo2Owl {

    private final AtomicInteger counter = new AtomicInteger();

    private final CountingInputStream countingInputStream;
    private final long fileSize;
    private final PerAxiomCsvExporter csvExporter;
    private final boolean isTrackingDeclaration;

    private final Set<OWLDeclarationAxiom> declarationCache = Sets.newHashSet();
    private long ts = ManagementFactory.getThreadMXBean().getCurrentThreadUserTime();

    public MinimalObo2Owl(@Nonnull CountingInputStream countingInputStream,
                          @Nonnull PerAxiomCsvExporter csvExporter,
                          long fileSize,
                          boolean isTrackingDeclaration) {
      super(OWLManager.createOWLOntologyManager());
      this.countingInputStream = countingInputStream;
      this.csvExporter = csvExporter;
      this.fileSize = fileSize;
      this.isTrackingDeclaration = isTrackingDeclaration;
    }

    public void translateProject(ProjectId projectId,
                                 BranchId branchId,
                                 OntologyDocumentId ontDocId) {
//      csvExporter.export(projectId, branchId, ontDocId);
    }

    @Override
    protected void add(Set<OWLAxiom> axioms) {
      if (axioms != null) {
        axioms.forEach(this::add);
      }
    }

    @Override
    protected void add(OWLAxiom axiom) {
      if (axiom instanceof OWLDeclarationAxiom) {
        if (isTrackingDeclaration) {
          if (declarationCache.add((OWLDeclarationAxiom) axiom)) {
            addAxiom(axiom);
          }
        } else {
          addAxiom(axiom);
        }
      } else {
        addAxiom(axiom);
      }
    }

    private void addAxiom(OWLAxiom axiom) {
      counter.incrementAndGet();
      printLog();
      csvExporter.export(axiom);
    }

    private void printLog() {
      var c = counter.get();
      if (c % 1_000_000 == 0) {
        var csvWriter = csvExporter.getCsvWriter();
        long read = countingInputStream.getCount() / (1024 * 1024);
        long ts1 = ManagementFactory.getThreadMXBean().getCurrentThreadUserTime();
        long delta = (ts1 - ts) / 1000_000;
        ts = ts1;
        double percentage = (countingInputStream.getCount() * 100.0) / fileSize;
        int percent = (int) percentage;
        var runtime = Runtime.getRuntime();
        var totalMemory = runtime.totalMemory();
        var freeMemory = runtime.freeMemory();
        var consumedMemory = (totalMemory - freeMemory) / (1024 * 1024);
        var trackedNodesPercent = (100.0 * csvWriter.getTrackedNodeCount()) / csvWriter.getNodeCount();
        var trackedEdgesPercent = (100.0 * csvWriter.getTrackedEdgeCount()) / csvWriter.getEdgeCount();
        System.out.printf("%,9d axioms (Read %,4d Mb [%3d%%]  Delta: %,5d ms) (Used memory: %,8d MB)  Nodes: %,8d  Edges: %,8d  Tracked nodes: %,8d (%,.2f%%)  Tracked edges: %,8d (%,.2f%%)\n",
            c, read, percent, delta, consumedMemory, csvWriter.getNodeCount(), csvWriter.getEdgeCount(), csvWriter.getTrackedNodeCount(), trackedNodesPercent, csvWriter.getTrackedEdgeCount(), trackedEdgesPercent);
      }
    }

    public int getAxiomsCount() {
      return counter.get();
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

    @Override
    public String get(@NonNull String key) {
      return key;
    }

    @Nullable
    @Override
    public Map<String, String> getAll(@NonNull Iterable<? extends String> keys) {
      return null;
    }

    @Override
    public void refresh(@NonNull String key) {
      // NO-OP
    }

    @Nullable
    @Override
    public String getIfPresent(@NonNull Object key) {
      return null;
    }

    @Nullable
    @Override
    public String get(@NonNull String key, @NonNull Function<? super String, ? extends String> mappingFunction) {
      return null;
    }

    @Nullable
    @Override
    public Map<String, String> getAllPresent(@NonNull Iterable<?> keys) {
      return null;
    }

    @Override
    public void put(@NonNull String key, @NonNull String value) {
      // NO-OP
    }

    @Override
    public void putAll(@NonNull Map<? extends String, ? extends String> map) {
      // NO-OP
    }

    @Override
    public void invalidate(@NonNull Object key) {
      // NO-OP
    }

    @Override
    public void invalidateAll(@NonNull Iterable<?> keys) {
      // NO-OP
    }

    @Override
    public void invalidateAll() {
      // NO-OP
    }

    @NonNegative
    @Override
    public long estimatedSize() {
      return 0;
    }

    @Nullable
    @Override
    public CacheStats stats() {
      return null;
    }

    @Nullable
    @Override
    public ConcurrentMap<String, String> asMap() {
      return null;
    }

    @Override
    public void cleanUp() {
      // NO-OP
    }

    @Nullable
    @Override
    public Policy<String, String> policy() {
      return null;
    }
  }
}
