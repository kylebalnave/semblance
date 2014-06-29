
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

/**
 * Loads a JSON Config { urls: [String, String], dir : String }
 *
 * @author balnave
 */
public class Config {

    protected Map JSON;

    public Config(String data) {
        parseJSON(data);
    }
    
    public Config(Map data) {
       JSON = data;
    }

    public Config(File file) throws FileNotFoundException {
        parseJSON(new BufferedReader(new FileReader(file)));
    }

    public Config(URL url) throws IOException {
        parseJSON(new BufferedReader(
                new InputStreamReader(url.openStream())));
    }
    
    private void parseJSON(String in) {
        try {
            JSON = (Map) JSONValue.parseWithException(in);
        } catch (ParseException ex) {
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void parseJSON(Reader in) {
        try {
            JSON = (Map) JSONValue.parseWithException(in);
        } catch (IOException ex) {
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Set getKeys() {
        return JSON.keySet();
    }
    
    public Set getKeys(Map rootData) {
        return rootData.keySet();
    }

    /**
     * Gets a value using a complex namespace E.g. var1 OR var1.child1
     *
     * @param key
     * @return
     */
    public Object getNestedValue(String key) {
        return getNestedValue(key, null);
    }

    /**
     * Gets a value using a complex namespace E.g. var1 OR var1.child1
     *
     * @param key
     * @param data
     * @return
     */
    private Object getNestedValue(String key, Map data) {
        data = data == null ? JSON : data;
        if (key.contains(".")) {
            String[] parts = key.split("\\.");
            String childNs = parts.length > 1 ? key.substring(parts[0].length() + 1) : "";
            for (String nsPart : parts) {
                if (data.containsKey(nsPart)) {
                    Object jsonPart = data.get(nsPart);
                    if (jsonPart instanceof Map) {
                        return getNestedValue(childNs, (Map) jsonPart);
                    }
                }
            }
        } else if (data.containsKey(key)) {
            return data.get(key);
        }
        return null;
    }

}
