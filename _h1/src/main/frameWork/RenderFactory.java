/**
 * 
 */
package main.frameWork;

public class RenderFactory {
    public static RenderBean render(String type) {
        return new RenderBean(type);
    }
}
