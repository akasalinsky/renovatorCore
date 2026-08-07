package org.example.service;

import org.example.model.*;
import org.example.service.money.Currency;
import org.example.service.money.Money;
import org.example.service.money.PriceCatalog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

@DisplayName("Тесты для RoomBudgetService")
class RoomBudgetServiceTest {

    private RoomBudgetService budgetService;
    private Room testRoom;
    private PriceCatalog defaultPrices;

    @BeforeEach
    void setUp() {
        // Дефолтный прайс-лист в рублях
        defaultPrices = PriceCatalog.defaultRub();
        budgetService = new RoomBudgetService(defaultPrices);

        // Комната 4м x 3м x 2.5м
        List<Wall> walls = List.of(
                new Wall(4000, 2500, 0, List.of()),
                new Wall(3000, 2500, 1, List.of()),
                new Wall(4000, 2500, 2, List.of()),
                new Wall(3000, 2500, 3, List.of())
        );
        testRoom = new Room("Test Room", 2500, walls, List.of(90, 90, 90, 90));
    }

    @Nested
    @DisplayName("Определение валюты по Locale")
    class CurrencyDetection {

       /* @Test
        @DisplayName("Для русского языка должна быть валюта RUB")
        void shouldReturnRubForRussianLocale() {
            Currency currency = budgetService.detectCurrency(Locale.forLanguageTag("ru"));
            assertThat(currency).isEqualTo(Currency.RUB);
        }

        @Test
        @DisplayName("Для английского языка должна быть валюта USD")
        void shouldReturnUsdForEnglishLocale() {
            Currency currency = budgetService.detectCurrency(Locale.ENGLISH);
            assertThat(currency).isEqualTo(Currency.USD);
        }*/
/*
        @Test
        @DisplayName("Для неизвестного языка должна быть валюта по умолчанию (USD)")
        void shouldReturnDefaultCurrencyForUnknownLocale() {
            Currency currency = budgetService.detectCurrency(Locale.FRENCH);
            assertThat(currency).isEqualTo(Currency.USD);
        }*/
    }

    @Nested
    @DisplayName("Пользовательские цены")
    class CustomPrices {

        @Test
        @DisplayName("Должен использовать пользовательские цены вместо дефолтных")
        void shouldUseCustomPrices() {
            // Пользовательские цены в 2 раза выше дефолтных
            PriceCatalog customPrices = new PriceCatalog(
                    Currency.RUB,
                    new BigDecimal("1000"),  // штукатурка (было 500)
                    new BigDecimal("300"),   // грунтовка (было 150)
                    new BigDecimal("600"),   // обои (было 300)
                    new BigDecimal("800"),   // заливка (было 400)
                    new BigDecimal("700"),   // линолеум (было 350)
                    new BigDecimal("1200"),  // ламинат (было 600)
                    new BigDecimal("400"),   // плинтус (было 200)
                    new BigDecimal("1400")   // потолок (было 700)
            );
            RoomBudgetService customService = new RoomBudgetService(customPrices);

            Money customBudget = customService.calculateTotalBudget(testRoom, FloorType.LINOLEUM, Locale.forLanguageTag("ru"));
            Money defaultBudget = budgetService.calculateTotalBudget(testRoom, FloorType.LINOLEUM, Locale.forLanguageTag("ru"));

            // Пользовательский бюджет должен быть ровно в 2 раза больше
            assertThat(customBudget.amount())
                    .isEqualByComparingTo(defaultBudget.amount().multiply(new BigDecimal("2")));
        }

        @Test
        @DisplayName("Должен отклонить прайс-лист с отрицательными ценами")
        void shouldRejectNegativePrices() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> new PriceCatalog(
                            Currency.RUB,
                            new BigDecimal("-100"), // ← отрицательная цена
                            BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                            BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
                    ))
                    .withMessageContaining("negative");
        }
    }

    @Nested
    @DisplayName("Расчёт с учётом валюты")
    class CurrencyCalculation {

        @Test
        @DisplayName("Для русского языка результат должен быть в рублях")
        void shouldReturnRubForRussian() {
            Money budget = budgetService.calculateTotalBudget(testRoom, FloorType.LINOLEUM, Locale.forLanguageTag("ru"));
            assertThat(budget.currency()).isEqualTo(Currency.RUB);
        }

        /*@Test
        @DisplayName("Для английского языка результат должен быть в долларах")
        void shouldReturnUsdForEnglish() {
            Money budget = budgetService.calculateTotalBudget(testRoom, FloorType.LINOLEUM, Locale.ENGLISH);
            assertThat(budget.currency()).isEqualTo(Currency.USD);
        }*/

        /*@Test
        @DisplayName("Money должен корректно форматироваться")
        void shouldFormatMoneyCorrectly() {
            Money rub = new Money(new BigDecimal("1234.56"), Currency.RUB);
            Money usd = new Money(new BigDecimal("1234.56"), Currency.USD);

            assertThat(rub.format()).isEqualTo("1 234,56 ₽");
            assertThat(usd.format()).isEqualTo("$1,234.56");
        }*/
    }

    @Nested
    @DisplayName("Защита от некорректных данных")
    class SafetyChecks {

        @Test
        @DisplayName("Должен отклонить null комнату")
        void shouldRejectNullRoom() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> budgetService.calculateTotalBudget(null, FloorType.LINOLEUM, Locale.ENGLISH))
                    .withMessageContaining("Room");
        }

        @Test
        @DisplayName("Должен отклонить null тип пола")
        void shouldRejectNullFloorType() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> budgetService.calculateTotalBudget(testRoom, null, Locale.ENGLISH))
                    .withMessageContaining("FloorType");
        }

        @Test
        @DisplayName("Должен отклонить null локаль")
        void shouldRejectNullLocale() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> budgetService.calculateTotalBudget(testRoom, FloorType.LINOLEUM, null))
                    .withMessageContaining("Locale");
        }

        @Test
        @DisplayName("Результат не может быть отрицательным")
        void shouldNeverReturnNegativeResult() {
            Money budget = budgetService.calculateTotalBudget(testRoom, FloorType.LINOLEUM, Locale.ENGLISH);
            assertThat(budget.amount().signum()).isGreaterThanOrEqualTo(0);
        }

        @Test
        @DisplayName("Должен защитить от переполнения при очень больших ценах")
        void shouldProtectFromOverflow() {
            PriceCatalog hugePrices = new PriceCatalog(
                    Currency.RUB,
                    new BigDecimal("99999999999999999999999999"),
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
            );
            RoomBudgetService hugeService = new RoomBudgetService(hugePrices);

            // BigDecimal не должен переполниться, но если цена нереалистично большая — защита
            Money budget = hugeService.calculateTotalBudget(testRoom, FloorType.LINOLEUM, Locale.forLanguageTag("ru"));
            assertThat(budget.amount()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Детальная смета")
    class DetailedEstimate {

       /* @Test
        @DisplayName("Должен вернуть детальную смету с правильными валютами")
        void shouldReturnDetailedEstimateWithCurrency() {
            BudgetEstimate estimate = budgetService.getDetailedEstimate(testRoom, FloorType.LAMINATE, Locale.forLanguageTag("ru"));

            assertThat(estimate.getWallPlasteringCost().currency()).isEqualTo(Currency.RUB);
            assertThat(estimate.getWallPrimingCost().currency()).isEqualTo(Currency.RUB);
            assertThat(estimate.getWallpaperingCost().currency()).isEqualTo(Currency.RUB);
            assertThat(estimate.getFloorPouringCost().currency()).isEqualTo(Currency.RUB);
            assertThat(estimate.getFloorCoveringCost().currency()).isEqualTo(Currency.RUB);
            assertThat(estimate.getSkirtingBoardCost().currency()).isEqualTo(Currency.RUB);
            assertThat(estimate.getCeilingCost().currency()).isEqualTo(Currency.RUB);
            assertThat(estimate.getTotalCost().currency()).isEqualTo(Currency.RUB);

            // Сумма деталей должна равняться итогу
            Money sum = estimate.getWallPlasteringCost()
                    .add(estimate.getWallPrimingCost())
                    .add(estimate.getWallpaperingCost())
                    .add(estimate.getFloorPouringCost())
                    .add(estimate.getFloorCoveringCost())
                    .add(estimate.getSkirtingBoardCost())
                    .add(estimate.getCeilingCost());
            assertThat(estimate.getTotalCost().amount())
                    .isEqualByComparingTo(sum.amount());
        }*/
    }
    @Nested
    @DisplayName("Реальные расчёты бюджета")
    class RealCalculations {

        @Test
        @DisplayName("Должен рассчитать бюджет для комнаты 4м x 3м x 2.5м с линолеумом")
        void shouldCalculateBudgetForSpecificRoom() {
            // Комната: 4м x 3м x 2.5м
            // Площадь пола: 12 м²
            // Периметр: 14 м
            // Площадь стен: 35 м² (без проёмов)

            Money budget = budgetService.calculateTotalBudget(testRoom, FloorType.LINOLEUM, Locale.forLanguageTag("ru"));

            // Ожидаемые расчёты:
            // Штукатурка: 35 * 500 = 17500
            // Грунтовка: 35 * 150 = 5250
            // Обои: 35 * 300 = 10500
            // Заливка: 12 * 400 = 4800
            // Линолеум: 12 * 350 = 4200
            // Плинтус: 14 * 200 = 2800
            // Потолок: 12 * 700 = 8400
            // ИТОГО: 53450

            assertThat(budget.amount()).isEqualByComparingTo(new BigDecimal("53450.00"));
            assertThat(budget.currency()).isEqualTo(Currency.RUB);
        }

        @Test
        @DisplayName("Должен рассчитать бюджет для комнаты с ламинатом")
        void shouldCalculateBudgetWithLaminate() {
            // Та же комната, но с ламинатом
            // Ламинат дороже линолеума: 12 * 600 = 7200 (вместо 4200)
            // Разница: 7200 - 4200 = 3000
            // Итого: 53450 + 3000 = 56450

            Money budget = budgetService.calculateTotalBudget(testRoom, FloorType.LAMINATE, Locale.forLanguageTag("ru"));

            assertThat(budget.amount()).isEqualByComparingTo(new BigDecimal("56450.00"));
        }

        @Test
        @DisplayName("Должен рассчитать бюджет в долларах для английского языка")
        void shouldCalculateBudgetInUsd() {
            // Те же расчёты, но в USD
            // Штукатурка: 35 * 15 = 525
            // Грунтовка: 35 * 5 = 175
            // Обои: 35 * 10 = 350
            // Заливка: 12 * 12 = 144
            // Линолеум: 12 * 10 = 120
            // Плинтус: 14 * 6 = 84
            // Потолок: 12 * 20 = 240
            // ИТОГО: 1638

            Money budget = budgetService.calculateTotalBudget(testRoom, FloorType.LINOLEUM, Locale.ENGLISH);

            assertThat(budget.amount()).isEqualByComparingTo(new BigDecimal("1638.00"));
            assertThat(budget.currency()).isEqualTo(Currency.USD);
        }

        @Test
        @DisplayName("Должен рассчитать бюджет с учётом проёмов")
        void shouldCalculateBudgetWithOpenings() {
            // Добавляем окно 1.2м x 1.4м = 1.68 м²
            Opening window = new Opening(OpeningType.WINDOW, 1200, 1400);
            WallOpening wallOpening = new WallOpening(window, 1000, 900);
            Room roomWithWindow = testRoom.withOpening(1, wallOpening);

            // Площадь стен уменьшилась: 35 - 1.68 = 33.32 м²
            // Штукатурка: 33.32 * 500 = 16660
            // Грунтовка: 33.32 * 150 = 4998
            // Обои: 33.32 * 300 = 9996
            // Остальное без изменений
            // Итого: 16660 + 4998 + 9996 + 4800 + 4200 + 2800 + 8400 = 51858

            Money budget = budgetService.calculateTotalBudget(roomWithWindow, FloorType.LINOLEUM, Locale.forLanguageTag("ru"));

            assertThat(budget.amount()).isEqualByComparingTo(new BigDecimal("51858.00"));
        }

        @Test
        @DisplayName("Должен рассчитать бюджет с несколькими проёмами")
        void shouldCalculateBudgetWithMultipleOpenings() {
            // Окно: 1.2м x 1.4м = 1.68 м²
            Opening window = new Opening(OpeningType.WINDOW, 1200, 1400);
            WallOpening windowOpening = new WallOpening(window, 1000, 900);

            // Дверь: 0.9м x 2.1м = 1.89 м²
            Opening door = new Opening(OpeningType.DOOR, 900, 2100);
            WallOpening doorOpening = new WallOpening(door, 500, 0);

            Room roomWithOpenings = testRoom
                    .withOpening(1, windowOpening)
                    .withOpening(2, doorOpening);

            // Площадь стен: 35 - 1.68 - 1.89 = 31.43 м²
            // Штукатурка: 31.43 * 500 = 15715
            // Грунтовка: 31.43 * 150 = 4714.5
            // Обои: 31.43 * 300 = 9429
            // Итого: 15715 + 4714.5 + 9429 + 4800 + 4200 + 2800 + 8400 = 50058.5

            Money budget = budgetService.calculateTotalBudget(roomWithOpenings, FloorType.LINOLEUM, Locale.forLanguageTag("ru"));

            assertThat(budget.amount()).isEqualByComparingTo(new BigDecimal("50058.50"));
        }

        /*@Test
        @DisplayName("Должен рассчитать детальную смету с точными значениями")
        void shouldCalculateDetailedEstimateWithExactValues() {
            BudgetEstimate estimate = budgetService.getDetailedEstimate(testRoom, FloorType.LINOLEUM, Locale.forLanguageTag("ru"));

            assertThat(estimate.getWallPlasteringCost().amount()).isEqualByComparingTo(new BigDecimal("17500.00"));
            assertThat(estimate.getWallPrimingCost().amount()).isEqualByComparingTo(new BigDecimal("5250.00"));
            assertThat(estimate.getWallpaperingCost().amount()).isEqualByComparingTo(new BigDecimal("10500.00"));
            assertThat(estimate.getFloorPouringCost().amount()).isEqualByComparingTo(new BigDecimal("4800.00"));
            assertThat(estimate.getFloorCoveringCost().amount()).isEqualByComparingTo(new BigDecimal("4200.00"));
            assertThat(estimate.getSkirtingBoardCost().amount()).isEqualByComparingTo(new BigDecimal("2800.00"));
            assertThat(estimate.getCeilingCost().amount()).isEqualByComparingTo(new BigDecimal("8400.00"));
            assertThat(estimate.getTotalCost().amount()).isEqualByComparingTo(new BigDecimal("53450.00"));
        }*/

        @Test
        @DisplayName("Должен рассчитать бюджет с пользовательскими ценами")
        void shouldCalculateBudgetWithCustomPrices() {
            // Пользовательские цены в 2 раза выше
            PriceCatalog customPrices = new PriceCatalog(
                    Currency.RUB,
                    new BigDecimal("1000"),  // штукатурка (было 500)
                    new BigDecimal("300"),   // грунтовка (было 150)
                    new BigDecimal("600"),   // обои (было 300)
                    new BigDecimal("800"),   // заливка (было 400)
                    new BigDecimal("700"),   // линолеум (было 350)
                    new BigDecimal("1200"),  // ламинат (было 600)
                    new BigDecimal("400"),   // плинтус (было 200)
                    new BigDecimal("1400")   // потолок (было 700)
            );
            RoomBudgetService customService = new RoomBudgetService(customPrices);

            // Все цены в 2 раза выше, значит итог тоже в 2 раза: 53450 * 2 = 106900
            Money budget = customService.calculateTotalBudget(testRoom, FloorType.LINOLEUM, Locale.forLanguageTag("ru"));

            assertThat(budget.amount()).isEqualByComparingTo(new BigDecimal("106900.00"));
        }
    }
}