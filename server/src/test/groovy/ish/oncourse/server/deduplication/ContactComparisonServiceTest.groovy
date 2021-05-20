package ish.oncourse.server.deduplication

import groovy.transform.CompileStatic
import ish.oncourse.server.cayenne.Contact
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

import java.time.LocalDate

@CompileStatic
@Disabled
class ContactComparisonServiceTest {

    Contact c1, c2
    ContactComparisonService comparison

    @BeforeEach
    void setUp() throws Exception {
        this.c1 = new Contact()
        this.c2 = new Contact()
        this.comparison = new ContactComparisonService(c1, c2)
    }

    @Test
    void getScore() {
        c1.email = null
        c2.email = null
        assert comparison.getScore() == 0

        c1.email = "test@example.com"
        c2.email = "test@example.com"
        assert comparison.getScore() == 0.5263158f

        c1.birthDate = LocalDate.of(1972, 1, 8)
        c2.birthDate = LocalDate.of(1972, 1, 8)
        assert comparison.getScore() == 0.75757575f

        c1.firstName = "Robert"
        c2.firstName = "Roberta"
        assert comparison.getScore() == 0.78571427f

        c1.email = "test3@example.com"
        c2.email = "test@example.com"
        assert comparison.getScore() == 0.74489796f
    }

    @Test
    void jaccardScore() {
        assert 0.875f == ContactComparisonService.jaccardScore("something", "somethinx")
        assert 0.8888889f == ContactComparisonService.jaccardScore("something", "somethings")

        assert 0.6f == ContactComparisonService.jaccardScore("The quick brown fox", "The slow brown fox")
        assert 0.7222222f == ContactComparisonService.jaccardScore("The quick brown fox", "The fox was quick brown")

        assert 0.0f == ContactComparisonService.jaccardScore("Adam", "Robert")
    }
}
