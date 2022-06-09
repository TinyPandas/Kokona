package com.panda.annotations;

import org.tinypandas.annotations.RegisterOnStart;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@RegisterOnStart
@Retention(RetentionPolicy.RUNTIME)
public @interface Listener {
    public String name() default "";
}
