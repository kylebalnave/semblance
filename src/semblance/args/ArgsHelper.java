/*
 * Copyright (C) 2014 kyleb2
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package semblance.args;

/**
 *
 * @author kyleb2
 */
public class ArgsHelper {
    
    /**
     * Gets the first arg flag matching
     * @param args
     * @param flagNames
     * @return 
     */
    public static String getFirstArgMatching(String[] args, String[] flagNames) {
        String result = "";
        for (String flagName : flagNames) {
            result = getArgMatching(args, flagName);
            if(!result.isEmpty()) {
                break;
            }
        }
        return result;
    }
    
    /**
     * Gets the first arg flag matching with default fallback
     * @param args
     * @param flagNames
     * @param defaultValue
     * @return 
     */
    public static String getFirstArgMatching(String[] args, String[] flagNames, String defaultValue) {
        String result = getFirstArgMatching(args, flagNames);
        return result.isEmpty() ? defaultValue : result;
    }
    
    /**
     * Gets an arg matching
     * @param args
     * @param flagName
     * @return 
     */
    public static String getArgMatching(String[] args, String flagName) {
        String result = "";
        int argIndex = 0;
        for (String arg : args) {
            if (arg.equalsIgnoreCase(flagName) && args.length >= argIndex + 1) {
                result = args[argIndex + 1];
                break;
            }
            argIndex++;
        }
        return result;
    }
    
    /**
     * Gets an arg matching with default fallback
     * @param args
     * @param flagName
     * @return 
     */
    public static String getArgMatching(String[] args, String flagName, String defaultValue) {
        String result = getArgMatching(args, flagName);
        return result.isEmpty() ? defaultValue : result;
    }
    
}
