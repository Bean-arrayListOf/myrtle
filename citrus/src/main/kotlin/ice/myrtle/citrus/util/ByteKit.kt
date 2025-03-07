package ice.myrtle.core.lang

import org.apache.commons.codec.digest.DigestUtils
import org.bouncycastle.crypto.ExtendedDigest
import org.bouncycastle.crypto.digests.*
import java.nio.charset.Charset
import java.text.Normalizer
import java.util.*

/**
 * ByteKit 对象，继承自 Raw 类型。
 * 该对象提供了一组工具方法，主要用于处理与字节相关的操作。
 * 它的设计目标是简化字节操作，提供便捷的方法来处理字节数据。
 */
object ByteKit {

	/**
	 * Byte缓存类，用于处理字节数组的缓存
	 *
	 * @property hash 字节数组，用于存储缓存的数据
	 */
	data class ByteCache(
	    var hash: ByteArray,
	) {
	    /**
	     * 将字节数组转换为Base64编码的字符串
	     *
	     * @return Base64编码的字符串
	     */
	    fun toBase64(): String {
	        return java.util.Base64.getEncoder().encodeToString(hash)
	    }

	    /**
	     * 将字节数组转换为指定分隔符的十六进制字符串
	     *
	     * @param spl 分隔符字符
	     * @return 格式化的十六进制字符串
	     */
	    fun toFormatHex(spl: Char): String {
	        val hexString = StringBuilder()
	        for (i in hash.indices) {
	            var hex = Integer.toHexString(0xFF and hash[i].toInt())
	            if (hex.length == 1) {
	                hex = "0$hex"
	            }
	            hexString.append(hex)
	            if (i < hash.size - 1) {
	                hexString.append(spl)
	            }
	        }
	        return hexString.toString()
	    }

	    /**
	     * 将字节数组转换为以":"为分隔符的十六进制字符串集合
	     *
	     * @return 十六进制字符串的集合
	     */
	    fun toHexs(): Collection<String> {
	        return toFormatHex(":").split(":")
	    }

	    /**
	     * 将字节数组转换为指定分隔符的十六进制字符串
	     *
	     * @param spl 分隔符字符串
	     * @return 格式化的十六进制字符串
	     */
	    fun toFormatHex(spl: String): String {
	        return toFormatHex(spl[0])
	    }

	    /**
	     * 将字节数组转换为连续的十六进制字符串
	     *
	     * @return 连续的十六进制字符串
	     */
	    fun toHex(): String {
	        val hexString = java.lang.StringBuilder()
	        for (b in hash) {
	            val hex = Integer.toHexString(0xFF and b.toInt())
	            if (hex.length == 1) {
	                hexString.append('0')
	            }
	            hexString.append(hex)
	        }
	        return hexString.toString()
	    }

		/**
		 * 重写 toString 方法，以十六进制字符串形式返回对象的字符串表示。
		 *
		 * @return 十六进制字符串表示的当前对象。
		 */
		override fun toString(): String {
		    return toHex()
		}
	}

	/**
	 * 哈希算法类型枚举类
	 * 列举了不同类型的哈希算法，用于标识和区分各种哈希算法
	 */
	enum class HashingType {
	    // MD2哈希算法
	    MD2,
	    // MD5哈希算法
	    MD5,
	    // 安全哈希算法（Secure Hash Algorithm）SHA的旧称
	    SHA,
	    // 安全哈希算法1（Secure Hash Algorithm 1）
	    SHA1,
	    // 安全哈希算法256（Secure Hash Algorithm 256）
	    SHA256,
	    // 安全哈希算法3之224位变体（Secure Hash Algorithm 3 - 224 bits）
	    SHA3_224,
	    // 安全哈希算法3之256位变体（Secure Hash Algorithm 3 - 256 bits）
	    SHA3_256,
	    // 安全哈希算法3之384位变体（Secure Hash Algorithm 3 - 384 bits）
	    SHA3_384,
	    // 安全哈希算法3之512位变体（Secure Hash Algorithm 3 - 512 bits）
	    SHA3_512,
	    // 安全哈希算法384（Secure Hash Algorithm 384）
	    SHA384,
	    // 安全哈希算法512（Secure Hash Algorithm 512）
	    SHA512,
	    // 安全哈希算法512之224位变体（Secure Hash Algorithm 512 - 224 bits）
	    SHA512_224,
	    // 安全哈希算法512之256位变体（Secure Hash Algorithm 512 - 256 bits）
	    SHA512_256
	}

	/**
	 * Punycode 算法对象，用于将 Unicode 字符串转换为 Punycode 编码形式
	 */
	object Punycode {
	    /**
	     * 将给定的 Unicode 字符串转换为 Punycode 编码形式
	     *
	     * @param mode 转换模式，当前实现未使用此参数
	     * @param hex 需要转换的 Unicode 字符串，以十六进制形式表示
	     * @return 返回 Punycode 编码的字符串，以 "xn--" 开头
	     */
	    operator fun get(mode: Mode, hex: String): String {
	        // 对输入的十六进制字符串进行标准化处理，使其适合 Punycode 转换
	        val normalize = Normalizer.normalize(hex, Normalizer.Form.NFKD)
	        // 构造 Punycode 编码的字符串，去除所有非字母数字字符
	        return "xn--${normalize.replace("[^a-zA-Z0-9]", "")}"
	    }
	}

	/**
	 * Hashing 对象用于提供散列相关的操作和工具。
	 */
	object Hashing {
		/**
		 * 根据指定的哈希类型和源字节数组生成对应的字节缓存对象。
		 *
		 * 此操作符函数通过接受一个哈希类型和一个字节数组作为输入，根据哈希类型的不同，
		 * 使用相应的哈希算法对源字节数组进行处理，然后将处理结果封装在字节缓存对象中返回。
		 *
		 * @param type 哈希类型，指定了要使用的哈希算法。
		 * @param source 源字节数组，是进行哈希计算的输入数据。
		 * @return 返回一个包含根据指定哈希算法计算得到结果的字节缓存对象。
		 */
		operator fun get(type: HashingType, source: ByteArray): ByteCache {
		    return ByteCache(
		        when (type) {
		            HashingType.MD2 -> DigestUtils.md2(source)
		            HashingType.MD5 -> DigestUtils.md5(source)
		            HashingType.SHA, HashingType.SHA1 -> DigestUtils.sha1(source)
		            HashingType.SHA256 -> DigestUtils.sha256(source)
		            HashingType.SHA3_224 -> DigestUtils.sha3_224(source)
		            HashingType.SHA3_256 -> DigestUtils.sha3_256(source)
		            HashingType.SHA3_384 -> DigestUtils.sha3_384(source)
		            HashingType.SHA3_512 -> DigestUtils.sha3_512(source)
		            HashingType.SHA384 -> DigestUtils.sha384(source)
		            HashingType.SHA512 -> DigestUtils.sha512(source)
		            HashingType.SHA512_224 -> DigestUtils.sha512_224(source)
		            HashingType.SHA512_256 -> DigestUtils.sha512_256(source)
		        }
		    )
		}

		/**
		 * 根据指定的哈希类型和源字符串生成字节缓存.
		 *
		 * @param type 哈希类型，决定了使用的哈希算法
		 * @param source 源字符串，将被转换为哈希值
		 * @return 返回一个包含源字符串哈希值的字节缓存对象
		 */
		fun get(type: HashingType, source: String): ByteCache {
		    // 根据哈希类型选择相应的哈希计算方法，并创建ByteCache对象
		    return ByteCache(
		        when (type) {
		            HashingType.MD2 -> DigestUtils.md2(source)
		            HashingType.MD5 -> DigestUtils.md5(source)
		            HashingType.SHA, HashingType.SHA1 -> DigestUtils.sha1(source)
		            HashingType.SHA256 -> DigestUtils.sha256(source)
		            HashingType.SHA3_224 -> DigestUtils.sha3_224(source)
		            HashingType.SHA3_256 -> DigestUtils.sha3_256(source)
		            HashingType.SHA3_384 -> DigestUtils.sha3_384(source)
		            HashingType.SHA3_512 -> DigestUtils.sha3_512(source)
		            HashingType.SHA384 -> DigestUtils.sha384(source)
		            HashingType.SHA512 -> DigestUtils.sha512(source)
		            HashingType.SHA512_224 -> DigestUtils.sha512_224(source)
		            HashingType.SHA512_256 -> DigestUtils.sha512_256(source)
		        }
		    )
		}
	}

	/**
	 * RIPEMD哈希算法类型枚举
	 * 列举了不同长度的RIPEMD哈希算法
	 */
	enum class RipemdType {
	    // 128位的RIPEMD哈希算法
	    RIPEMD128,
	    // 160位的RIPEMD哈希算法，常用于比特币地址的生成
	    RIPEMD160,
	    // 256位的RIPEMD哈希算法
	    RIPEMD256,
	    // 320位的RIPEMD哈希算法
	    RIPEMD320,
	}

	/**
	 * Ripemd 对象用于封装和提供RIPEMD哈希算法的相关功能。
	 * RIPEMD（RACE Integrity Primitives Evaluation Message Digest）是一种基于MD4结构的哈希算法，
	 * 由欧洲的一些密码学家设计。该算法产生128位的哈希值，被广泛应用于数字签名、数据完整性校验等领域。
	 */
	object Ripemd {
		/**
		 * 根据指定的RIPEMD哈希类型，计算给定字节数组的哈希值。
		 *
		 * @param type 指定的RIPEMD哈希类型，如RIPEMD128、RIPEMD160等。
		 * @param source 需要计算哈希值的字节数组。
		 * @return 返回包含计算得到的哈希值的ByteCache对象。
		 */
		fun get(type: RipemdType, source: ByteArray): ByteCache {
		    // 根据不同的RIPEMD类型，实例化相应的摘要对象
		    val rd: GeneralDigest
		    when (type) {
		        RipemdType.RIPEMD128 -> {
		            rd = RIPEMD128Digest()
		        }

		        RipemdType.RIPEMD160 -> {
		            rd = RIPEMD256Digest()
		        }

		        RipemdType.RIPEMD256 -> {
		            rd = RIPEMD256Digest()
		        }

		        RipemdType.RIPEMD320 -> {
		            rd = RIPEMD320Digest()
		        }
		    }
		    // 更新摘要对象的数据，使用字节数组source
		    rd.update(source, 0, source.size)
		    // 创建一个字节数组，用于存储最终的哈希值
		    val hash = ByteArray(rd.digestSize)
		    // 完成哈希计算，将结果存储在hash数组中
		    rd.doFinal(hash, 0)
		    // 返回包含哈希值的ByteCache对象
		    return ByteCache(hash)
		}

		/**
		 * 根据指定的RIPEMD哈希算法类型和源字符串获取字节缓存.
		 *
		 * 此函数提供了一种通过字符串直接获取哈希字节缓存的便捷方法，内部调用了重载的get方法，
		 * 将源字符串转换为字节数组作为参数传递.
		 *
		 * @param type RIPEMD哈希算法的类型，如RIPEMD160等.
		 * @param source 需要进行哈希计算的源字符串.
		 * @return 返回计算得到的哈希字节缓存.
		 */
		fun get(type: RipemdType, source: String): ByteCache {
		    return get(type, source.toByteArray())
		}
	}

	/**
	 * SM3哈希算法对象
	 * 提供了SM3哈希算法的相关功能和属性
	 */
	object Sm3 {
		/**
		 * 从字节数组生成一个 ByteCache 对象，该对象包含 SM3 哈希摘要。
		 *
		 * @param source 字节数组，用作生成 SM3 哈希摘要的源数据。
		 * @return 返回一个包含 SM3 哈希摘要的 ByteCache 对象。
		 */
		fun get(source: ByteArray): ByteCache {
		    // 初始化 SM3 摘要对象
		    val sM3Digest = SM3Digest()
		    // 使用源数据更新 SM3 摘要
		    sM3Digest.update(source, 0, source.size)
		    // 创建一个字节数组，用于存储 SM3 摘要结果
		    val hash = ByteArray(sM3Digest.digestSize)
		    // 完成 SM3 摘要计算，并将结果存储到 hash 数组中
		    sM3Digest.doFinal(hash, 0)
		    // 返回包含 SM3 摘要的 ByteCache 对象
		    return ByteCache(hash)
		}

		/**
		 * 从字符串源中获取字节缓存
		 *
		 * 此方法首先将字符串源转换为字节数组，然后通过调用重载的get方法来获取字节缓存
		 * 使用platform.encode编码方式来转换字符串，这是基于特定平台的编码方式，例如UTF-8
		 *
		 * @param source 要转换为字节缓存的字符串源
		 * @return 转换后的字节缓存对象
		 */
		fun get(source: String): ByteCache {
		    return get(source.toByteArray(Charset.defaultCharset()))
		}
	}

    /**
     * Blake哈希算法类型枚举
     *
     * 该枚举包含了Blake系列的四种哈希算法，每种算法在不同场景下有不同的性能和安全表现
     *
     * - Blake2b: 适用于需要高速和高安全性的场景，产生哈希摘要长度可变，最大支持64字节
     * - Blake2s: 适用于需要在受限环境中运行的场景，产生哈希摘要长度可变，最大支持32字节
     * - Blake2xs: 一种实验性质的算法，不在标准化讨论范围内，提供了介于Blake2b和Blake2s之间的性能和特性
     * - Blake3: 最新的Blake算法，设计上更加简洁，性能更优，同时保持了高安全性
     */
	enum class BlakeType {
		Blake2b,
		Blake2s,
		Blake2xs,
		Blake3
	}


	// 定义一个单例对象Blake，用于提供Blake2b哈希算法的工具方法
	object Blake {
		/**
		 * 根据指定的哈希类型和源数据生成哈希值
		 *
		 * 此函数提供了一个接口，用于根据不同的Blake哈希算法类型，计算给定输入数据的哈希值
		 * 它首先根据提供的哈希类型创建相应的哈希算法实例，然后使用该实例对源数据进行哈希计算，
		 * 最后返回计算得到的哈希值
		 *
		 * @param type 哈希算法类型，决定了使用哪种Blake哈希算法
		 * @param source 需要进行哈希计算的源数据，以字节数组形式提供
		 * @return 返回一个包含计算得到的哈希值的ByteCache对象
		 */
		fun get(type: BlakeType, source: ByteArray): ByteCache {
		    // 根据哈希类型创建相应的ExtendedDigest实例
		    val ed: ExtendedDigest
		    when (type) {
		        BlakeType.Blake2b -> {
		            ed = Blake2bDigest()
		        }

		        BlakeType.Blake2s -> {
		            ed = Blake2sDigest()
		        }

		        BlakeType.Blake2xs -> {
		            ed = Blake2xsDigest()
		        }

		        BlakeType.Blake3 -> {
		            ed = Blake3Digest()
		        }
		    }
		    // 使用源数据更新哈希计算上下文
		    ed.update(source, 0, source.size)
		    // 创建一个用于存储最终哈希值的数组
		    val hash = ByteArray(ed.digestSize)
		    // 完成哈希计算，将结果存储到hash数组中
		    ed.doFinal(hash, 0)
		    // 返回包含计算得到的哈希值的ByteCache对象
		    return ByteCache(hash)
		}

		/**
		 * 根据指定的哈希类型和源字符串获取对应的 ByteCache 对象。
		 *
		 * 此函数提供了一种通过字符串生成特定类型哈希的便捷方式，内部调用了重载的 `get` 函数，
		 * 将字符串转换为字节数组后进行处理。
		 *
		 * @param type 哈希的类型，使用 `BlakeType` 枚举指定。
		 * @param source 作为哈希源的字符串。
		 * @return 返回生成的 ByteCache 对象，包含计算得到的哈希值。
		 */
		fun get(type: BlakeType, source: String): ByteCache {
		    return get(type, source.toByteArray(Charset.defaultCharset()))
		}
	}

	/**
	 * Hex对象用于提供字节序列和十六进制字符串之间的转换功能
	 */
	object Hex {
	    /**
	     * 将字节数组转换为十六进制字符串
	     *
	     * @param source 字节数组，需要被转换为十六进制字符串
	     * @return 转换后的十六进制字符串
	     */
	    fun to(source: ByteArray): String {
	        return HexFormat.of().formatHex(source)
	    }

	    /**
	     * 将字符串转换为十六进制字符串
	     *
	     * @param source 字符串，需要被转换为十六进制字符串
	     * @return 转换后的十六进制字符串
	     */
	    fun to(source: String): String {
	        // 使用平台编码将字符串转换为字节数组，然后调用to方法将字节数组转换为十六进制字符串
	        return to(source.toByteArray(Charset.defaultCharset()))
	    }

	    /**
	     * 将十六进制字符串解析为字节数组
	     *
	     * @param source 十六进制字符串，需要被解析为字节数组
	     * @return 解析后的字节数组
	     */
	    fun from(source: String): ByteArray {
	        return HexFormat.of().parseHex(source)
	    }

	    /**
	     * 将十六进制字符串解析为字符串
	     *
	     * @param source 十六进制字符串，需要被解析为普通字符串
	     * @return 解析后的字符串
	     */
	    fun fromString(source: String): String {
	        // 将十六进制字符串解析为字节数组，然后使用平台编码转换为字符串
	        return String(from(source), Charset.defaultCharset())
	    }
	}

	/**
	 * 操作模式枚举类
	 *
	 * 该枚举类定义了两种操作模式：Encoder（编码器模式）和Decoder（解码器模式）
	 * 这些模式用于指定处理数据的方式，帮助开发者根据上下文选择正确的操作模式
	 */
	enum class Mode {
	    Encoder,    // 编码器模式，用于数据的编码操作
	    Decoder     // 解码器模式，用于数据的解码操作
	}

	/**
	 * Base64工具类，提供统一的接口进行Base64编码和解码
	 */
	object Base64 {
	    /**
	     * 根据指定模式对字节数组进行Base64编码或解码
	     *
	     * @param mode 操作模式，可以是编码器或解码器
	     * @param source 待处理的字节数组
	     * @return 编码后的字节数组，如果模式是编码器；或解码后的字节数组，如果模式是解码器
	     */
	    fun doFind(mode: Mode, source: ByteArray): ByteArray {
	        return when (mode) {
	            Mode.Encoder -> {
	                // 使用Java自带的Base64编码器对源数组进行编码
	                java.util.Base64.getEncoder().encode(source)
	            }

	            Mode.Decoder -> {
	                // 使用Java自带的Base64解码器对源数组进行解码
	                java.util.Base64.getDecoder().decode(source)
	            }
	        }
	    }

	    /**
	     * 根据指定模式对字符串进行Base64编码或解码
	     *
	     * @param mode 操作模式，可以是编码器或解码器
	     * @param source 待处理的字符串
	     * @return 编码后的字符串，如果模式是编码器；或解码后的字符串，如果模式是解码器
	     */
	    fun doFind(mode: Mode, source: String): String {
	        return when (mode) {
	            Mode.Encoder -> {
	                // 将字符串转换为字节数组，然后使用Java自带的Base64编码器进行编码，最后返回编码后的字符串
	                java.util.Base64.getEncoder().encodeToString(source.toByteArray(Charset.defaultCharset()))
	            }

	            Mode.Decoder -> {
	                // 使用Java自带的Base64解码器对字符串进行解码，然后转换为字符串
	                String(java.util.Base64.getDecoder().decode(source))
	            }
	        }
	    }
	}
}