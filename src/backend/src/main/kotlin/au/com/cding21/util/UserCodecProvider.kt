package au.com.cding21.util

import org.bson.codecs.Codec
import org.bson.codecs.configuration.CodecProvider
import org.bson.codecs.configuration.CodecRegistry

class UserCodecProvider: CodecProvider {
    override fun <T : Any?> get(p0: Class<T>, p1: CodecRegistry?): Codec<T>? {
        if(p0 == UserCodec::class.java) {
            return UserCodec() as Codec<T>
        }
        return null
    }
}