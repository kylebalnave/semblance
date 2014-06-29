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

import io.URLReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import semblance.results.IResult;
import semblance.runners.IRunner;

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
        Config cf = new Config(new File(configUrlOrFilePath));
        List<Map> actionData = (List<Map>) cf.getNestedValue(action);
        if (actionData != null) {
            List<IResult> allResults = new ArrayList<IResult>();
            for (Map singleActionData : actionData) {
                String runnerClassName = (String) singleActionData.get("runner");
                if (runnerClassName instanceof String) {
                    allResults.addAll(callRunner(runnerClassName, singleActionData));
                } else {
                    Logger.getLogger(getClass().getName()).log(Level.WARNING, String.format("Each config Map requires a 'runner' key."));
                }
            }
            //
            // create each report
        }
    }

    /**
     * Calls a single IRunner
     *
     * @param runnerClassName
     * @param configData
     * @return
     */
    private List<IResult> callRunner(String runnerClassName, Map configData) {
        List<IResult> runnerResults = new ArrayList<IResult>();
        if (!runnerClassName.startsWith("//")) {
            try {
                Class<IRunner> runnerClass;
                Constructor<IRunner> runnerConstructor = null;
                runnerClass = (Class<IRunner>) Class.forName(runnerClassName);
                runnerConstructor = runnerClass.getDeclaredConstructor(Config.class);
                IRunner runnerInstance = runnerConstructor.newInstance(new Config(configData));
                runnerResults.addAll(runnerInstance.run());
            } catch (Exception ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Semblance Runner Exception!", ex);
            } catch (Error ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Semblance Runner Error!", ex);
            }
        }
        return runnerResults;
    }
}
