package dmit2015.model;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
/**
 * Represents a Person with properties like first name, last name, and birth date.
 * Provides methods to calculate age, Chinese zodiac, and astrological sign and age on a date.
 * @author elijah
 * @version 2024-02-05
 */
@Named("Person")
@RequestScoped
public class Person {
    @NotBlank(message = "First name is required")
      String firstname;
    @NotBlank(message = "Last name is required")
      String lastname;
    @NotNull(message = "Birth date is required")
      LocalDate birthDate;
    LocalDate ageon;
      String birthDateString;
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public LocalDate getAgeon() {
        return ageon;
    }

    public void setAgeon(LocalDate ageon) {
        this.ageon = ageon;
    }

    public String getBirthDateString() {
        return birthDateString;
    }

    public void setBirthDateString(String birthDateString) {
        this.birthDateString = birthDateString;
    }
    public Person() {
    }
    public Person(String firstname, String lastname, LocalDate birthDate) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.birthDate = birthDate;
        this.birthDateString = birthDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public int currentAge() {
        LocalDate currentDate = LocalDate.now();
        int age = (int) ChronoUnit.YEARS.between(birthDate, currentDate);
        String message = firstname+ " " + lastname + " age: " + age;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, null));
        return age;
    }

    public int ageOn(LocalDate onDate) {
        int age = (int) ChronoUnit.YEARS.between(birthDate, onDate);
        String message = firstname+ " " + lastname + " age on the date of " + ageon + " is: " + age;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, null));
        return age;
    }

    public String chineseZodiac() {

        String zodiac = switch (birthDate.getYear() % 12) {
            case 0 -> "Monkey";
            case 1 -> "Rooster";
            case 2 -> "Dog";
            case 3 -> "Pig";
            case 4 -> "Rat";
            case 5 -> "Ox";
            case 6 -> "Tiger";
            case 7 -> "Rabbit";
            case 8 -> "Dragon";
            case 9 -> "Snake";
            case 10 -> "Horse";
            case 11 -> "Sheep";
            default -> "";
        };
        String message = firstname + " " + lastname + " Chinese Zodiac is: " + zodiac;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, null));
        return zodiac;
    }

    public String astrologicalSign() {
        String astro_sign = "";
        String month = birthDate.getMonth().name();

        switch (month) {
            case "DECEMBER" -> {

                if (birthDate.getDayOfMonth() < 22)
                    astro_sign = "Sagittarius";
                else
                    astro_sign = "capricorn";
            }
            case "JANUARY" -> {
                if (birthDate.getDayOfMonth() < 20)
                    astro_sign = "Capricorn";
                else
                    astro_sign = "aquarius";
            }
            case "FEBRUARY" -> {
                if (birthDate.getDayOfMonth() < 19)
                    astro_sign = "Aquarius";
                else
                    astro_sign = "pisces";
            }
            case "MARCH" -> {
                if (birthDate.getDayOfMonth() < 21)
                    astro_sign = "Pisces";
                else
                    astro_sign = "aries";
            }
            case "APRIL" -> {
                if (birthDate.getDayOfMonth() < 20)
                    astro_sign = "Aries";
                else
                    astro_sign = "taurus";
            }
            case "MAY" -> {
                if (birthDate.getDayOfMonth() < 21)
                    astro_sign = "Taurus";
                else
                    astro_sign = "gemini";
            }
            case "JUNE" -> {
                if (birthDate.getDayOfMonth() < 22)
                    astro_sign = "Gemini";
                else
                    astro_sign = "cancer";
            }
            case "JULY" -> {
                if (birthDate.getDayOfMonth() < 23)
                    astro_sign = "Cancer";
                else
                    astro_sign = "leo";
            }
            case "AUGUST" -> {
                if (birthDate.getDayOfMonth() < 23)
                    astro_sign = "Leo";
                else
                    astro_sign = "virgo";
            }
            case "SEPTEMBER" -> {
                if (birthDate.getDayOfMonth() < 23)
                    astro_sign = "Virgo";
                else
                    astro_sign = "libra";
            }
            case "OCTOBER" -> {
                if (birthDate.getDayOfMonth() < 23)
                    astro_sign = "Libra";
                else
                    astro_sign = "scorpio";
            }
            case "NOVEMBER" -> {
                if (birthDate.getDayOfMonth() < 23)
                    astro_sign = "scorpio";
                else
                    astro_sign = "sagittarius";
            }
        }
        String message = firstname + " " + lastname + " astrological sign is: " + astro_sign;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, null));

        return astro_sign;
    }
}



