package me.lauriichan.data.xdat;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(FIELD)
public @interface XData {

    String ioId() default XDatPrimitiveIO.ID;

    String[] ioArgs() default {};

}
