package kotlin

import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.reflect.Method
import java.lang.reflect.Type
import kotlin.DynamicSelector.TypeCompareResult
import kotlin.DynamicSelector.isTypeMoreSpecific
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.kotlinFunction


internal fun isMethodSuitable(method: Method, arguments: Array<Any>, skipReceiverCheck: Boolean): Boolean {
    val requiredMethodParameters =
            if (skipReceiverCheck) method.parameterTypes.slice(1..method.parameterCount)
            else method.parameterTypes.toList()


    if (method.isVarArgs) {
        /*
        * arguments already includes receiver and we processed empty vararg case
        */
        if (requiredMethodParameters.size > arguments.size) {
            return false
        }
    } else if (requiredMethodParameters.size < arguments.size - 1) {
        return false
    } else if (requiredMethodParameters.size > arguments.size - 1) {
        //check for default arguments
        if (method.kotlinFunction
                ?.valueParameters
                ?.slice(arguments.size - 1 until method.parameterCount)
                ?.any { !it.isOptional } ?: true) {
            return false
        }
    }

    arguments.drop(1).forEachIndexed { i, argument ->
        if (isTypeMoreSpecific(argument::class.java, requiredMethodParameters[i]).index
                >= TypeCompareResult.WORSE.index) {
            return false
        }
    }/*
    requiredMethodParameters.indices.forEach {
        if (isTypeMoreSpecific(arguments[it + 1]::class.java, requiredMethodParameters[it]).index
                >= TypeCompareResult.WORSE.index) {
            return false
        }
    }*/

    return true
}

internal fun transformMethodIfRequired(targetMethod: Method, methodClass: Class<Any>, argumentsCount: Int): Method {
    if (targetMethod.isVarArgs || targetMethod.parameterCount == argumentsCount) {
        return targetMethod
    }
    val defaultMethodCaller = methodClass.declaredMethods
            .filter { it.isBridge && it.name == "${targetMethod.name}\$default" }
    if (defaultMethodCaller.size != 1) {
        throw DynamicBindException("Cannot find default method caller")
    }

    return defaultMethodCaller.first()
}

internal fun insertDefaultArguments(handle: MethodHandle, targetMethod: Method, methodClass: Class<Any>, argumentsCount: Int): MethodHandle {
    if (targetMethod.isVarArgs || targetMethod.parameterCount == argumentsCount) {
        return handle
    }

    var mask = 0
    val fixedArguments = mutableListOf<Any?>()
    targetMethod.parameterTypes
            .drop(argumentsCount + 1)
            .dropLast(2)
            .forEachIndexed { index, parameter ->
                fixedArguments.add(defaultPrimitiveValue(parameter))
                mask = mask or (1 shl (index % Integer.SIZE))
            }
    fixedArguments.add(mask)
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