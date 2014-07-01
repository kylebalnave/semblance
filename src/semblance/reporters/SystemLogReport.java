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
import java.util.logging.Level;
import java.util.logging.Logger;
import semblance.results.IResult;

/**
 * Outputs the results to the System.out.println
 *
 * @author balnave
 */
public class SystemLogReport extends Report {

    public SystemLogReport(List<IResult> results) {
        super(results);
    }

    @Override
    protected String doForEachResultList(List<IResult> results) {
        return String.format("=============\nRESULTS\n=============\nTests(%s) -- Failures(%s) -- Errors(%s)\n",
                getResultsCount(),
                getFailureCount(),
                getErrorCount());
    }

    @Override
    protected String doForEachPassedResult(IResult result) {
        return String.format("Pass:'%s' -- Url:'%s'\n",
                result.getMessage(),
                result.getName());
    }

    @Override
    protected String doForEachFailedResult(IResult result) {
        return String.format("Fail:'%s' -- Url:'%s'\nMessage:'%s'\n",
                result.getMessage(),
                result.getName(),
                result.getReason());
    }

    @Override
    protected String doForEachErrorResult(IResult result) {
        return String.format("Error:'%s' -- Url:'%s'\nMessage:'%s'\n",
                result.getMessage(),
                result.getName(),
                result.getReason());
    }

    @Override
    public void out() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.doForEachResultList(results));
        for (IResult result : results) {
            if (result.hasError()) {
                sb.append(this.doForEachErrorResult(result));
                sb.append("=============\n");
            } else if (result.hasFailed()) {
                sb.append(this.doForEachFailedResult(result));
                sb.append("=============\n");
            } else {
                Logger.getAnonymousLogger().log(Level.INFO, this.doForEachPassedResult(result));
            }
        }
        Logger.getAnonymousLogger().log(Level.INFO, sb.toString());
    }

    @Override
    public boolean out(String fileOut) {
        out();
        return false;
    }

}
