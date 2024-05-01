package dmit2015.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PersonTest {

    @ParameterizedTest(name = "[{index}] {arguments}") // shown is the default name attribute value
    @CsvSource(useHeadersInDisplayName = true, textBlock = """
         FIRST_NAME,    LAST_NAME,          BIRTHDATE,      EXPECTED_AGE
         Queen,         Elizabeth II,       1926-04-21,     97
         King,          Charles III,        1948-11-14,     75
         Prince,        William of Wales,    1982-06-21,     41
         Prince,        George of Wales,    2013-07-22,     10
         Uncle,         Bob,                1952-12-05,     71
        """)
    void currentAge_knownAge_returnsAge(
            String firstName,
            String lastName,
            LocalDate birthDate,
            int expectedAge
    ) {
        var currentPerson = new Person(firstName, lastName, birthDate);
        assertEquals(expectedAge, currentPerson.currentAge());
    }

    @ParameterizedTest
    @CsvSource(useHeadersInDisplayName = true, textBlock = """
         FIRST_NAME,    LAST_NAME,          BIRTHDATE,      ON_DATE,    EXPECTED_AGE
         Queen,         Elizabeth II,       1926-04-21,     2022-09-08, 96
         King,          Charles III,        1948-11-14,     2024-02-07, 75
         Uncle,         Bob,                1952-12-05,     2024-02-07, 71
        """)
    void ageOn_futureDate_returnsAge(
            String firstName,
            String lastName,
            LocalDate birthDate,
            LocalDate onDate,
            long expectedAge
    ) {
        var currentPerson = new Person(firstName, lastName, birthDate);
        assertEquals(expectedAge, currentPerson.ageOn(onDate));
    }

    @ParameterizedTest
    @CsvSource(useHeadersInDisplayName = true, textBlock = """
        BIRTH_DATE,     EXPECTED_ANIMAL
        1900-01-01,     Rat
        1901-02-01,     Ox
        1902-03-01,     Tiger
        1903-04-01,     Rabbit
        1904-05-01,     Dragon
        1905-06-01,     Snake
        1906-07-01,     Horse
        1907-08-01,     Sheep
        1908-09-01,     Monkey
        1909-10-01,     Rooster
        1910-11-01,     Dog
        1911-12-01,     Pig
        """)
    void chineseZodiac_yearsForAllAnimals_correctAnimal(
            LocalDate birthDate,
            String expectedChineseZodiac
    ) {
        var currentPerson = new Person("Chinese","Zodiac", birthDate);
        assertEquals(expectedChineseZodiac.toUpperCase(), currentPerson.chineseZodiac().toUpperCase());
    }

    @ParameterizedTest
    @CsvSource(useHeadersInDisplayName = true, textBlock = """
        BIRTH_DATE,     EXPECTED_SIGN
        2000-03-21,     Aries
        2000-04-19,     Aries
        2000-04-20,     Taurus
        2000-05-20,     Taurus
        2000-05-21,     Gemini
        2000-06-21,     Gemini
        2000-06-22,     Cancer
        2000-07-22,     Cancer
        2000-07-23,     Leo
        2000-08-22,     Leo
        2000-08-23,     Virgo
        2000-09-22,     Virgo
        2000-09-23,     Libra
        2000-10-22,     Libra
        2000-10-23,     Scorpio
        2000-11-22,     Scorpio
        2000-11-23,     Sagittarius
        2000-12-21,     Sagittarius
        2000-12-22,     Capricorn
        2000-01-19,     Capricorn
        2000-01-20,     Aquarius
        2000-02-18,     Aquarius
        2000-02-19,     Pisces
        2000-03-20,     Pisces
        """)
    void astrologicalSign_allSignDateRanges_correctSign(
            LocalDate birthDate,
            String expectedAstrologicalSign
    ) {
        var currentPerson = new Person("Astrological","Sign", birthDate);
        assertEquals(expectedAstrologicalSign.toUpperCase(), currentPerson.astrologicalSign().toUpperCase());
    }

}