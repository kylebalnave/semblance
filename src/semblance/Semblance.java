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
package semblance;

import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import semblance.data.MapHelper;
import semblance.io.URLReader;
import semblance.json.JSONParser;
import semblance.reflection.ClassCreator;
import semblance.reporters.Report;
import semblance.reporters.SystemLogReport;
import semblance.results.ErrorResult;
import semblance.results.IResult;
import semblance.results.Result;
import semblance.runners.Runner;

/**
 * Runs several Metrics tests on URLs and output combined results
 *
 * @author balnave
 */
public class Semblance {

    public static final String KEY_REPORT_RUNNERS_LIST = "runners";
    public static final String KEY_REPORT_RUNNER = "runner";
    public static final String KEY_REPORT_CLASS = "className";

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        String configUrlOrFilePath = "./config.json";
        String action = "dist";
        int argIndex = 0;
        for (String arg : args) {
            if (args.length >= argIndex + 1) {
                if (arg.equalsIgnoreCase("-cf") || arg.equalsIgnoreCase("-config")) {
                    configUrlOrFilePath = args[argIndex + 1];
                } else if (arg.equalsIgnoreCase("-act") || arg.equalsIgnoreCase("-action")) {
                    action = args[argIndex + 1];
                } else if (arg.equalsIgnoreCase("-proxy") || arg.equalsIgnoreCase("-pr")) {
                    URLReader.setProxyDetails(args[argIndex + 1], Integer.valueOf(args[argIndex + 2]));
                }
            }
            argIndex++;
        }
        Semblance semblance = new Semblance(configUrlOrFilePath, action);
    }

    /**
     * Initialisation
     *
     * @param configUrlOrFilePath
     * @param action
     * @throws java.io.FileNotFoundException
     */
    public Semblance(String configUrlOrFilePath, String action) throws FileNotFoundException {
        Logger.getLogger(getClass().getName()).log(Level.INFO, String.format("Loading json config '%s'", configUrlOrFilePath));
        JSONParser jParser = new JSONParser(configUrlOrFilePath);
        Map config = (Map<String, Object>) jParser.getJson();
        Map actionMap = (Map) MapHelper.getValue(config, action);
        List<Map> actionRunners = (List<Map>) MapHelper.getValue(actionMap, KEY_REPORT_RUNNERS_LIST, new ArrayList());
        List<IResult> results = new ArrayList<IResult>();
        for (Map singleActionData : actionRunners) {
            Runner runner = null;
            String runnerClassName = (String) MapHelper.getValue(singleActionData, KEY_REPORT_RUNNER, "");
            if (runnerClassName.isEmpty()) {
                results.add(new ErrorResult(runnerClassName, String.format("Each action requires a '%s' key.", KEY_REPORT_RUNNER)));
                Logger.getLogger(getClass().getName()).log(Level.WARNING, String.format("Each action requires a '%s' key.", KEY_REPORT_RUNNER));
            } else if (runnerClassName.startsWith("//")) {
                results.add(new Result(runnerClassName, true, String.format("Ignoring Runner %s", runnerClassName)));
                Logger.getLogger(getClass().getName()).log(Level.INFO, String.format("Ignoring Runner %s", runnerClassName));
            } else {
                results.addAll(callRunner(runnerClassName, singleActionData));
            }
        }
        //
        // log the summary of all results
        Report report = new SystemLogReport(results);
        report.out();
    }

    /**
     * Call a single Runner and return the Results
     * @param runnerClassName
     * @param configMap
     * @return 
     */
    private List<IResult> callRunner(String runnerClassName, Map configMap) {
        List<IResult> results = new ArrayList<IResult>();
        ClassCreator definition = new ClassCreator<Runner>(runnerClassName);
        Constructor<Runner> constructor = definition.getConstructor(Map.class);
        Runner runner = (Runner) definition.newInstance(constructor, configMap);
        //
        // call the runner
        if (runner != null) {
            try {
                results.addAll(runner.run());
                runner.report();
            } catch (Exception ex) {
                results.add(new ErrorResult(runnerClassName, "Uncaught Exception processing runner"));
                Logger.getLogger(Semblance.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Error ex) {
                results.add(new ErrorResult(runnerClassName, "Uncaught Error processing runner"));
                Logger.getLogger(Semblance.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return results;
    }
}
