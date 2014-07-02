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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author balnave
 * @param <T>
 */
public class ClassCreator<T> {

    /**
     * Parameters of the method to add an URL to the System classes.
     */
    private static final Class<?>[] parameters = new Class[]{URL.class};

    /**
     * Adds a file to the classpath.
     *
     * @param s a String pointing to the file
     * @throws IOException
     */
    public static void addFile(String s) throws IOException {
        File f = new File(s);
        addFile(f);
    }//end method

    /**
     * Adds a file to the classpath
     *
     * @param f the file to be added
     * @throws IOException
     */
    public static void addFile(File f) throws IOException {
        addURL(f.toURI().toURL());
    }//end method

    /**
     * Adds the content pointed by the URL to the classpath.
     *
     * @param u the URL pointing to the content to be added
     * @throws IOException
     */
    public static void addURL(URL u) throws IOException {
        URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class<?> sysclass = URLClassLoader.class;
        try {
            Method method = sysclass.getDeclaredMethod("addURL", parameters);
            method.setAccessible(true);
            method.invoke(sysloader, new Object[]{u});
        } catch (IllegalAccessException t) {
            throw new IOException("Error, could not add URL to system classloader");
        } catch (IllegalArgumentException t) {
            throw new IOException("Error, could not add URL to system classloader");
        } catch (NoSuchMethodException t) {
            throw new IOException("Error, could not add URL to system classloader");
        } catch (SecurityException t) {
            throw new IOException("Error, could not add URL to system classloader");
        } catch (InvocationTargetException t) {
            throw new IOException("Error, could not add URL to system classloader");
        }
    }

    private final String className;

    public ClassCreator(String className) {
        this.className = className;
    }

    /**
     * Creates a new instance
     *
     * @param constructor
     * @param params
     * @return
     */
    public T newInstance(Constructor<T> constructor, Object... params) {
        T instance = null;
        try {
            instance = constructor.newInstance(params);
        } catch (InstantiationException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return instance;
    }

    /**
     * Get a class constructor
     *
     * @param definitionClasses
     * @return
     */
    public Constructor<T> getConstructor(Class... definitionClasses) {
        Constructor<T> constructor = null;
        Class<T> definition = getClassDefinition();
        try {
            constructor = definition == null ? null : definition.getDeclaredConstructor(definitionClasses);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(ClassCreator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(ClassCreator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return constructor;
    }

    /**
     * Loads a class
     *
     * @return
     */
    private Class<T> getClassDefinition() {
        try {
            return (Class<T>) ClassLoader.getSystemClassLoader().loadClass(className);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ClassCreator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
