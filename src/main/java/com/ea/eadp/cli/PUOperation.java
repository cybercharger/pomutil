package com.ea.eadp.cli;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by chriskang on 12/22/2016.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PUOperation {
    Class<? extends PomUtilOption> option();
    String operationName() default "";
    String description() default "";
}
