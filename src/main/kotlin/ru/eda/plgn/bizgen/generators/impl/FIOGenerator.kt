package ru.eda.plgn.bizgen.generators.impl

import ru.eda.plgn.bizgen.generators.Generator
import ru.eda.plgn.bizgen.generators.GeneratorResult
import ru.eda.plgn.bizgen.generators.GeneratorResultWithEscape
import ru.eda.plgn.bizgen.generators.impl.FIOGeneratorInner.generateFioFull
import ru.eda.plgn.bizgen.generators.impl.FIOGeneratorInner.generateFioInitials
import ru.eda.plgn.bizgen.generators.impl.FIOGeneratorInner.generateFioShort

/**
 * Генератор ФИО в полном формате (Фамилия Имя Отчество).
 *
 * @author Dmitry_Emelyanenko
 */
class FIOFullGenerator : Generator<String> {
  override val uniqueDistance: Int = 120

  override fun generate(): GeneratorResult<String> = GeneratorResultWithEscape(data = generateFioFull())
}

/**
 * Генератор ФИО в сокращенном формате (Фамилия И. О.).
 *
 * @author Dmitry_Emelyanenko
 */
class FIOShortGenerator : Generator<String> {
  override val uniqueDistance: Int = 30

  override fun generate(): GeneratorResult<String> = GeneratorResultWithEscape(data = generateFioShort())
}

/**
 * Генератор ФИО в формате инициалов (И.О. Фамилия).
 *
 * @author Dmitry_Emelyanenko
 */
class FIOInitialsGenerator : Generator<String> {
  override val uniqueDistance: Int = 30

  override fun generate(): GeneratorResult<String> = GeneratorResultWithEscape(data = generateFioInitials())
}


private object FIOGeneratorInner {

  private val maleFirstNames = listOf(
    "Александр", "Дмитрий", "Максим", "Сергей", "Андрей", "Алексей", "Иван", "Михаил", "Артем", "Илья",
    "Кирилл", "Никита", "Егор", "Павел", "Роман", "Владимир", "Константин", "Тимур", "Олег", "Юрий",
    "Василий", "Виктор", "Глеб", "Даниил", "Евгений", "Захар", "Лев", "Матвей", "Николай", "Петр",
    "Степан", "Федор", "Ярослав", "Борис", "Григорий", "Денис", "Игорь", "Леонид", "Станислав", "Тарас",
    "Арсений", "Валентин", "Георгий", "Елисей", "Игнат", "Клим", "Марк", "Назар", "Оскар", "Родион"
  )

  private val femaleFirstNames = listOf(
    "Елена", "Ольга", "Наталья", "Анна", "Мария", "Ирина", "Екатерина", "Светлана", "Татьяна", "Юлия",
    "Александра", "Алина", "Алиса", "Анастасия", "Валентина", "Вера", "Виктория", "Галина", "Дарья", "Диана",
    "Евгения", "Елизавета", "Жанна", "Зоя", "Инна", "Кира", "Ксения", "Лариса", "Лидия", "Любовь",
    "Людмила", "Маргарита", "Марина", "Милана", "Надежда", "Нина", "Полина", "Раиса", "Регина", "Римма",
    "Снежана", "София", "Тамара", "Ульяна", "Элина", "Эльвира", "Юлиана", "Яна", "Варвара", "Вероника"
  )

  private val maleLastNames = listOf(
    "Иванов", "Петров", "Сидоров", "Смирнов", "Кузнецов", "Попов", "Васильев", "Павлов", "Семенов", "Голубев",
    "Виноградов", "Богданов", "Воробьев", "Федоров", "Михайлов", "Беляев", "Тарасов", "Белов", "Комаров", "Орлов",
    "Киселев", "Макаров", "Андреев", "Ковалев", "Ильин", "Гусев", "Титов", "Кузьмин", "Кудрявцев", "Баранов",
    "Куликов", "Алексеев", "Степанов", "Яковлев", "Сорокин", "Сергеев", "Романов", "Захаров", "Борисов", "Королев",
    "Герасимов", "Пономарев", "Григорьев", "Лазарев", "Медведев", "Ершов", "Никитин", "Соболев", "Рябов", "Поляков"
  )

  private val femaleLastNames = maleLastNames.map { it + "а" } + listOf(
    "Цветкова", "Жукова", "Коновалова", "Крылова", "Скворцова",
    "Зайцева", "Ермакова", "Фролова", "Данилова", "Мельникова"
  )

  private val middleNames = listOf(
    "Александрович", "Алексеевич", "Анатольевич", "Андреевич", "Антонович",
    "Аркадьевич", "Артемович", "Борисович", "Вадимович", "Валентинович",
    "Валериевич", "Васильевич", "Викторович", "Витальевич", "Владимирович",
    "Владиславович", "Вячеславович", "Геннадьевич", "Георгиевич", "Григорьевич",
    "Данилович", "Денисович", "Дмитриевич", "Евгеньевич", "Егорович",
    "Иванович", "Игоревич", "Ильич", "Кириллович", "Константинович",

    "Александровна", "Алексеевна", "Анатольевна", "Андреевна", "Антоновна",
    "Аркадьевна", "Артемовна", "Борисовна", "Вадимовна", "Валентиновна",
    "Валерьевна", "Васильевна", "Викторовна", "Витальевна", "Владимировна",
    "Владиславовна", "Вячеславовна", "Геннадьевна", "Георгиевна", "Григорьевна",
    "Даниловна", "Денисовна", "Дмитриевна", "Евгеньевна", "Егоровна",
    "Ивановна", "Игоревна", "Ильинична", "Кирилловна", "Константиновна"
  )

  fun generateFioInitials(): String = generate(format = NameFormat.INITIALS)

  fun generateFioShort(): String = generate(format = NameFormat.SHORT)

  fun generateFioFull(): String = generate(format = NameFormat.FULL)

  private fun generate(format: NameFormat = NameFormat.FULL): String = generate(gender = Gender.random(), format = format)

  private fun generate(gender: Gender = Gender.random(), format: NameFormat = NameFormat.FULL): String {
    // имя
    val firstName = when (gender) {
      Gender.MALE -> maleFirstNames.random()
      Gender.FEMALE -> femaleFirstNames.random()
    }

    // фамилия
    val lastName = when (gender) {
      Gender.MALE -> maleLastNames.random()
      Gender.FEMALE -> femaleLastNames.random()
    }

    // отчество
    val middleName = middleNames.filter {
      it.endsWith("ич") && gender == Gender.MALE ||
          it.endsWith("на") && gender == Gender.FEMALE
    }.random()

    return when (format) {
      NameFormat.FULL -> "$lastName $firstName $middleName"
      NameFormat.SHORT -> "$lastName ${firstName.first()}.${middleName.first()}."
      NameFormat.INITIALS -> "${firstName.first()}.${middleName.first()}. $lastName"
    }.trim()
  }

  // Перечисления для настроек
  enum class Gender {
    MALE, FEMALE;

    companion object {
      fun random(): Gender = entries.toTypedArray().random()
    }
  }

  enum class NameFormat {
    FULL,      // Иванов Иван Иванович
    SHORT,     // Иванов И.И.
    INITIALS,  // И.И. Иванов
  }
}
