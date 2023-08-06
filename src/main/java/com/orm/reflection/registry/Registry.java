package com.orm.reflection.registry;

import com.orm.reflection.accessor.Accessor;
import com.orm.reflection.annotator.Annotator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static com.orm.annotations.Annotations.DEFAULT;

//todo: javadoc
public class Registry {
    private static final Map<Key, Object> getRegistryCache = new ConcurrentHashMap<>();
    private static final Map<Key, Object> getMethodRegistryCache = new ConcurrentHashMap<>();

    public static synchronized <T extends Annotable> T get(Class<T> clazz, Object varInput) throws Throwable {
        return get(clazz, varInput, DEFAULT);
    }

    public static synchronized <T extends Annotable, A extends Annotation> T get(Class<T> clazz, Object varInput, String view) {
        Key key = new Key("get", clazz, varInput, view);
        // check cache
        T result = (T) getRegistryCache.get(key);
        if (result == null) {
            Class<A> configureAnnotationType = getConfigureType(clazz);
            // look for the needed annotation
            A configureAnnotation = Annotator.getAnnotationForView(varInput, configureAnnotationType, view, true);
            // create and configure it
            result = createAndConfigureInstance(clazz, configureAnnotation, varInput, null);
            getRegistryCache.put(key, result);
        }
        return result;
    }


    public static <T extends Annotable, A extends Annotation> T createAndConfigureFromMethod(Class<T> baseClass, A cfg, String operation, Object varInput) {
        Key key = new Key("getActualClass", baseClass, cfg.annotationType(), operation);
        // check cache
        Class<T> actualClass = (Class<T>) getMethodRegistryCache.get(key);
        if (actualClass == null) {
            //extract class
            actualClass = getActualClass(baseClass, cfg, operation);
            // cache it
            getMethodRegistryCache.put(key, actualClass);
        }
        // check if operation is enabled
        if (!checkEnabled(cfg, operation)) return null;
        // build instance
        return createAndConfigureInstance(actualClass, cfg, varInput, operation);
    }

    private static <T extends Annotable, A extends Annotation> Class<T> getActualClass(Class<T> baseClass, A cfg, String operation) {

        Class<T> actualClass = Annotator.of(cfg)
                                        .getAccessor(operation)
                                        .getAs(cfg, Class.class);
        if (!baseClass.isAssignableFrom(actualClass)) {
            throw new ClassCastException("Method " + operation + "(...) in " + cfg.annotationType()
                                                                                  .getSimpleName() + " does not return a " + baseClass.getSimpleName() + "!");
        }
        return actualClass;
    }

    private static <T extends Annotable, A extends Annotation> T createAndConfigureInstance(Class<T> ofClass, A cfg, Object varInput, String operation) {
        T result = newInstance(ofClass);
        if (varInput == null) {
            result.configure(cfg, null, null, null, null, operation);
        } else if (varInput instanceof Class) {
            result.configure(cfg, (Class<?>) varInput, null, null, null, operation);
        } else if (varInput instanceof Field) {
            Field onField = (Field) varInput;
            result.configure(cfg, onField.getDeclaringClass(), onField, null, null, operation);
        } else if (varInput instanceof Method) {
            Method onMethod = (Method) varInput;
            result.configure(cfg, onMethod.getDeclaringClass(), null, onMethod, null, operation);
        } else if (varInput instanceof Accessor<?>) {
            Accessor<?> onAccessor = ((Accessor<?>) varInput);
            result.configure(cfg, onAccessor.getDeclaringClass(), null, null, onAccessor, operation);
        } else {
            result.configure(cfg, varInput.getClass(), null, null, null, operation);
        }
        return result;
    }

    private static <A extends Annotation> boolean checkEnabled(A cfg, String annotMethod) {
        return Stream.of(Annotator.of(cfg)
                                  .getAccessor("enabledOn")
                                  .getAs(cfg, String[].class))
                     .anyMatch(annotMethod::equals);
    }

    private static <T> T newInstance(Class<T> ofClass) {
        try {
            Constructor<T> constructor = ofClass.getConstructor();
            if (constructor == null) {
                throw new RuntimeException(" Class " + ofClass.getSimpleName() + " does not have empty constructor!");
            }
            return constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T extends Annotable, A extends Annotation> Class<A> getConfigureType(Class<T> clazz) {
        Class<A> configureAnnotationType = null;
        // try to read required annotation using ConfiguredBy annotation
        ConfiguredBy configuredBy = Annotator.of(clazz)
                                             .getAnnotation(ConfiguredBy.class, false);
        if (configuredBy != null) {
            // if annotation exists, we use it
            configureAnnotationType = (Class<A>) configuredBy.value();
        } else {
            // otherwise extract the annotation from the implemented interfaces
            //todo: refactor this and use helper
            configureAnnotationType = (Class<A>) Annotator.of(clazz)
                                                          .getParameterizedTypesForImplementedInterface(Annotable.class, false)[0];
        }
        return configureAnnotationType;
    }
}
