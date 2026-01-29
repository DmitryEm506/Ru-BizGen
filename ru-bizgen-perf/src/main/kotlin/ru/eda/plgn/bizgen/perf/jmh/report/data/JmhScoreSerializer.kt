package ru.eda.plgn.bizgen.perf.jmh.report.data

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive

/**
 * Отдельный сериализатор для поля score JMH отчета.
 *
 * Необходим из-за того, что данное поле может содержать [Double] значение или строковое значение "NaN"
 *
 * @author Dmitry_Emelyanenko
 */
object JmhScoreSerializer : KSerializer<Double?> {

  @OptIn(InternalSerializationApi::class)
  override val descriptor: SerialDescriptor =
    buildSerialDescriptor("JmhScore", SerialKind.CONTEXTUAL)

  override fun deserialize(decoder: Decoder): Double? {
    val jsonDecoder = decoder as? JsonDecoder
      ?: error("JmhScoreSerializer can be used only with Json")

    return when (val element = jsonDecoder.decodeJsonElement()) {
      is JsonPrimitive -> {
        when {
          element.isString && element.content == "NaN" -> null
          else -> element.content.toDoubleOrNull()
        }
      }

      else -> null
    }
  }

  override fun serialize(encoder: Encoder, value: Double?) {
    val jsonEncoder = encoder as? JsonEncoder
      ?: error("JmhScoreSerializer can be used only with Json")

    val jsonValue = value?.let { JsonPrimitive(it) } ?: JsonPrimitive("NaN")
    jsonEncoder.encodeJsonElement(jsonValue)
  }
}