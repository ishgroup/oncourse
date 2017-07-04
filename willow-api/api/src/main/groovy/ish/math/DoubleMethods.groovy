package ish.math

class DoubleMethods {
    static Money toMoney(Number self) { 
        new Money(self.toBigDecimal())
    }
}
