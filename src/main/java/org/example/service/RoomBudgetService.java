package org.example.service;

import org.example.model.Room;
import org.example.service.money.Currency;
import org.example.service.money.Money;
import org.example.service.money.PriceCatalog;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.Objects;

public class RoomBudgetService {

    private final PriceCatalog providedCatalog;
    private final RoomCalculationService calcService;

    public RoomBudgetService(PriceCatalog providedCatalog) {
        this.providedCatalog = Objects.requireNonNull(providedCatalog, "PriceCatalog must not be null");
        this.calcService = new RoomCalculationService();
    }

    private PriceCatalog getEffectiveCatalog(Locale locale) {
        // Если передан стандартный каталог – подменяем в зависимости от локали
        if (providedCatalog.equals(PriceCatalog.defaultRub()) || providedCatalog.equals(PriceCatalog.defaultUsd())) {
            if (locale.getLanguage().equals("ru")) {
                return PriceCatalog.defaultRub();
            } else {
                return PriceCatalog.defaultUsd();
            }
        }
        // Кастомный каталог – используем как есть
        return providedCatalog;
    }

    public Currency detectCurrency(Locale locale) {
        return getEffectiveCatalog(locale).getCurrency();
    }

    public Money calculateTotalBudget(Room room, FloorType floorType, Locale locale) {
        Objects.requireNonNull(room, "Room must not be null");
        Objects.requireNonNull(floorType, "FloorType must not be null");
        Objects.requireNonNull(locale, "Locale must not be null");

        PriceCatalog catalog = getEffectiveCatalog(locale);

        double wallArea = calcService.getNetWallArea(room) / 1_000_000.0;
        double floorArea = calcService.getFloorArea(room) / 1_000_000.0;
        double perimeter = 2.0 * (room.getLength() + room.getWidth()) / 1000.0;

        BigDecimal wallPlasteringCost = catalog.getPlasteringPrice().multiply(BigDecimal.valueOf(wallArea));
        BigDecimal wallPrimingCost = catalog.getPrimingPrice().multiply(BigDecimal.valueOf(wallArea));
        BigDecimal wallpaperingCost = catalog.getWallpaperingPrice().multiply(BigDecimal.valueOf(wallArea));
        BigDecimal floorPouringCost = catalog.getFloorPouringPrice().multiply(BigDecimal.valueOf(floorArea));

        BigDecimal floorCoveringPrice = (floorType == FloorType.LINOLEUM)
                ? catalog.getLinoleumPrice()
                : catalog.getLaminatePrice();
        BigDecimal floorCoveringCost = floorCoveringPrice.multiply(BigDecimal.valueOf(floorArea));

        BigDecimal skirtingBoardCost = catalog.getSkirtingPrice().multiply(BigDecimal.valueOf(perimeter));
        BigDecimal ceilingCost = catalog.getCeilingPrice().multiply(BigDecimal.valueOf(floorArea));

        BigDecimal total = wallPlasteringCost
                .add(wallPrimingCost)
                .add(wallpaperingCost)
                .add(floorPouringCost)
                .add(floorCoveringCost)
                .add(skirtingBoardCost)
                .add(ceilingCost);

        total = total.setScale(2, RoundingMode.HALF_UP);

        return new Money(total, catalog.getCurrency());
    }

    public BudgetEstimate getDetailedEstimate(Room room, FloorType floorType, Locale locale) {
        Objects.requireNonNull(room, "Room must not be null");
        Objects.requireNonNull(floorType, "FloorType must not be null");
        Objects.requireNonNull(locale, "Locale must not be null");

        PriceCatalog catalog = getEffectiveCatalog(locale);

        double wallArea = calcService.getNetWallArea(room) / 1_000_000.0;
        double floorArea = calcService.getFloorArea(room) / 1_000_000.0;
        double perimeter = 2.0 * (room.getLength() + room.getWidth()) / 1000.0;

        BigDecimal wallPlasteringCost = catalog.getPlasteringPrice().multiply(BigDecimal.valueOf(wallArea));
        BigDecimal wallPrimingCost = catalog.getPrimingPrice().multiply(BigDecimal.valueOf(wallArea));
        BigDecimal wallpaperingCost = catalog.getWallpaperingPrice().multiply(BigDecimal.valueOf(wallArea));
        BigDecimal floorPouringCost = catalog.getFloorPouringPrice().multiply(BigDecimal.valueOf(floorArea));

        BigDecimal floorCoveringPrice = (floorType == FloorType.LINOLEUM)
                ? catalog.getLinoleumPrice()
                : catalog.getLaminatePrice();
        BigDecimal floorCoveringCost = floorCoveringPrice.multiply(BigDecimal.valueOf(floorArea));

        BigDecimal skirtingBoardCost = catalog.getSkirtingPrice().multiply(BigDecimal.valueOf(perimeter));
        BigDecimal ceilingCost = catalog.getCeilingPrice().multiply(BigDecimal.valueOf(floorArea));

        BigDecimal total = wallPlasteringCost
                .add(wallPrimingCost)
                .add(wallpaperingCost)
                .add(floorPouringCost)
                .add(floorCoveringCost)
                .add(skirtingBoardCost)
                .add(ceilingCost);

        Currency currency = catalog.getCurrency();

        return new BudgetEstimate(
                new Money(wallPlasteringCost.setScale(2, RoundingMode.HALF_UP), currency),
                new Money(wallPrimingCost.setScale(2, RoundingMode.HALF_UP), currency),
                new Money(wallpaperingCost.setScale(2, RoundingMode.HALF_UP), currency),
                new Money(floorPouringCost.setScale(2, RoundingMode.HALF_UP), currency),
                new Money(floorCoveringCost.setScale(2, RoundingMode.HALF_UP), currency),
                new Money(skirtingBoardCost.setScale(2, RoundingMode.HALF_UP), currency),
                new Money(ceilingCost.setScale(2, RoundingMode.HALF_UP), currency),
                new Money(total.setScale(2, RoundingMode.HALF_UP), currency)
        );
    }
}