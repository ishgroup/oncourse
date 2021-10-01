package ish.math

import groovy.transform.CompileStatic

@CompileStatic
class DoubleMethods {
    static Money toMoney(Number self) { 
        new Money(self.toBigDecimal())
    }
}
