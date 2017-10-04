package org.summerframework.experimental.report;

import java.util.List;

/**
 *
 */
public interface Report {

    Summary getSummary();

    List<Entry> getEntries();

}
