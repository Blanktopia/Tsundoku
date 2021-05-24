package me.weiwen.tsundoku.serializers

import de.themoep.minedown.adventure.MineDown
import de.themoep.minedown.adventure.MineDownParser
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.kyori.adventure.text.Component

@Serializable(with = FormattedStringSerializer::class)
data class FormattedString(val component: Component)

object FormattedStringSerializer : KSerializer<FormattedString> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("FormattedString", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): FormattedString {
        val string = decoder.decodeString()

        val component = MineDown.parse(string)

        return FormattedString(component)
    }

    override fun serialize(encoder: Encoder, value: FormattedString) {
        val string = MineDown.stringify(value.component)

        return encoder.encodeString(string)
    }
}
