package com.panda.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@RegisterOnStart
@Retention(RetentionPolicy.RUNTIME)
public @interface ListenerAnnotation {
    public String name() default "";
}
