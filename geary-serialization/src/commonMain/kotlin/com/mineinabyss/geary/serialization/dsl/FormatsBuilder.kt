package com.mineinabyss.geary.serialization.dsl

import com.mineinabyss.geary.serialization.formats.Format
import com.mineinabyss.geary.serialization.formats.Formats
import com.mineinabyss.geary.serialization.formats.SimpleFormats
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.modules.SerializersModule

class FormatsBuilder {
    val formats = mutableMapOf<String, (SerializersModule) -> Format>()

    /** Registers a [Format] for a file with extension [ext]. */
    fun register(ext: String, makeFromat: (SerializersModule) -> Format) {
        formats[ext] = makeFromat
    }

    fun build(): Formats {
        val serializers = serializableComponents.serializers

        return SimpleFormats(
            binaryFormat = Cbor {
                serializersModule = serializers.module
                encodeDefaults = false
            },
            formats = formats.mapValues { it.value(serializers.module) },
        )
    }
}
