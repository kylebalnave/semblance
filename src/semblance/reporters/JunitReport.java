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

import java.util.List;
import org.w3c.dom.Element;
import semblance.results.IResult;

/**
 * Exports a JunitReport TestSuite to an XML file
 *
 * @author balnave
 */
public class JunitReport extends XmlReport {

    public JunitReport(List<IResult> results) {
        super(results);
    }

    @Override
    protected Element doForEachResultList(List<IResult> results) {
        // testsuites element
        Element testsuites = doc.createElement("testsuites");
        testsuites.setAttribute("name", "suite-results");
        testsuites.setAttribute("tests", String.valueOf(this.getResultsCount()));
        testsuites.setAttribute("failures", String.valueOf(this.getFailureCount()));
        testsuites.setAttribute("errors", String.valueOf(this.getErrorCount()));
        testsuites.setAttribute("time", String.valueOf(this.getResultsTimeMs()));
        testsuites.setAttribute("timestamp", String.valueOf(this.getTimeStamp()));
        doc.appendChild(testsuites);
        // testsuite element
        Element testsuite = doc.createElement("testsuite");
        testsuite.setAttribute("id", "1");
        testsuite.setAttribute("name", "suite-results");
        testsuites.setAttribute("tests", String.valueOf(this.getResultsCount()));
        testsuites.setAttribute("failures", String.valueOf(this.getFailureCount()));
        testsuites.setAttribute("errors", String.valueOf(this.getErrorCount()));
        testsuites.setAttribute("time", String.valueOf(this.getResultsTimeMs()));
        testsuites.appendChild(testsuite);
        return testsuite;
    }

    @Override
    protected Element doForEachPassedResult(IResult result) {
        // staff elements
        Element element = doc.createElement("testcase");
        element.setAttribute("name", result.getURIString());
        element.setAttribute("tests", "1");
        element.setAttribute("assertions", "1");
        element.setAttribute("failures", result.hasFailed()? "1" : "0");
        element.setAttribute("errors", result.hasError()? "1" : "0");
        element.setAttribute("time", String.valueOf(result.getExecutionTimeMs()));
        return element;
    }

    @Override
    protected Element doForEachFailedResult(IResult result) {
        Element element = doForEachPassedResult(result);
        Element failure = doc.createElement("failure");
        failure.setAttribute("type", String.valueOf(result.getReason()));
        failure.setAttribute("message", result.getMessage());
        element.appendChild(failure);
        return element;
    }

    @Override
    protected Element doForEachErrorResult(IResult result) {
        Element element = doForEachPassedResult(result);
        Element error = doc.createElement("error");
        error.setAttribute("type", String.valueOf(result.getReason()));
        error.setAttribute("message", result.getMessage());
        element.appendChild(error);
        return element;
    }
}
