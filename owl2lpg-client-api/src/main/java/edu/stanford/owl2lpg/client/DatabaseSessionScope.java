package edu.stanford.owl2lpg.client;

import javax.inject.Scope;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Retention(RetentionPolicy.RUNTIME)
@Scope
public @interface DatabaseSessionScope {
}
