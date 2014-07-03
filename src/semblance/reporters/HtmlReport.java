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

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import semblance.results.IResult;

/**
 * Outputs the results to the System.out.println
 *
 * @author balnave
 */
public class HtmlReport extends Report {

    public HtmlReport(List<IResult> results) {
        super(results);
    }

    @Override
    protected String doForEachResultList(List<IResult> results) {
        return String.format("Tests(%s) -- Failures(%s) -- Errors(%s)",
                getResultsCount(),
                getFailureCount(),
                getErrorCount());
    }

    @Override
    protected String doForEachPassedResult(IResult result) {
        return String.format("<li><p class=\"text-success\">%s</p><p class=\"text-info\">%s</p></li>",
                result.getName(),
                result.getMessage());
    }

    @Override
    protected String doForEachFailedResult(IResult result) {
        return String.format("<li><p class=\"text-danger\">%s</p><p class=\"text-info\">%s</p><pre>%s</pre></li>",
                result.getName(),
                result.getReason(),
                result.getMessage());
    }

    @Override
    protected String doForEachErrorResult(IResult result) {
        return String.format("<li><p class=\"text-danger\">%s</p><p class=\"text-info\">%s</p><pre>%s</pre></li>",
                result.getName(),
                result.getReason(),
                result.getMessage());
    }

    @Override
    public void out() {
        Logger.getLogger(getClass().getName()).log(Level.INFO, buildOut());
    }

    @Override
    public boolean out(String fileOut) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(fileOut);
            out.print(buildOut());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, null, ex);
        } finally {
            if (out != null) {
                out.close();
            }
        }
        return false;
    }

    /**
     * Builds the out String
     *
     * @return
     */
    private String buildOut() {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>");
        sb.append("<html lang=\"en\">");
        sb.append("<head>");
        sb.append(String.format("<title>%s</title>", "Semblance HTML Report"));
        sb.append(String.format("<link rel=\"stylesheet\" type=\"text/css\" href=\"%s\" />", "http://netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css"));
        sb.append("</head>");
        sb.append("<body>");
        sb.append("<div class=\"center-block\" style=\"max-width:800px;\">");
        sb.append("<p class=\"bg-info\">%s</p>");
        sb.append("<ul class=\"bg-danger\">%s");
        sb.append("</ul>");
        sb.append("<ul class=\"bg-danger\">%s");
        sb.append("</ul>");
        sb.append("<ul class=\"bg-success\">%s");
        sb.append("</ul>");
        sb.append("<p class=\"bg-info\">%s</p>");
        sb.append("</div>");
        sb.append("</body>");
        sb.append("</html>");
        String template = sb.toString();
        StringBuilder summaryHtml = new StringBuilder();
        StringBuilder errorsHtml = new StringBuilder();
        StringBuilder failsHtml = new StringBuilder();
        StringBuilder passesHtml = new StringBuilder();
        summaryHtml.append(this.doForEachResultList(results));
        for (IResult result : results) {
            if (isErrorResult(result)) {
                errorsHtml.append(this.doForEachErrorResult(result));
            } else if (isFailResult(result)) {
                failsHtml.append(this.doForEachFailedResult(result));
            } else {
                passesHtml.append(this.doForEachPassedResult(result));
            }
        }
        return String.format(
                template,
                summaryHtml.toString(),
                errorsHtml.toString(),
                failsHtml.toString(),
                passesHtml.toString(),
                summaryHtml.toString());
    }

}
