package dmit2015.model;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

/**
 * Represents a CanadianPersonalIncomeTax with properties like tax year , taxable income , and province.
 * Provides methods to calculate provincial tax, federal tax , and total lab.
 * @author elijah
 * @version 2024-02-05
 */
@Named("canadianPersonalIncomeTax")
@RequestScoped
public class CanadianPersonalIncomeTax {
     int taxYear;
     double taxableIncome;
    String province ;

    public int getTaxYear() {
        return taxYear;
    }

    public void setTaxYear(int taxYear) {
        this.taxYear = taxYear;
    }

    public double getTaxableIncome() {
        return taxableIncome;
    }

    public void setTaxableIncome(double taxableIncome) {
        this.taxableIncome = taxableIncome;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public CanadianPersonalIncomeTax(){

    }
    public CanadianPersonalIncomeTax(int taxYear, double taxableIncome, String province) {
        this.taxYear = taxYear;
        this.taxableIncome = taxableIncome;
        this.province = province;
    }
    public double federalIncomeTax() {
        double taxAmount;

        if (taxableIncome <= 53359) {
            taxAmount = taxableIncome * 0.15;
        } else if (taxableIncome <= 106717) {
            taxAmount = 53359 * 0.15 + (taxableIncome - 53359) * 0.205;
        } else if (taxableIncome <= 165430) {
            taxAmount = 53359 * 0.15 + (106717 - 53359) * 0.205 + (taxableIncome - 106717) * 0.26;
        } else if (taxableIncome <= 235675) {
            taxAmount = 53359 * 0.15 + (106717 - 53359) * 0.205 + (165430 - 106717) * 0.26 + (taxableIncome - 165430) * 0.29;
        } else {
            taxAmount = 53359 * 0.15 + (106717 - 53359) * 0.205 + (165430 - 106717) * 0.26 + (235675 - 165430) * 0.29 + (taxableIncome - 235675) * 0.33;
        }
        String message = "The federal income tax for the year of "+ taxYear +" is $" + taxAmount;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, null));

        return taxAmount;
    }

    public double provincialIncomeTax() {
        double taxAmount ;

        if (taxableIncome <= 142292) {
            taxAmount = taxableIncome * 0.10;
        } else if (taxableIncome <= 170751) {
            taxAmount = 142292 * 0.10 + (taxableIncome - 142292) * 0.12;
        } else if (taxableIncome <= 227668) {
            taxAmount = 142292 * 0.10 + (170751 - 142292) * 0.12 + (taxableIncome - 170751) * 0.13;
        } else if (taxableIncome <= 341502) {
            taxAmount = 142292 * 0.10 + (170751 - 142292) * 0.12 + (227668 - 170751) * 0.13 + (taxableIncome - 227668) * 0.14;
        } else {
            taxAmount = 142292 * 0.10 + (170751 - 142292) * 0.12 + (227668 - 170751) * 0.13 + (341502 - 227668) * 0.14 + (taxableIncome - 341502) * 0.15;
        }

        String message = "The provincial income tax for the year of "+ taxYear +" is $" + taxAmount;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, null));

        return taxAmount;
    }

    public double totalIncomeTax() {

        double total = federalIncomeTax() + provincialIncomeTax();
        String message = "The total Income tax for the year of "+ taxYear +" is $" + total;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, null));

        return total;
    }


}
