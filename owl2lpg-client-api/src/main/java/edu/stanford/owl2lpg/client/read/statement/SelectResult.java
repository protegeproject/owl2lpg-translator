package edu.stanford.owl2lpg.client.read.statement;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class SelectResult {

  public static SelectResult create(@Nonnull ImmutableList<String> columnNames,
                                    @Nonnull ImmutableList<ImmutableMap<String, Object>> dataRows) {
    return new AutoValue_SelectResult(columnNames, dataRows);
  }

  public int size() {
    return getDataRows().size();
  }

  public ImmutableMap<String, Object> get(int index) {
    return getDataRows().get(index);
  }

  public Stream<ImmutableMap<String, Object>> rows() {
    return getDataRows().stream();
  }

  public abstract ImmutableList<String> getColumnNames();

  protected abstract ImmutableList<ImmutableMap<String, Object>> getDataRows();

  static class Builder {

    private List<String> cacheColumnNames = Lists.newArrayList();

    private List<ImmutableMap<String, Object>> mutableDataRows = Lists.newArrayList();

    public Builder add(Map<String, Object> row) {
      if (cacheColumnNames.isEmpty()) {
        cacheColumnNames.addAll(row.keySet());
      }
      mutableDataRows.add(ImmutableMap.copyOf(row));
      return this;
    }

    public SelectResult build() {
      return SelectResult.create(
          ImmutableList.copyOf(cacheColumnNames),
          ImmutableList.copyOf(mutableDataRows));
    }
  }
}
