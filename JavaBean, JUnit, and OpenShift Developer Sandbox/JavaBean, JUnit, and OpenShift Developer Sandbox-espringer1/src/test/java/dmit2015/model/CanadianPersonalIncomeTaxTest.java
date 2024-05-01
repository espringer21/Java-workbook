package dmit2015.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class CanadianPersonalIncomeTaxTest {
    private static final double DELTA = 0.50;

    @Test
    public void testFederalIncomeTax() {
        String[] taxableIncomes = {"26679.50", "53359.00", "80038.00", "106717.00", "136073.50", "165430.00",
                "200552.50", "235675.00", "471350.00"};
        String[] expectedTaxAmounts = {"4001.93", "8003.85", "13473.05", "18942.24", "26574.93", "34207.62",
                "44393.15", "54578.67", "132351.42"};

        for (int i = 0; i < taxableIncomes.length; i++) {
            double taxableIncome = Double.parseDouble(taxableIncomes[i]);
            double expectedTaxAmount = Double.parseDouble(expectedTaxAmounts[i]);
            CanadianPersonalIncomeTax taxCalculator = new CanadianPersonalIncomeTax(2023, taxableIncome, "AB");
            double actualTaxAmount = taxCalculator.federalIncomeTax();
            assertEquals(expectedTaxAmount, actualTaxAmount, DELTA);
        }
    }

    @Test
    public void testProvincialIncomeTax() {
        String[] taxableIncomes = {"71146.00", "142292.00", "156521.50", "170751.00", "199209.50", "227668.00",
                "284585.00", "341502.00", "512253.00"};
        String[] expectedTaxAmounts = {"7114.60", "14229.20", "15936.74", "17644.28", "21343.89", "25043.49",
                "33011.87", "40980.25", "66592.90"};

        for (int i = 0; i < taxableIncomes.length; i++) {
            double taxableIncome = Double.parseDouble(taxableIncomes[i]);
            double expectedTaxAmount = Double.parseDouble(expectedTaxAmounts[i]);
            CanadianPersonalIncomeTax taxCalculator = new CanadianPersonalIncomeTax(2023, taxableIncome, "AB");
            double actualTaxAmount = taxCalculator.provincialIncomeTax();
            assertEquals(expectedTaxAmount, actualTaxAmount, DELTA);
        }
    }
}
