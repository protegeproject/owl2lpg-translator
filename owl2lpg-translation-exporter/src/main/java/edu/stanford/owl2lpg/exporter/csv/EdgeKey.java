package edu.stanford.owl2lpg.exporter.csv;

import com.google.auto.value.AutoValue;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabel;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

@AutoValue
public abstract class EdgeKey implements Comparable<EdgeKey> {

    public static EdgeKey get(long start, long end, EdgeLabel label) {
        return new AutoValue_EdgeKey(start, end, label);
    }

    public abstract long getStart();

    public abstract long getEnd();

    public abstract EdgeLabel getLabel();

    @Override
    public int compareTo(@NotNull EdgeKey o) {
        long startDiff = o.getStart() - this.getStart();
        if(startDiff < 0) {
            return -1;
        }
        else if(startDiff > 0) {
            return 1;
        }
        long endDiff = o.getEnd() - this.getEnd();
        if(endDiff < 0) {
            return -1;
        }
        else if(endDiff > 0) {
            return 1;
        }
        return o.getLabel().compareTo(this.getLabel());
    }
}
