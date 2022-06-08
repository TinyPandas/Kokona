package com.panda.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@RegisterOnStart
@Retention(RetentionPolicy.RUNTIME)
public @interface Listener {
    public String name() default "";
}
