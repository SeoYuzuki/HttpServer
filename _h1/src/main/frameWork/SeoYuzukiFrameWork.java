/**
 * 
 */
package main.frameWork;

import main.frameWork.beans.BeanResource;

public class SeoYuzukiFrameWork {
    BeanResource beanResource = new BeanResource();

    public SeoYuzukiFrameWork(Class<?> class1) {
        // System.out.println("!!!!!!!" + class1.getResource("../"));

        beanResource.setWhereMainAt(class1.getResource("../").getPath().substring(1));
        System.out.println("!!!" + class1.getResource("../").getPath().substring(1).replace("/bin/", "/src/"));
        beanResource.setWhereMainAtNoBin(class1.getResource("../").getPath().substring(1).replace("/bin/", "/src/"));

        AnnotationsSetUp.ScanAnnotations(beanResource);
    }

    public BeanResource getBeanResource() {
        return beanResource;

    }

}
