package kotlin

import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.lang.reflect.Method
import java.lang.reflect.Type
import kotlin.DynamicOverloadResolution.DEFAULT_CALLER_SUFFIX
import kotlin.DynamicOverloadResolution.isTypeMoreSpecific
import kotlin.DynamicSelector.*
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.kotlinFunction


internal fun isMethodSuitable(method: Method, arguments: Array<Any?>, skipReceiverCheck: Boolean): Boolean {
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
        if ((argument != null) &&
                (requiredMethodParameters[i] != null) &&
                (isTypeMoreSpecific(argument!!::class.java, requiredMethodParameters[i]).index
                        >= TypeCompareResult.WORSE.index)) {
            return false
        }
    }

    return true
}

fun insertDefaultArgumentsAndNamedParameters(handle: MethodHandle, targetMethod: Method, owner: Method?, namedArguments: Array<String>?, arguments: Array<Any>): MethodHandle {
    val isDefaultCaller = targetMethod.name.endsWith(DEFAULT_CALLER_SUFFIX)
    if ((namedArguments?.isEmpty() ?: false) && !isDefaultCaller) {
        return handle;
    }

    val methodNamedArguments = owner?.kotlinFunction?.valueParameters?.mapNotNull { it.name } ?: listOf<String>()

    if (namedArguments == null || namedArguments.isEmpty())
        return insertDefaultArguments(handle, targetMethod, arguments.size - 1)
    else
        return insertNamedArguments(handle, targetMethod, namedArguments, methodNamedArguments, arguments, isDefaultCaller)
}

internal fun insertDefaultArguments(handle: MethodHandle, targetMethod: Method, argumentsCount: Int): MethodHandle {
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

internal fun permuteMethodType(type: MethodType, permutation: ArrayList<Int>): MethodType {
    val newType: MethodType = MethodType.methodType(type.returnType())
    val newTypeParameters = MutableList<Class<*>>(permutation.size, { _ -> Any::class.java })

    permutation.forEachIndexed { index, it -> newTypeParameters[it] = type.parameterType(index) }

    return newType.appendParameterTypes(
            newTypeParameters
    )
}

internal fun insertNamedArguments(handle: MethodHandle, targetMethod: Method, currentNamedArguments: Array<String>, methodNamedArguments: List<String>, arguments: Array<Any>, defaultCaller: Boolean): MethodHandle {
    var mask = 0
    val masks = ArrayList<Int>(1)

    val fixedArguments = mutableListOf<Any?>()
    var currentHandle = handle
    val argumentsCount = arguments.size - 1 - currentNamedArguments.size
    var insertPosition = 1
    var argumentPosition = 0
    val permutation = ArrayList<Int>(1)
    permutation.add(0)


    targetMethod.parameterTypes
            .dropLast(if (defaultCaller) 2 else 0)
            .drop(if (defaultCaller) 1 else 0)
            .forEach { parameter ->
                val namedIndex = currentNamedArguments.indexOf(methodNamedArguments[argumentPosition])
                if (namedIndex >= 0) {
                    val newPosition = argumentsCount + namedIndex
                    permutation.add(1 + newPosition)
                    ++insertPosition

                } else if (argumentPosition < argumentsCount) {
                    permutation.add(insertPosition)
                    ++insertPosition
                } else {
                    if (argumentPosition != 0 && argumentPosition % Integer.SIZE == 0) {
                        masks.add(mask)
                        mask = 0
                    }
                    currentHandle = MethodHandles.insertArguments(currentHandle, insertPosition, defaultPrimitiveValue(parameter))
                    mask = mask or (1 shl (argumentPosition % Integer.SIZE))
                }
                ++argumentPosition
            }

    if (mask == 0 && masks.isEmpty()) {
        val newType = permuteMethodType(currentHandle.type(), permutation);
        return MethodHandles.permuteArguments(currentHandle, newType, *permutation.toIntArray())
    }
    masks.add(mask)
    fixedArguments.addAll(masks)
    fixedArguments.add(null)

    currentHandle = MethodHandles.insertArguments(currentHandle, insertPosition, *fixedArguments.toTypedArray())
    val newType = permuteMethodType(currentHandle.type(), permutation)

    return MethodHandles.permuteArguments(currentHandle, newType, *permutation.toIntArray())
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