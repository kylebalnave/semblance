/*
 * Copyright (C) 2014 balnave
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
package semblance.reflection;

import java.lang.reflect.Constructor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author balnave
 * @param <T>
 */
public class ClassCreator<T> {

    private final String className;

    public ClassCreator(String className) {
        this.className = className;
    }

    /**
     * Get a class constructor
     * @param definitionClasses
     * @return 
     */
    public Constructor<T> getConstructor(Class... definitionClasses) {
        Constructor<T> constructor;
        Class<T> definition = getClassDefinition();
        try {
            constructor = definition == null ? null : definition.getDeclaredConstructor(definitionClasses);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(ClassCreator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(ClassCreator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private Class<T> getClassDefinition() {
        try {
            return (Class<T>) Class.forName(className);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ClassCreator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
