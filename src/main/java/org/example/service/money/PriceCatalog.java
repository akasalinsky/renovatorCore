package org.example.service.money;

import org.example.service.FloorType;

import java.math.BigDecimal;
import java.util.Objects;

public class PriceCatalog {
    private final Currency currency;
    private final BigDecimal plasteringPrice;
    private final BigDecimal primingPrice;
    private final BigDecimal wallpaperingPrice;
    private final BigDecimal floorPouringPrice;
    private final BigDecimal linoleumPrice;
    private final BigDecimal laminatePrice;
    private final BigDecimal skirtingPrice;
    private final BigDecimal ceilingPrice;

    public PriceCatalog(
            Currency currency,
            BigDecimal plasteringPrice,
            BigDecimal primingPrice,
            BigDecimal wallpaperingPrice,
            BigDecimal floorPouringPrice,
            BigDecimal linoleumPrice,
            BigDecimal laminatePrice,
            BigDecimal skirtingPrice,
            BigDecimal ceilingPrice) {
        Objects.requireNonNull(currency, "currency cannot be null");
        validateNonNegative(plasteringPrice, "plasteringPrice");
        validateNonNegative(primingPrice, "primingPrice");
        validateNonNegative(wallpaperingPrice, "wallpaperingPrice");
        validateNonNegative(floorPouringPrice, "floorPouringPrice");
        validateNonNegative(linoleumPrice, "linoleumPrice");
        validateNonNegative(laminatePrice, "laminatePrice");
        validateNonNegative(skirtingPrice, "skirtingPrice");
        validateNonNegative(ceilingPrice, "ceilingPrice");

        this.currency = currency;
        this.plasteringPrice = plasteringPrice;
        this.primingPrice = primingPrice;
        this.wallpaperingPrice = wallpaperingPrice;
        this.floorPouringPrice = floorPouringPrice;
        this.linoleumPrice = linoleumPrice;
        this.laminatePrice = laminatePrice;
        this.skirtingPrice = skirtingPrice;
        this.ceilingPrice = ceilingPrice;
    }

    private void validateNonNegative(BigDecimal value, String name) {
        Objects.requireNonNull(value, name + " cannot be null");
        if (value.signum() < 0) {
            throw new IllegalArgumentException(name + " cannot be negative: " + value);
        }
    }

    public static PriceCatalog defaultRub() {
        return new PriceCatalog(
                Currency.RUB,
                new BigDecimal("500"),
                new BigDecimal("150"),
                new BigDecimal("300"),
                new BigDecimal("400"),
                new BigDecimal("350"),
                new BigDecimal("600"),
                new BigDecimal("200"),
                new BigDecimal("700")
        );
    }

    public static PriceCatalog defaultUsd() {
        return new PriceCatalog(
                Currency.USD,
                new BigDecimal("15"),
                new BigDecimal("5"),
                new BigDecimal("10"),
                new BigDecimal("12"),
                new BigDecimal("10"),
                new BigDecimal("18"),
                new BigDecimal("6"),
                new BigDecimal("20")
        );
    }

    // Getters
    public Currency getCurrency() { return currency; }
    public BigDecimal getPlasteringPrice() { return plasteringPrice; }
    public BigDecimal getPrimingPrice() { return primingPrice; }
    public BigDecimal getWallpaperingPrice() { return wallpaperingPrice; }
    public BigDecimal getFloorPouringPrice() { return floorPouringPrice; }
    public BigDecimal getLinoleumPrice() { return linoleumPrice; }
    public BigDecimal getLaminatePrice() { return laminatePrice; }
    public BigDecimal getSkirtingPrice() { return skirtingPrice; }
    public BigDecimal getCeilingPrice() { return ceilingPrice; }

    // Aggregate prices (не используются в расчётах, но могут пригодиться)
    public BigDecimal getWallPrice() {
        return getPlasteringPrice().add(getPrimingPrice()).add(getWallpaperingPrice());
    }
    public BigDecimal getFloorPriceWithLinoleum() {
        return getFloorPouringPrice()
                .add(getLinoleumPrice())
                .add(getSkirtingPrice())
                .add(getCeilingPrice());
    }
    public BigDecimal getFloorPriceWithLaminate() {
        return getFloorPouringPrice()
                .add(getLaminatePrice())
                .add(getSkirtingPrice())
                .add(getCeilingPrice());
    }
    public BigDecimal getTotalBudget(FloorType floorType) {
        switch (floorType) {
            case LAMINATE -> { return getWallPrice().add(getFloorPriceWithLaminate()); }
            case LINOLEUM -> { return getWallPrice().add(getFloorPriceWithLinoleum()); }
            default -> throw new IllegalArgumentException("Unknown floor type: " + floorType);
        }
    }

    // equals & hashCode для корректного сравнения стандартных каталогов
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PriceCatalog that = (PriceCatalog) o;
        return currency == that.currency &&
                Objects.equals(plasteringPrice, that.plasteringPrice) &&
                Objects.equals(primingPrice, that.primingPrice) &&
                Objects.equals(wallpaperingPrice, that.wallpaperingPrice) &&
                Objects.equals(floorPouringPrice, that.floorPouringPrice) &&
                Objects.equals(linoleumPrice, that.linoleumPrice) &&
                Objects.equals(laminatePrice, that.laminatePrice) &&
                Objects.equals(skirtingPrice, that.skirtingPrice) &&
                Objects.equals(ceilingPrice, that.ceilingPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currency, plasteringPrice, primingPrice, wallpaperingPrice,
                floorPouringPrice, linoleumPrice, laminatePrice, skirtingPrice, ceilingPrice);
    }
}