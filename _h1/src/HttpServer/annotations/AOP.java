/**
 * 
 */
package HttpServer.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface AOP {
    public Class<?> message();
}
