package com.fdzang.micro.common.jsonfmt;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JsonFmt {
    /**
     * 值
     */
    String value() default "";

    /**
     * 是否必须
     */
    boolean require() default true;
}