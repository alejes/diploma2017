package kotlin

import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.reflect.Method
import java.lang.reflect.Type
import java.util.ArrayList
import kotlin.DynamicSelector.*
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.kotlinFunction


internal fun isMethodSuitable(method: Method, arguments: Array<Any>, skipReceiverCheck: Boolean): Boolean {
    val isDefaultArgumentCaller = method.name.endsWith(DEFAULT_CALLER_SUFFIX)
    val requiredMethodParameters =
            if (skipReceiverCheck || isDefaultArgumentCaller) method.parameterTypes.slice(1..method.parameterCount - 1)
            else method.parameterTypes.toList()


    if (method.isVarArgs) {
        /*
        * arguments already includes receiver and we processed empty vararg case
        */
        if (requiredMethodParameters.size > arguments.size) {
            return false
        }
    } else if (requiredMethodParameters.size != arguments.size - 1) {
        if (!isDefaultArgumentCaller || (requiredMethodParameters.size - 1 < arguments.size)) {
            return false
        }
    }

    arguments.drop(1).forEachIndexed { i, argument ->
        if (isTypeMoreSpecific(argument::class.java, requiredMethodParameters[i]).index
                >= TypeCompareResult.WORSE.index) {
            return false
        }
    }

    return true
}

internal fun insertDefaultArguments(handle: MethodHandle, targetMethod: Method, owner: Method?, namedArguments: Array<String>?, argumentsCount: Int): MethodHandle {
    if (!targetMethod.name.endsWith(DynamicSelector.DEFAULT_CALLER_SUFFIX)) {
        return handle;
    }

    val realArgumentNames = owner?.kotlinFunction?.valueParameters?.map { it.name } ?: listOf<String>()

    var mask = 0
    val masks = ArrayList<Int>(1)

    val fixedArguments = mutableListOf<Any?>()
    var index = argumentsCount
    for (i in 1..argumentsCount.div(Integer.SIZE)) {
        masks.add(mask)
    }
    targetMethod.parameterTypes
            .drop(argumentsCount + 1)
            .dropLast(2)
            .forEach { parameter ->
                if (index != 0 && index % Integer.SIZE == 0) {
                    masks.add(mask)
                    mask = 0
                }
                fixedArguments.add(defaultPrimitiveValue(parameter))
                mask = mask or (1 shl (index % Integer.SIZE))
                ++index
            }

    if (mask == 0 && masks.isEmpty()) {
        return MethodHandles.insertArguments(handle, argumentsCount + 1, *fixedArguments.toTypedArray());
    }
    masks.add(mask)
    fixedArguments.addAll(masks)
    fixedArguments.add(null)

    return MethodHandles.insertArguments(handle, argumentsCount + 1, *fixedArguments.toTypedArray());
}

private fun defaultPrimitiveValue(type: Type): Any? =
        if (type is Class<*> && type.isPrimitive) {
            when (type) {
                Boolean::class.java -> false
                Char::class.java -> 0.toChar()
                Byte::class.java -> 0.toByte()
                Short::class.java -> 0.toShort()
                Int::class.java -> 0
                Float::class.java -> 0f
                Long::class.java -> 0L
                Double::class.java -> 0.0
                Void.TYPE -> throw IllegalStateException("Parameter with void type is illegal")
                else -> throw UnsupportedOperationException("Unknown primitive: $type")
            }
        } else null