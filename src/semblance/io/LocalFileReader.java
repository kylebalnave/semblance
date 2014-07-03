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
package semblance.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 *
 * @author balnave
 */
public class LocalFileReader extends AbstractReader implements IReader {

    private String message;

    public LocalFileReader(String uri) {
        super(uri);
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String load() {
        String result = "";
        try {
            FileInputStream fis = new FileInputStream(new File(uri));
            result = readInputStream(fis);
        } catch (FileNotFoundException ex) {
            this.message = ex.getMessage();
        }
        return result;
    }

}
