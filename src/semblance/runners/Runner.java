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
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import semblance.json.JSONParser;
import semblance.reflection.ClassCreator;
import semblance.reporters.Report;
import semblance.results.IResult;

/**
 * The concrete implementation of IRunner
 *
 * @author balnave
 */
public abstract class Runner {

    protected List<IResult> results = new ArrayList<IResult>();
    protected Map<String, Object> config;

    /**
     * Initialiser with config data
     *
     * @param config
     */
    public Runner(Map<String, Object> config) {
        this.config = config;
    }

    /**
     * Initialiser with config path
     *
     * @param pathToJson
     */
    public Runner(String pathToJson) {
        JSONParser jParser = new JSONParser(pathToJson);
        config = (Map<String, Object>) jParser.getJson();
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
     * Outputs reports to a file
     */
    public void report() {
        List<Map<String, String>> reportList = (List<Map<String, String>>) getConfigValue("reports", new ArrayList());
        for (Map<String, String> reportData : reportList) {
            String out = reportData.get("out");
            String className = reportData.get("className");
            File reportFile = new File(out);
            if (reportFile.getParent() != null) {
                try {
                    File reportDir = new File(reportFile.getParent());
                    reportDir.mkdirs();
                    ClassCreator<Report> cLoader = new ClassCreator<Report>(className);
                    Constructor<Report> constructor = cLoader.getConstructor(List.class);
                    Report report = constructor.newInstance(this.results);
                    report.out(out);
                } catch (InstantiationException ex) {
                    Logger.getLogger(Runner.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(Runner.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(Runner.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvocationTargetException ex) {
                    Logger.getLogger(Runner.class.getName()).log(Level.SEVERE, null, ex);
                }
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
        if (config != null && config.containsKey(key)) {
            return config.get(key);
        }
        return defaultValue;
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

    /**
     * Gets a Report Constructor
     *
     * @param className
     * @return
     */
    protected Constructor<Report> getReporterConstructor(String className) {
        try {
            Class<Report> runnerClass;
            runnerClass = (Class<Report>) Class.forName(className);
            return runnerClass.getDeclaredConstructor(List.class);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Semblance Runner Exception!", ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Semblance Runner Exception!", ex);
        } catch (SecurityException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Semblance Runner Exception!", ex);
        } catch (Error ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Semblance Runner Error!", ex);
        }
        return null;

    }
}
