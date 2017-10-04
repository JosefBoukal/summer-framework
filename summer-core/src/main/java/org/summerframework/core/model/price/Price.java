package org.summerframework.core.model.price;

import lombok.Data;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The price with {@link #getAmount()}, {@link #getNoVat()}, {@link #getTax()} and {@link #getVat()} fields.
 *
 * @author Josef Boukal
 */
@Data
public class Price {

    /**
     * The ISO 4217 currency code.
     */
    private String currency;

    /**
     * Monetary amount either as simple or with VAT included.
     */
    private BigDecimal amount;

    /**
     * Monetary amount without VAT.
     */
    private BigDecimal noVat;

    /**
     * Returns VAT in percents, e.g. 21 for 21 %.
     */
    private BigDecimal vat;

    /**
     * Returns the tax value in the given currency.
     */
    private BigDecimal tax;

    public Price() {
    }

    /**
     * Constructor for Jackson from decimal point number.
     *
     * @param amount the price amount
     */
    public Price(Double amount) {
        this.amount = new BigDecimal(amount);
    }

    /**
     * Constructor for Jackson from int number.
     *
     * @param amount the price amount
     */
    public Price(int amount) {
        this.amount = new BigDecimal(amount);
    }

    public Price(String currency, BigDecimal amount) {
        this.currency = currency;
        this.amount = amount;
    }

    public Price(String currency,
                 BigDecimal amount,
                 BigDecimal noVat,
                 BigDecimal vat,
                 BigDecimal tax)
    {
        this.currency = currency;
        this.amount = amount;
        this.noVat = noVat;
        this.vat = vat;
        this.tax = tax;
    }

    public static Price valueOf(Map<String, ?> price) {
        if (price == null) {
            return null;
        }
        String currency = (String) price.get("currency");
        BigDecimal amount = toBigDecimal(price, "amount");
        BigDecimal vat = toBigDecimal(price, "vat");
        if (vat == null) {
            return new Price(currency, amount);
        }
        BigDecimal noVat = toBigDecimal(price, "noVat");
        BigDecimal tax = toBigDecimal(price, "tax");
        return new Price(currency, amount, noVat, vat, tax);
    }

    @SuppressWarnings("unchecked")
    public static Price valueOf(Object price) {
        if (price == null) {
            return null;
        }
        if (price instanceof Map) {
            return valueOf((Map) price);
        }
        if (price instanceof Price) {
            return (Price) price;
        }
        if (price instanceof BigDecimal) {
            return new Price(null, (BigDecimal) price);
        }
        if (price instanceof Number || price instanceof String) {
            return new Price(null, new BigDecimal(price.toString()));
        }
        throw new IllegalStateException("Creating a Price from the " + price.getClass().getName() + " type is not supported!");
    }

    private static BigDecimal toBigDecimal(Map<String, ?> price, String field) {
        Object value = price.get(field);
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        return new BigDecimal(value.toString());
    }

    /**
     * Returns this price as map. Not serialized to JSON as it is not an accessor.
     */
    public Map<String, ?> asMap() {
        Map<String, Object> result = new LinkedHashMap<>(6);
        result.put("amount", amount);
        result.put("currency", currency);
        if (noVat != null) {
            result.put("noVat", noVat);
        }
        if (vat != null) {
            result.put("vat", vat);
        }
        if (tax != null) {
            result.put("tax", tax);
        }
        return result;
    }

    /**
     * Returns a new copy of this price.
     */
    public Price copy() {
        return new Price(currency, amount, noVat, vat, tax);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (currency != null) {
            sb.append(currency);
            sb.append(' ');
        }
        if (amount != null) {
            sb.append(amount);
        } else if (noVat != null) {
            sb.append(noVat);
        } else {
            sb.append("???");
        }
        return sb.toString();
    }

}
