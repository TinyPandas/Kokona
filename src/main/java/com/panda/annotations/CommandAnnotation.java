package com.panda.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@RegisterOnStart
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandAnnotation {
    public String name() default "";
    public String desc() default "";
}
