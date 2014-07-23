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
        StringBuilder sb = new StringBuilder();
        sb.append("<tr class=\"success\">");
        sb.append("<td></td>");
        sb.append("<td>" + result.getName() + "</td>");
        sb.append("<td>" + result.getMessage() + "</td>");
        sb.append("<td>" + result.getReason()+ "</td>");
        sb.append("<td>" + result.getLine()+ "</td>");
        sb.append("<td>" + result.getParagraph()+ "</td>");
        sb.append("</tr>");
        return sb.toString();
    }

    @Override
    protected String doForEachFailedResult(IResult result) {
        StringBuilder sb = new StringBuilder();
        sb.append("<tr class=\"warning\">");
        sb.append("<td></td>");
        sb.append("<td>" + result.getName() + "</td>");
        sb.append("<td>" + result.getMessage() + "</td>");
        sb.append("<td>" + result.getReason()+ "</td>");
        sb.append("<td>" + result.getLine()+ "</td>");
        sb.append("<td>" + result.getParagraph()+ "</td>");
        sb.append("</tr>");
        return sb.toString();
    }

    @Override
    protected String doForEachErrorResult(IResult result) {
        StringBuilder sb = new StringBuilder();
        sb.append("<tr class=\"danger\">");
        sb.append("<td></td>");
        sb.append("<td>" + result.getName() + "</td>");
        sb.append("<td>" + result.getMessage() + "</td>");
        sb.append("<td>" + result.getReason()+ "</td>");
        sb.append("<td>" + result.getLine()+ "</td>");
        sb.append("<td>" + result.getParagraph()+ "</td>");
        sb.append("</tr>");
        return sb.toString();
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
        sb.append("<div class=\"center-block\" style=\"max-width:800px;margin-top:50px;\">");
        sb.append("<p class=\"bg-info\">%s</p>");
        sb.append("<table class=\"table table-hover\">");
        sb.append("<thead>");
        sb.append("<tr>");
        sb.append("<th>#</th>");
        sb.append("<th>Name</th>");
        sb.append("<th>Message</th>");
        sb.append("<th>Reason</th>");
        sb.append("<th>Line</th>");
        sb.append("<th>Paragraph</th>");
        sb.append("</tr>");
        sb.append("</thead>");
        sb.append("<tbody>");
        sb.append("%s");
        sb.append("%s");
        sb.append("%s");
        sb.append("</tbody>");
        sb.append("</table>");
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
