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
package semblance.runners;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import semblance.results.IResult;

/**
 * The concrete implementation of IRunner
 *
 * @author balnave
 */
public abstract class Runner {

    protected List<IResult> results = new ArrayList<IResult>();
    protected Map<String, Object> config;

    public Runner(Map<String, Object> config) {
        this.config = config;
    }

    /**
     * Calls the Runner to begin
     *
     * @return
     * @throws Exception
     * @throws Error
     */
    public abstract List<IResult> run() throws Exception, Error;

    /**
     * Outputs reports
     */
    public void report() {
        List<String> reportPaths = (List<String>) getConfigValue("reports", new ArrayList());
        /*
         if (reportPaths != null) {
         for (String reportPath : reportPaths) {
         File reportFile = new File(reportPath);
         if (reportFile != null && reportFile.getParent() != null) {
         File reportDir = new File(reportFile.getParent());
         reportDir.mkdirs();
         }
         if (reportPath.toLowerCase().endsWith(".xml")) {
         new XmlReport(getResults()).out(reportPath);
         } else if (reportPath.toLowerCase().endsWith(".junit")) {
         new JunitReport(getResults()).out(reportPath);
         } else if (reportPath.equalsIgnoreCase("system")) {
         new SystemLogReport(getResults()).out();
         }
         }
         } else {
         new SystemLogReport(getResults()).out();
         }
         */

    }

    /**
     * Gets a value from the Config
     * @param key
     * @param defaultValue
     * @return 
     */
    protected Object getConfigValue(String key, Object defaultValue) {
        if(config != null && config.containsKey(key)) {
            return config.get(key);
        }
        return defaultValue;
    }
    
    protected Object getConfigValue(String key) {
        return getConfigValue(key, null);
    }
}
