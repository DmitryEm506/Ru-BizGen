package ru.eda.plgn.bizgen.generators

/**
 * Базовый класс для тестирования генераторов, которые возвращают строковый объект.
 *
 * @param generator генератор
 * @param uniqDistance количество запусков генератора (дистанция). По умолчанию = 30.
 * @author Dmitry_Emelyanenko
 */
internal abstract class StrGeneratorTest(generator: Generator<String>, uniqDistance: Int = 30) :
  GeneratorBaseTest<String>(generator, uniqDistance)