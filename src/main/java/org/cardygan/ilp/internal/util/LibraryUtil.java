package org.cardygan.ilp.internal.util;

import java.io.File;
import java.lang.reflect.Field;

public final class LibraryUtil {

    /**
     * Load user library from given path.
     *
     * @param libraryPath the absolute path to load user library from.
     */
    public static void loadLibraryFromPath(String libraryPath) {
        try {
            System.setProperty("java.library.path", System.getProperty("java.library.path")
                    + File.pathSeparator + libraryPath);
            Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
            fieldSysPath.setAccessible(true);
            fieldSysPath.set(null, null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            System.err.println("Could not load library from path " + libraryPath);
            e.printStackTrace();
        }
    }
}
