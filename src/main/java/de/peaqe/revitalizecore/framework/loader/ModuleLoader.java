package de.peaqe.revitalizecore.framework.loader;

import de.peaqe.revitalizecore.RevitalizeCore;
import de.peaqe.revitalizecore.config.ModuleConfig;
import de.peaqe.revitalizecore.framework.annotation.RevitalizeModule;
import lombok.Getter;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 26.11.2025 | 20:35 Uhr
 * *
 */

public class ModuleLoader {

    private static final String BASE_PACKAGE = "de.peaqe.revitalizecore.modules";
    @Getter
    private static List<String> modules = new ArrayList<>();

    public static List<Object> loadModules(RevitalizeCore core) {

        var loadedModules = new ArrayList<Object>();
        var moduleConfig = new ModuleConfig(core);

        var moduleClasses = findModuleClasses(core);

        for (var clazz : moduleClasses) {

            if (!clazz.isAnnotationPresent(RevitalizeModule.class))
                continue;

            var annotation = clazz.getAnnotation(RevitalizeModule.class);
            var name = annotation.name();
            var defaultEnabled = annotation.enabledByDefault();

            if (!moduleConfig.hasModule(name)) {
                core.getLogger().info("Module added to config: " + name);
                moduleConfig.addModule(name, defaultEnabled);
                moduleConfig.save();
            }

            if (!moduleConfig.isModuleEnabled(name)) {
                core.getLogger().info("Module disabled in config: " + name);
                continue;
            }

            try {
                var instance = clazz.getDeclaredConstructor().newInstance();

                callLifecycle(instance, "onLoad", core);
                callLifecycle(instance, "onEnable", core);

                core.getLogger().info("Loaded module: " + name);
                loadedModules.add(instance);
                modules.add(name);

            } catch (Exception exception) {
                core.getLogger().severe("Cannot load module: " + clazz.getSimpleName());
                throw new RuntimeException(exception);
            }
        }

        return loadedModules;
    }

    private static void callLifecycle(Object instance, String methodName, RevitalizeCore core) {

        try {
            var method = instance.getClass().getMethod(methodName, RevitalizeCore.class);
            method.invoke(instance, core);

        } catch (NoSuchMethodException ignored) {
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private static List<Class<?>> findModuleClasses(RevitalizeCore core) {

        var result = new ArrayList<Class<?>>();
        var codeSource = core.getClass().getProtectionDomain().getCodeSource();

        if (codeSource == null) {
            core.getLogger().warning("ModuleLoader: Unable to access code source.");
            return result;
        }

        File location;
        try {
            location = new File(codeSource.getLocation().toURI());
        } catch (URISyntaxException exception) {
            core.getLogger().severe("ModuleLoader: Invalid code source URI.");
            exception.printStackTrace();
            return result;
        }

        var packagePath = BASE_PACKAGE.replace('.', '/') + "/";

        if (location.isFile() && location.getName().endsWith(".jar")) {

            try (var jar = new JarFile(location)) {

                Enumeration<JarEntry> entries = jar.entries();

                while (entries.hasMoreElements()) {

                    var entry = entries.nextElement();
                    var name = entry.getName();

                    if (entry.isDirectory()) continue;
                    if (!name.endsWith(".class")) continue;
                    if (!name.startsWith(packagePath)) continue;

                    var className = name
                            .replace('/', '.')
                            .substring(0, name.length() - 6);

                    try {
                        var clazz = Class.forName(className);
                        result.add(clazz);
                    } catch (ClassNotFoundException ignored) {}
                }

            } catch (Exception exception) {
                core.getLogger().severe("ModuleLoader: Could not read plugin JAR file.");
                throw new RuntimeException(exception);
            }

            return result;
        }

        var directory = new File(location, packagePath);
        scanDirectory(core, directory, BASE_PACKAGE, result);

        return result;
    }

    private static void scanDirectory(
            RevitalizeCore core,
            File directory,
            String currentPackage,
            List<Class<?>> result
    ) {

        if (!directory.exists()) return;

        var files = directory.listFiles();
        if (files == null) return;

        var loader = core.getClass().getClassLoader();

        for (var file : files) {

            if (file.isDirectory()) {
                scanDirectory(core, file, currentPackage + "." + file.getName(), result);
                continue;
            }

            if (!file.getName().endsWith(".class"))
                continue;

            var className = currentPackage + "." +
                    file.getName().substring(0, file.getName().length() - 6);

            try {
                var clazz = loader.loadClass(className);
                result.add(clazz);
            } catch (ClassNotFoundException ignored) {}
        }
    }
}
