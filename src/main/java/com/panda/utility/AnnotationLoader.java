package com.panda.utility;

import com.panda.annotations.LoaderAnnotation;
import com.panda.annotations.RegisterOnStart;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AnnotationLoader {

    private static final Logger logger = LoggerFactory.getLogger(AnnotationLoader.class.getName());
    private static final Reflections reflections = new Reflections("com.panda");
    private static final Map<String, Loader> loaders = new HashMap<>();

    static {
        Set<Class<?>> loaderClasses = reflections.getTypesAnnotatedWith(LoaderAnnotation.class);
        logger.info("Registering " + loaderClasses.size() + " loaders.");
        loaderClasses.forEach(loaderClass -> {
            LoaderAnnotation annotation = loaderClass.getAnnotation(LoaderAnnotation.class);
            if (annotation.loader() != Loader.class) return;
            String clazz = loaderClass.getName();
            Loader loader = null;
            try {
                loader = (Loader) loaderClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                logger.warn("Failed to register loader for " + clazz);
                logger.warn(e.getMessage());
            }
            loaders.put(clazz, loader);
            logger.info("Registered loader: " + loader + ".");
        });
    }

    public static void onStartRegister() {
        Set<Class<?>> onStartClasses = reflections.getTypesAnnotatedWith(RegisterOnStart.class).stream()
                .filter(clazz -> clazz.getPackageName().contains("annotations"))
                .collect(Collectors.toSet());
        logger.info("Annotations found for onStart registration: " + onStartClasses.size());
        onStartClasses.forEach(onStartClass -> {
            Class<? extends Annotation> annotation = onStartClass.asSubclass(Annotation.class);
            logger.info("Loading classes annotated with '" + annotation + "'");
            Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(annotation);
            logger.info("Classes with " + annotation + ": " + annotatedClasses.size());

            annotatedClasses.forEach(annotatedClass -> {
                logger.info("Loading: " + annotatedClass);
                LoaderAnnotation loaderAnnotation = annotatedClass.getAnnotation(LoaderAnnotation.class);
                if (loaderAnnotation == null) {
                    logger.warn("Class annotated with " + annotation + " does not have a defined loader.");
                    return;
                }

                Class<? extends Loader> loaderDefinition = loaderAnnotation.loader();
                Loader loader = loaders.get(loaderDefinition.getName());
                if (loader == null) {
                    logger.warn("Loader for '" + loaderDefinition + "' was not defined.");
                    return;
                }
                loader.registerClass(annotatedClass);
            });
        });
    }
}
