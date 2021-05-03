package ish.oncourse.server.deduplication

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import ish.oncourse.server.cayenne.Contact
import org.junit.jupiter.api.Test

import java.time.LocalDate

@CompileStatic
        class DuplicateFinderTest {

    @Test
    void ngram() {
        assert ["The big", "big bad", "bad fox"] == DuplicateFinder.ngram("The big bad fox")
        assert ["The big", "big bad", "bad fox"] == DuplicateFinder.ngram("The big, bad fox.")
        assert ["The big bad", "big bad fox"] == DuplicateFinder.ngram("The big bad fox", 3)
        assert ["The big bad fox"] == DuplicateFinder.ngram("The big bad fox", 4)
        assert ["The big bad fox"] == DuplicateFinder.ngram("The big bad fox", 5)
    }

    @Test
    void ngram_chars() {
        assert ['Th', 'he', 'eb', 'bi', 'ig', 'gb', 'ba', 'ad', 'df', 'fo', 'ox'] == DuplicateFinder.ngram("The big bad fox", 2, true)
        assert ['Th', 'he', 'eb', 'bi', 'ig', 'gb', 'ba', 'ad', 'df', 'fo', 'ox'] == DuplicateFinder.ngram("The big, bad fox.", 2, true)
        assert ['The', 'heb', 'ebi', 'big', 'igb', 'gba', 'bad', 'adf', 'dfo', 'fox'] == DuplicateFinder.ngram("The big bad fox", 3, true)
        assert ['Theb', 'hebi', 'ebig', 'bigb', 'igba', 'gbad', 'badf', 'adfo', 'dfox'] == DuplicateFinder.ngram("The big bad fox", 4, true)
        assert ["The big bad fox"] == DuplicateFinder.ngram("The big bad fox", 25, true)
    }

    @Test
    void addToken() {
        def finder = new DuplicateFinder()

        finder.addToken("abc")
        assert [1, 1, 1, -1, -1, -1, 1, 1, -1, -1, -1, -1, -1, -1, -1, -1, 1, -1, 1, 1, -1, -1, -1, 1, 1, -1, -1, -1, -1, -1, -1, -1] == finder.getBits()

        finder.addToken("1234")
        assert [2, 2, 0, 0, -2, -2, 2, 2, -2, -2, -2, -2, -2, -2, -2, -2, 0, -2, 0, 2, 0, 0, 0, 2, 2, -2, -2, -2, -2, -2, -2, -2] == finder.getBits()

        finder.addToken(DuplicateFinder.ngram("The sun was shining on the sea, shining with all its might."))
        assert 132973514 == finder.getHash()

        finder.addToken(null)
        assert 132973514 == finder.getHash()

        finder.addToken("xyz", 5)
        assert 35062030 == finder.getHash()
    }

    
    @Test
    void build() {
        def finder = new DuplicateFinder()

        def c = new Contact()
        c.birthDate = new LocalDate(1972, 1, 8)
        finder.build(c)
        assert 183239159 == finder.getHash()

        c.firstName = "Robert"
        finder.build(c)
        assert 15466679 == finder.getHash()
    }

    @Test
    void getDistance() {
        assert 0 == DuplicateFinder.getDistance(0b10101111, 0b10101111)
        assert 1 == DuplicateFinder.getDistance(0b101011110, 0b101011111)
        assert 2 == DuplicateFinder.getDistance(0b1010111100, 0b1010111111)
    }
}