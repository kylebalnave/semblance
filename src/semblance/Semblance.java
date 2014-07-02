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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import semblance.data.MapHelper;
import semblance.io.FileUtils;
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
    public static final String KEY_REPORT_RUNNER = "className";
    public static final String KEY_REPORT_CLASS = "className";
    public static final String KEY_PLUGIN_DIRS = "classpaths";

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
        //
        // load the JSON config
        Logger.getLogger(getClass().getName()).info(String.format("Loading json config '%s'", configUrlOrFilePath));
        JSONParser jParser = new JSONParser(configUrlOrFilePath);
        Map config = (Map<String, Object>) jParser.getJson();
        Logger.getLogger(getClass().getName()).info(String.format("Preparing action '%s'", action));
        Map actionMap = (Map) MapHelper.getValue(config, action);
        List<Map> actionRunners = (List<Map>) MapHelper.getValue(actionMap, KEY_REPORT_RUNNERS_LIST, new ArrayList());
        Logger.getLogger(getClass().getName()).info(String.format("Preparing runners"));
        List<IResult> results = new ArrayList<IResult>();
        //
        // List all external plugin jars
        List<String> pluginDirs = (List<String>) MapHelper.getValue(config, KEY_PLUGIN_DIRS, new ArrayList());
        if (pluginDirs.isEmpty()) {
            pluginDirs.add("./plugins");
        }
        for (String jarPath : pluginDirs) {
            String pluginsPath = jarPath;
            Map<String, File> plugins = FileUtils.listFiles(pluginsPath);
            for (File file : plugins.values()) {
                if (file.getName().endsWith(".jar")) {
                    try {
                        Logger.getLogger(getClass().getName()).info(String.format("Loading jar '%s'", file.getAbsolutePath()));
                        ClassCreator.addFile(file);
                    } catch (IOException ex) {
                        Logger.getLogger(Semblance.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        //
        // Create a run each 
        for (Map singleActionData : actionRunners) {
            String runnerClassName = (String) MapHelper.getValue(singleActionData, KEY_REPORT_RUNNER, "");
            if (runnerClassName.isEmpty()) {
                results.add(new ErrorResult(runnerClassName, String.format("Each action requires a '%s' key.", KEY_REPORT_RUNNER)));
                Logger.getLogger(getClass().getName()).warning(String.format("Each action requires a '%s' key.", KEY_REPORT_RUNNER));
            } else if (runnerClassName.startsWith("//")) {
                results.add(new Result(runnerClassName, true, String.format("Ignoring Runner %s", runnerClassName)));
                Logger.getLogger(getClass().getName()).warning(String.format("Ignoring Runner %s", runnerClassName));
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
     *
     * @param className
     * @param configMap
     * @return
     */
    private List<IResult> callRunner(String className, Map configMap) {
        List<IResult> results = new ArrayList<IResult>();
        ClassCreator definition = new ClassCreator<Runner>(className);
        Constructor<Runner> constructor = definition.getConstructor(Map.class);
        if (constructor != null) {
            Runner runner = (Runner) definition.newInstance(constructor, configMap);
            //
            // call the runner
            if (runner != null) {
                try {
                    Logger.getLogger(getClass().getName()).info(String.format("Calling Runner '%s'", className));
                    results.addAll(runner.run());
                    runner.report();
                } catch (Exception ex) {
                    results.add(new ErrorResult(className, "Uncaught Exception processing runner"));
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                } catch (Error ex) {
                    results.add(new ErrorResult(className, "Uncaught Error processing runner"));
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, String.format("Cannot create instance of '%s'.  Does a *.jar need to be included? And Does the constructor exist?", className));
            }
        } else {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, String.format("Cannot find class constructor '%s'.  Does a *.jar need to be included?", className));
        }
        return results;
    }
}
