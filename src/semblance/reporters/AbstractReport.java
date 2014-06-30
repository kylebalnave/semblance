/*
 * Copyright (C) 2014 balnave
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package semblance.reporters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import semblance.results.IResult;

/**
 *
 * @author balnave
 */
public abstract class AbstractReport {

    protected final List<IResult> results;

    /**
     * Constructor
     *
     * @param results
     */
    public AbstractReport(List<IResult> results) {
        this.results = results;
        Collections.sort(this.results, new Comparator<IResult>() {
            @Override
            public int compare(IResult o1, IResult o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
    }

    /**
     * Out method
     */
    public abstract void out();

    /**
     * Out method
     *
     * @param fileOut
     * @return
     */
    public abstract boolean out(String fileOut);

    /**
     * Gets all the failure results
     *
     * @return
     */
    public List<IResult> getFailureResults() {
        List<IResult> failures = new ArrayList<IResult>();
        for (IResult result : results) {
            if (result.hasFailed()) {
                failures.add(result);
            }
        }
        return failures;
    }

    /**
     * Gets all the error results
     *
     * @return
     */
    public List<IResult> getErrorResults() {
        List<IResult> errors = new ArrayList<IResult>();
        for (IResult result : results) {
            if (result != null) {
                if (result.hasError()) {
                    errors.add(result);
                }
            }
        }
        return errors;
    }

    /**
     * The number of Results
     *
     * @return
     */
    public int getResultsCount() {
        return results.size();
    }

    /**
     * Gets the total time taken for all tests
     *
     * @return
     */
    public long getResultsTimeMs() {
        long timeMs = 0;
        for (IResult result : results) {
            timeMs += result.getExecutionTimeMs();
        }
        return timeMs;
    }

    public String getTimeStamp() {
        Date date = new Date();
        return new SimpleDateFormat("yyyy/MM/dd").format(date) + "T" + new SimpleDateFormat("h:mm:ss").format(date);
    }

    /**
     * The number of failure Results
     *
     * @return
     */
    public int getFailureCount() {
        return getFailureResults().size();
    }

    /**
     * The number of error Results
     *
     * @return
     */
    public int getErrorCount() {
        return getErrorResults().size();
    }

    /**
     * Action to perform for the full list of results
     *
     * @param results
     * @return
     */
    protected abstract Object doForEachResultList(List<IResult> results);

    /**
     * Action to perform for each result
     *
     * @param result
     * @return
     */
    protected abstract Object doForEachPassedResult(IResult result);

    /**
     * Action to perform for each failed result
     *
     * @param result
     * @return
     */
    protected abstract Object doForEachFailedResult(IResult result);

    /**
     * Action to perform for each error result
     *
     * @param result
     * @return
     */
    protected abstract Object doForEachErrorResult(IResult result);

}
