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
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import semblance.data.MapHelper;
import semblance.io.URLReader;
import semblance.json.JSONParser;
import semblance.results.IResult;
import semblance.runners.Runner;

/**
 * Runs several Metrics tests on URLs and output combined results
 *
 * @author balnave
 */
public class Semblance {

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
                } else if (arg.equalsIgnoreCase("-proxy")) {
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
        Logger.getLogger(Semblance.class.getName()).log(Level.INFO, String.format("Loading config '%s'", configUrlOrFilePath));
        JSONParser jParser = new JSONParser(configUrlOrFilePath);
        Map config = (Map<String, Object>) jParser.getJson();
        Map actionData = (Map) MapHelper.getValue(config, action);
        if (actionData instanceof Map) {
            List<Map<String, Object>> actionRunners = (List<Map<String, Object>>) actionData.get("runners");
            List<Map<String, Object>> actionReporters = (List<Map<String, Object>>) actionData.get("reporters");
            for (Map singleActionData : actionRunners) {
                String runnerClassName = (String) singleActionData.get("runner");
                if (runnerClassName instanceof String) {
                    callRunner(runnerClassName, singleActionData);
                } else {
                    Logger.getLogger(getClass().getName()).log(Level.WARNING, String.format("Each config Map requires a 'runner' key."));
                }
            }
        }
    }

    /**
     * Calls a single IRunner
     *
     * @param runnerClassName
     * @param configData
     * @return
     */
    private void callRunner(String runnerClassName, Map configData) {
        if (!runnerClassName.startsWith("//")) {
            try {
                Class<Runner> runnerClass;
                Constructor<Runner> runnerConstructor = null;
                runnerClass = (Class<Runner>) Class.forName(runnerClassName);
                runnerConstructor = runnerClass.getDeclaredConstructor(Map.class);
                Runner runner = runnerConstructor.newInstance(configData);
                runner.run();
                runner.report();
            } catch (Exception ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Semblance Runner Exception!", ex);
            } catch (Error ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Semblance Runner Error!", ex);
            }
        }
    }
}
