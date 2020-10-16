/**
 * 
 */
package main.frameWork.annotatoins;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface WsOnMessage {
    public String TypeOfFrame() default "text";
}
