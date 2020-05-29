package edu.stanford.owl2lpg.exporter.csv;

import com.google.auto.value.AutoValue;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabel;

@AutoValue
public abstract class EdgeKey {

    public static EdgeKey get(long start, long end, EdgeLabel label) {
        return new AutoValue_EdgeKey(start, end, label);
    }

    public abstract long getStart();

    public abstract long getEnd();

    public abstract EdgeLabel getLabel();

}
