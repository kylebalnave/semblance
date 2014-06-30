
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
package semblance.json;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import semblance.io.IReader;
import semblance.io.ReaderFactory;
import semblance.runners.Runner;

/**
 * Loads a JSON JSONParser { urls: [String, String], dir : String }
 *
 * @author balnave
 */
public class JSONParser {

    private final ReaderFactory rf = new ReaderFactory();
    private Object json;

    public JSONParser(String pathToJson) {
        this.json = null;
        IReader reader = rf.getReader(pathToJson);
        String source = reader.load();
        if (!source.isEmpty()) {
            try {
                json = (Map<String, Object>) JSONValue.parseWithException(source);
            } catch (ParseException ex) {
                Logger.getLogger(Runner.class.getName()).log(Level.SEVERE, "Exception loading JSON config", ex);
            }
        } else {
            Logger.getLogger(Runner.class.getName()).severe(String.format("Unable to load JSON config %s", pathToJson));
        }
    }

    public Object getJson() {
        return json;
    }
    
}
