/*
 * Copyright (C) 2014 kyleb2
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
package io;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author kyleb2
 */
public class FileUtils {

    /**
     * lists all files in a flat Dir
     *
     * @param path
     * @return
     */
    public static Map<String, File> listFiles(String path) {
        File root = new File(path);
        File[] list = root.listFiles();
        Map<String, File> matched = new HashMap<String, File>();
        for (File f : list) {
            if (f.isFile()) {
                matched.put(f.getName(), f);
            }
        }
        return matched;
    }

    /**
     * lists all folders
     *
     * @param path
     * @return
     */
    public static List<File> listFolders(String path) {
        File root = new File(path);
        File[] list = root.listFiles();
        List<File> matched = new ArrayList<File>();
        if (list != null) {
            for (File f : list) {
                // expecting a directory name using timestamp
                if (f.isDirectory()) {
                    matched.add(f);
                }
            }
        }
        return matched;
    }
    
    public static List<File> listFolders(File path) {
        return listFolders(path.getAbsolutePath());
    }
}
