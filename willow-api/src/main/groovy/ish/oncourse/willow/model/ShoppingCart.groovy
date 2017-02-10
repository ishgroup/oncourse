package ish.oncourse.willow.model

class ShoppingCart {
    String id
    Contact contact
    List<CourseClass> classes = []
    List<Product> products = []
    List<Voucher> vouchers = []
    List<Promotion> promotions = []
 }
