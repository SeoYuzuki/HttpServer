/**
 * 
 */
package main.frameWork.annotatoins;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Context
public @interface Controller {
    public String message() default "";
}
