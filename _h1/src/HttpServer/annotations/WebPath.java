/**
 * 
 */
package HttpServer.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface WebPath {

    public String route() default "";

    public String methed() default "";
}
