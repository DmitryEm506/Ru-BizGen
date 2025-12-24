package ru.eda.plgn.bizgen.generators

/**
 * Базовый класс для тестирования генераторов, которые возвращают строковый объект.
 *
 * @param generator генератор
 * @author Dmitry_Emelyanenko
 */
internal abstract class StrGeneratorTest(generator: Generator<String>) : GeneratorBaseTest<String>(generator)