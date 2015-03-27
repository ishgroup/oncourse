package ish.oncourse.ui.components;

import ish.oncourse.model.Product;
import ish.oncourse.ui.utils.CourseItemModel;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class RelatedPorducts {
    @Property
    @Parameter(required = true)
    private CourseItemModel model;

    @Property
    private Product product;

    @Property
    private List<Product> products;

    @SetupRender
    public void beforeRender() {
        products = model.getRelatedProducts();
        Collections.sort(products, new Comparator<Product>() {
            @Override
            public int compare(Product o1, Product o2) {
                if (o1 != null && o2 != null && o1.getName() != null) {
                    return o1.getName().compareTo(o2.getName());
                }
                return 0;
            }
        });
    }
}
