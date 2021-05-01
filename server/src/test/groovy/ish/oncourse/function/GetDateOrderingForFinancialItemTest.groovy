package ish.oncourse.function

import groovy.transform.CompileStatic
import ish.oncourse.cayenne.FinancialItem
import org.apache.cayenne.query.Ordering
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito

import java.time.LocalDate

@CompileStatic
class GetDateOrderingForFinancialItemTest {

    private FinancialItem item1
    private FinancialItem item2
    private FinancialItem item3
    private FinancialItem item4

    @Test
    void testSorting() {
        List<FinancialItem> financialItems = prepareList()

        Ordering.orderList(financialItems, GetDateOrderingForFinancialItem.valueOf().get())

        Assertions.assertEquals(item4, financialItems.get(0))
        Assertions.assertEquals(item2, financialItems.get(1))
        Assertions.assertEquals(item1, financialItems.get(2))
        Assertions.assertEquals(item3, financialItems.get(3))

        Ordering.orderList(financialItems, GetDateOrderingForFinancialItem.valueOf(false).get())

        Assertions.assertEquals(item3, financialItems.get(0))
        Assertions.assertEquals(item1, financialItems.get(1))
        Assertions.assertEquals(item2, financialItems.get(2))
        Assertions.assertEquals(item4, financialItems.get(3))
    }

    private List<FinancialItem> prepareList() {
        ArrayList<FinancialItem> list = new ArrayList<>()

        LocalDate now = LocalDate.now()
        Calendar calendar = Calendar.getInstance()
        calendar.setTime(new Date())
        calendar.add(Calendar.DAY_OF_MONTH, -10)

        item1 = Mockito.mock(FinancialItem.class)
        Mockito.when(item1.getDate()).thenReturn(now.minusDays(10))
        Mockito.when(item1.getCreatedOn()).thenReturn(calendar.getTime())
        list.add(item1)

        item2 = Mockito.mock(FinancialItem.class)
        Mockito.when(item2.getDate()).thenReturn(now.minusDays(15))
        Mockito.when(item2.getCreatedOn()).thenReturn(calendar.getTime())
        list.add(item2)

        calendar.add(Calendar.DAY_OF_MONTH, 2)

        item3 = Mockito.mock(FinancialItem.class)
        Mockito.when(item3.getDate()).thenReturn(now.minusDays(10))
        Mockito.when(item3.getCreatedOn()).thenReturn(calendar.getTime())
        list.add(item3)

        item4 = Mockito.mock(FinancialItem.class)
        Mockito.when(item4.getDate()).thenReturn(now.minusDays(20))
        Mockito.when(item4.getCreatedOn()).thenReturn(calendar.getTime())
        list.add(item4)

        return list
    }
}
