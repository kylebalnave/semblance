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

/**
 * Creates Readers
 * @author balnave
 */
public class ReaderFactory {
    
    /**
     * Creates an appropriate IReader
     * @param urlOrFilePath
     * @return
     */
    public static IReader getReader(String urlOrFilePath) {
        IReader reader;
        if(startsWithHttpOrHttps(urlOrFilePath)) {
            reader = new URLReader(urlOrFilePath);
        } else {
            reader = new LocalFileReader(urlOrFilePath);
        }
        return reader;
    }
    
    /**
     * Internal used to detect URLs
     * @param urlOrFilePath
     * @return 
     */
    private static boolean startsWithHttpOrHttps(String urlOrFilePath) {
        urlOrFilePath = urlOrFilePath.toLowerCase();
        return urlOrFilePath.startsWith("http://") || urlOrFilePath.startsWith("https://");
    }
    
}
