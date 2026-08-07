qq# Тестовое описание квартиры для юнит-тестов
## Описание тестов

В проекте реализованы юнит-тесты с использованием JUnit 5 для проверки корректности расчетов.

### Тесты для `Room.java` (`src/test/java/org/example/model/RoomTest.java`)

* Тесты на создание объекта:  
* `shouldCreateRoomSuccessfully()`: Проверяет, что комната создается корректно по имени, высоте, количеству стен, объект иммутабелен, имеет четыре угла.

* Тесты на валидацию:
*   `shouldThrowIllegalArgumentExceptionForNonPositiveHeight()`: проверка на отрицательную или нулевую высоту комнаты
*   `shouldThrowIllegalArgumentExceptionForNullWalls()`: проверка на null в списке стен
*   `shouldThrowIllegalArgumentExceptionForEmptyWalls()`: проверка пустой список стен
*   `shouldThrowIllegalArgumentExceptionForNullAngles()`: проверка на null в списке углов
*   `shouldThrowIllegalArgumentExceptionIfAnyAngleIsInvalid()`: проверка что углы лежат в пределах от 0 до 260 не включая
*   `shouldThrowIllegalArgumentExceptionIfWallsListContainsNullElement()`: проверка на содержание null в списке стен

* Тесты на корректность расчетов:
*   `totalWallAreaShouldSumAreasOfAllWalls()`: проверка, что общая площадь совпадает с площадью стен
*   `totalOpeningsAreaShouldSumAreasOfAllOpeningsInAllWalls()`: проверка, что общая площадь проемов совпадает с площадью из метода
*   `netWallAreaShouldSubtractTotalOpeningsAreaFromTotalWallArea()`: проверка, что чистая площадь стен совпадает с площадью без проемов
*   `netWallAreaShouldSubtractTotalOpeningsAreaFromTotalWallArea()`: проверка, что чистая площадь стен совпадает с площадью без проемов

* Тесты на корректность описания:
*   `describeShouldContainHeightWallsCountAndTotalOpeningsArea()`: проверка, что описание содержит высоту комнаты, количество углов, и площадь проемов
*   `shouldAddWallOpeningInRoom()`: проверка, корректности добавления нового проема в комнату
*   `shouldAddWallOpeningInRoom()`: проверка, корректности добавления нового проема в комнату

### Тесты для `BotService.java` (`src/test/java/org/example/actions/bot/BotServiceTest.java`)

* Тесты на получение приветственного сообщения:
*   `shouldReturnWelcomeMessageForStartCommand()`: проверка, что возвращается приветственное сообщение по команде start
*   `shouldReturnWelcomeMessageForHelloCommand()`: проверка, что возвращается приветственное сообщение по команде hello
*   `shouldBeCaseInsensitiveForStartHello()`: проверка, что возвращается приветственное сообщение по команде hello или start вне зависимости от заглавия букв

* Тесты на создание комнаты и получение описания созданной комнаты:
*   `shouldCreateAndThenDescribeRoom()`: проверка, что создается объект комнаты и описание содержит имя комнаты

* Тесты на получение плана комнаты:
*   `shouldRenderPlanAfterCreatingRoom()`: проверка, что после создания план комнаты содержит символы стен и углов

* Тесты на получение количества обоев:
*   `shouldCalculateWallpaperRollsAfterCreatingRoom()`: проверка, корректности расчета рулоново обоев

* Тесты на получение площади комнаты:
*   `shouldReturnFloorAreaAfterCreatingRoom()`: проверка, корректности расчета площади пола

* Тесты на изоляцию разных комнат:
*   `shouldIsolateSessionsBetweenDifferentUsers()`: проверка, что у разных комнат разные названия

* Тесты на выход и сброс сессии:
*   `shouldResetSessionAfterExitCommand()`: проверка, что после команды exit сессия обновляется, т.е. комната не создана
*   `shouldAllowNewSessionAfterExit()`: проверка, что после команды exit и создания второй комнаты, сессия обновляется, первой комнаты нет, а вторая есть

* Тесты на неизвестную команду:
*   `shouldReturnUnknownCommandMessage()`: проверка, что при получении неизвестной команды пользователю возвращается сообщение

### Тесты для `Opening.java` (`src/test/java/org/example/actions/model/OpeningTest.java`)

*   `shouldCreateOpeningWithCorrectParameters()`: проверка, что после создания объект создан с корректными параметрами
*   `shouldThrowIllegalArgumentExceptionWhenWidthIsZeroOrNegative()`: проверка, что выбрасывается исключение с сообщением, если ширина проема отрицательная или ноль
*   `shouldThrowIllegalArgumentExceptionWhenHeightIsZeroOrNegative()`: проверка, что выбрасывается исключение с сообщением, если высота проема отрицательная или ноль
*   `areaShouldReturnWidthMultipliedByHeight()`: проверка, что площадь проема расчитывается верно
*   `equalOpeningsShouldBeEqual()`: проверка, что два созданных проема с одинаковыми параметрами идентичны
*   `differentOpeningsShouldNotBeEqual()`: проверка, что два созданных проема с разными параметрами не идентичны
*   `hashCodeShouldBeConsistentWithEquals()`: проверка, что два созданных проема с одинаковыми параметрами имеют один хэшкод
*   `toStringShouldContainTypeWidthAndHeight()`: проверка, что метод toString возвращает ширину и высоту проема

### Тесты для `WallOpening.java` (`src/test/java/org/example/actions/model/WallOpeningTest.java`)

*   `shouldCreateWallOpeningSuccessfully()`: проверка, что после создания объект создан с корректными параметрами
*   `shouldThrowIllegalArgumentExceptionForNegativeDistanceFromLeft()`: проверка, что выбрасывается исключение с сообщением, если растояние до левой стены отрицательное или ноль
*   `shouldThrowIllegalArgumentExceptionForNegativeDistanceFromFloor()`: проверка, что выбрасывается исключение с сообщением, если растояние до пола отрицательное или ноль
*   `shouldThrowNullPointerExceptionForNullOpening()`: проверка, что выбрасывается исключение если в параметр проема передается null
*   `widthShouldReturnOpeningWidth()`: проверка, что после создания корректно возвращается ширина проема
*   `heightShouldReturnOpeningHeight()`: проверка, что после создания корректно возвращается высота проема
*   `areaShouldReturnOpeningArea()`: проверка, что корректно возвращается площадь проема

### Тесты для `Wall.java` (`src/test/java/org/example/actions/model/WallTest.java`)

*   `shouldCreateWallWithEmptyOpeningsList()`: проверка, что после создания объект создан без проемов
*   `shouldCreateWallWithProvidedOpeningsList()`: проверка, что после создания с проемом , лист с корретными размерами проема
*   `shouldThrowIllegalArgumentExceptionForNonPositiveLength()`: проверка, что выбрасывается исключение если принимает не положительную или нулевую длинну комнаты
*   `shouldReplaceNullOpeningsListWithEmptyList()`: проверка, что при получении null вместо листа с проемами - создается пустой лист
*   `shouldReturnImmutableOpeningsList()`: проверка, на иммутабельность списка проемов
*   `shouldThrowIllegalArgumentExceptionIfAnyOpeningExceedsWallLength()`: проверка, что выбрасывается исключение если размер проема больше стены
*   `shouldAddOpeningAndReturnNewWallKeepingOriginalUnchanged()`: проверка, что при добавлении нового проема создается новая стена с теми же размерами и актуальным списком проемов
*   `shouldCreateWallOpeningSuccessfully()`: проверка, что при добавление проема выбрасывается исключение если размер проема больше стены
*   `shouldThrowIllegalArgumentExceptionIfNewOpeningExceedsWallLength()`: проверка, что выбрасывается исключение с сообщением, если растояние до левой стены отрицательное или ноль
*   `shouldThrowNpeIfNewOpeningIsNull()`: проверка, что выбрасывается исключение, если при создании нового проема передается null
*   `shouldChainMultipleWithOpeningCalls()`: проверка, корректности работы цепочки вызовов при добавлении проемов
*   `totalOpeningsAreaShouldSumAreasOfAllOpenings()`: проверка, что корректно считается общая плозщадб проемов в списке
*   `netAreaShouldSubtractTotalOpeningsAreaFromGrossArea()`: проверка, что чистая площадь стен соответствует разнице общей площади площади проемов
*   `equalWallsShouldHaveSameHashCode()`: проверка, что две созданных стены с одинаковыми параметрами имеют один хэшкод
*   `unequalWallsShouldNotBeEqual()`: проверка, что две созданных стены с разными параметрами имеют разные хэшкоды

### Тесты для метода renderPlan() в `Room.java` (`src/test/java/org/example/actions/model/RoomRenderPlanTest.java`)
*   `shouldRenderSimpleRectangle()`: проверка, что после создания план соответствует размерам комнаты и корректно отображается
*   `shouldRenderWindowOnNorthWall()`: проверка, что корректно отображается план с проемом на 1 стене
*   `shouldRenderDoorOnEastWall()`: проверка, что корректно отображается план с проемом на 2 стене
*   `shouldRenderOpeningsOnAllFourWalls()`: проверка, что корректно отображается план с проемами на нескольких стенах
*   `shouldScaleOneMeterToOneCharacter()`: проверка, что корректно тображается масштаб

### Тесты для проверки перевода (`src/test/java/org/example/InternationalizationTest.java`)
*   `startCommand_shouldReturnWelcomeInDefaultLanguage()`: проверка, что при команде start корректно возвращается приветствие на языке по умолчанию
*   `helloCommand_shouldReturnWelcomeInDefaultLanguage()`: проверка, что при команде hello корректно возвращается приветствие на языке по умолчанию
*   `exitCommand_shouldReturnGoodbyeInDefaultLanguage()`: проверка, что при команде exit корректно возвращается прощание на языке по умолчанию
*   `langCommand_shouldSwitchToRussianAndRespondInRussian()`: проверка, что корректно меняется язык на русский
*   `langCommand_shouldSwitchToEnglishAndRespondInEnglish()`: проверка, что корректно меняется язык на английский
*   `langCommand_withUnsupportedLanguage_shouldReturnErrorInCurrentLanguage()`: проверка, что возвращается сообщение не поддерживается язык, при попытке переключиться на любой кроме русского и английского
*   `afterLanguageChange_allCommandsShouldUseNewLanguage()`: проверка, что после переключения языка все команды работают на другом
*   `multipleUsers_shouldHaveIndependentLanguages()`: проверка, что разым пользователям отображаются разные языки
*   `exitCommand_shouldClearSessionAndGoodbyeInCurrentLanguage()`: проверка, что при выполнении команды выход сессия очищается а команда выводится на применимом языке
*   `nullMessage_shouldReturnEmptyMessageInDefaultLanguage()`: проверка, что сообщение null возвращает пустое сообщение
*   `createCommand_shouldReturnLocalizedSuccess()`: проверка, что при успешном создании комнаты возвращается локализованное сообщение об успехе на английском и русском языках
*   `createCommand_withMissingArgs_shouldReturnLocalizedUsage()`: проверка, что при недостаточном количестве аргументов команды create возвращается локализованная инструкция по использованию
*   `describeCommand_withoutRoom_shouldReturnLocalizedError()`: проверка, что при вызове describe до создания комнаты возвращается локализованное сообщение об ошибке
*   `describeCommand_withRoom_shouldReturnLocalizedDescription()`: проверка, что команда describe возвращает корректное описание созданной комнаты на соответствующем языке
*   `areaCommand_shouldReturnLocalizedFloorArea()`: проверка, что команда area корректно вычисляет и отображает площадь пола в локализованном формате
*   `wallpaperCommand_shouldReturnLocalizedRollCount()`: проверка, что команда wallpaper возвращает расчет количества обоев в локализованном сообщении
*   `wallpaperCommand_withMissingArgs_shouldReturnLocalizedUsage()`: проверка, что при неполных аргументах команды wallpaper возвращается локализованная инструкция по использованию
*   `planCommand_withInvalidScale_shouldReturnLocalizedUsage()`: проверка, что при вводе некорректного масштаба для команды plan возвращается локализованная инструкция
*   `planCommand_withoutRoom_shouldReturnRoomNotCreated()`: проверка, что при отсутствии созданной комнаты команда plan возвращает локализованное сообщение о том, что комната не создана
*   `exitCommand_shouldReturnLocalizedGoodbye()`: проверка, что команда exit возвращает корректное локализованное прощание
*   `unknownCommand_shouldReturnLocalizedUnknown()`: проверка, что при вводе неизвестной команды возвращается локализованное сообщение об ошибке
*   `emptyCommand_shouldReturnLocalizedEmptyMessage()`: проверка, что ввод пустой строки или пробелов возвращает локализованное сообщение о пустой команде
*   `shouldReturnCorrectEnglishMessages()`: проверка, что корректно возвращаются фразы на английском
*   `shouldSubstituteParametersInEnglishMessage()`: проверка, что корректно возвращается название комнаты на английском
*   `shouldSubstituteParametersInRussianMessage()`: проверка, что корректно возвращается название комнаты на русском
*   `shouldThrowExceptionForMissingKey()`: проверка, что выбрасывается исключение при потере ключа
