package com.fdzang.micro.common.jsonfmt;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(JsonFmtConfig.class)
public @interface EnableJsonFmt {
}
