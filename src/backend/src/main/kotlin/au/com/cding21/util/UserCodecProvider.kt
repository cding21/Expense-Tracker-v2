package au.com.cding21.util

import org.bson.codecs.Codec
import org.bson.codecs.configuration.CodecProvider
import org.bson.codecs.configuration.CodecRegistry

class UserCodecProvider: CodecProvider {
    @Suppress("UNCHECKED_CAST")
    override fun <T : Any?> get(p0: Class<T>, p1: CodecRegistry?): Codec<T>? {
        if(p0 == UserCodec::class.java) {
            try {
                return UserCodec() as Codec<T>
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }
}