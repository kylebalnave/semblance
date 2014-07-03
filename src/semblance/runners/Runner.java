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

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import semblance.args.ArgsHelper;
import semblance.data.MapHelper;
import semblance.io.URLReader;
import semblance.json.JSONParser;
import semblance.reflection.ClassCreator;
import semblance.reporters.Report;
import semblance.reporters.SystemLogReport;
import semblance.results.IResult;

/**
 * The an Abstract Class for Semblance Runners
 *
 * @author balnave
 */
public abstract class Runner {

    public static final String KEY_REPORT_REPORTS = "reports";
    public static final String KEY_REPORT_OUT = "out";
    public static final String KEY_REPORT_CLASS = "className";

    protected List<IResult> results = new ArrayList<IResult>();
    protected Map<String, Object> config;
    
    /**
     * Helper to create a Runner from a main
     * @param classToCreate
     * @param args 
     */
    public static void callRunnerSequence(Class classToCreate, String[] args) {
        String configPath = ArgsHelper.getFirstArgMatching(args, new String[]{"-cf", "-config"}, "./config.json");
        String proxyDetails = ArgsHelper.getArgMatching(args, "proxy", "");
        String[] proxyParts = proxyDetails.split(":");
        if(proxyParts.length == 2) {
            URLReader.setProxyDetails(proxyParts[0], Integer.valueOf(proxyParts[1]));
        }
        Runner runner;
        try {
            Constructor constructor = classToCreate.getConstructor(String.class);
            runner = (Runner) constructor.newInstance(configPath);
            List<IResult> results = runner.run();
            runner.report();
            //
            // log the summary of all results
            Report report = new SystemLogReport(results);
            report.out();
        } catch (Exception ex) {
            Logger.getLogger(Runner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Initialiser with config data
     *
     * @param config
     */
    public Runner(Map<String, Object> config) {
        Logger.getLogger(getClass().getName()).info(String.format("Config set as Map"));
        this.config = config;
    }

    /**
     * Initialiser with config path
     *
     * @param pathToJson
     */
    public Runner(String pathToJson) {
        if (!pathToJson.isEmpty()) {
            Logger.getLogger(getClass().getName()).info(String.format("Loading config '%s'", pathToJson));
            JSONParser jParser = new JSONParser(pathToJson);
            config = (Map<String, Object>) jParser.getJson();
        } else {
            Logger.getLogger(getClass().getName()).severe(String.format("Config path cannot be empty"));
            config = new HashMap();
        }
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
        List<Map<String, String>> reportList = (List<Map<String, String>>) getConfigValue(KEY_REPORT_REPORTS, new ArrayList());
        for (Map reportData : reportList) {
            try {
                String out = (String) MapHelper.getValue(reportData, KEY_REPORT_OUT, "");
                String className = (String) MapHelper.getValue(reportData, KEY_REPORT_CLASS, "");
                if (!out.isEmpty()) {
                    File reportFile = new File(out);
                    if (reportFile.getParent() != null) {
                        File reportDir = new File(reportFile.getParent());
                        reportDir.mkdirs();
                        ClassCreator<Report> cLoader = new ClassCreator<Report>(className);
                        Constructor<Report> constructor = cLoader.getConstructor(List.class);
                        if (constructor != null) {
                            Report report = cLoader.newInstance(constructor, results);
                            if (report != null) {
                                report.out(out);
                            } else {
                                Logger.getLogger(getClass().getName()).warning(String.format("Cannot find Report instance for Class %s", className));
                            }
                        } else {
                            Logger.getLogger(getClass().getName()).warning(String.format("Cannot find Report constructor for %s", className));
                        }
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(getClass().getName()).log(Level.WARNING, "Exception creating report", ex);
            } catch (Error er) {
                Logger.getLogger(getClass().getName()).log(Level.WARNING, "Error creating report", er);
            }
        }
    }

    /**
     * Gets a value from the Config
     *
     * @param key
     * @param defaultValue
     * @return
     */
    protected Object getConfigValue(String key, Object defaultValue) {
        return MapHelper.getValue(config, key, defaultValue);
    }

    /**
     * Gets a value from the Config
     *
     * @param key
     * @return
     */
    protected Object getConfigValue(String key) {
        return getConfigValue(key, null);
    }

}
