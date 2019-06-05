package pl.edu.agh.financeservice.model.user.postgres;

public enum Currency {
    EUR("EUR"),
    USD("USD"),
    GBP("GBP"),
    CHF("CHF");

    private String currency;

    Currency(String currency) {
        this.currency = currency;
    }

    public String getCurrency() {
        return currency;
    }

}
