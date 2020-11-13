/**
 * 
 */
package main.frameWork;

public class SeoYuzukiFrameWork {
    BeanResource beanResource = new BeanResource();

    public SeoYuzukiFrameWork(Class<?> class1) {
        System.out.println("!!!!!!!" + class1.getResource("../"));

        beanResource.setWhereMainAt(class1.getResource("../").getPath().substring(1));
        beanResource.setWhereMainAtNoBin(class1.getResource("../").getPath().substring(1));

        Resources.whereMainAtNoBin = class1.getResource("../").getPath().substring(1);
        AnnotationsSetUp.ScanAnnotations(beanResource);
    }

    public BeanResource getBeanResource() {
        return beanResource;

    }

}
