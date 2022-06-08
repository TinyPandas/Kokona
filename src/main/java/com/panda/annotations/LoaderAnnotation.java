package com.panda.annotations;

import com.panda.utility.Loader;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface LoaderAnnotation {
    @Deprecated
    String path() default "";
    Class<? extends Loader> loader() default Loader.class;
}
