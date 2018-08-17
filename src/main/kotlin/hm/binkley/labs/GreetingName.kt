package hm.binkley.labs

import hm.binkley.labs.ValidationErrorType.REQUIRED
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FIELD
import kotlin.annotation.AnnotationTarget.PROPERTY_GETTER
import kotlin.annotation.AnnotationTarget.TYPE
import kotlin.annotation.AnnotationTarget.VALUE_PARAMETER
import kotlin.reflect.KClass

@Target(FIELD, VALUE_PARAMETER, TYPE, PROPERTY_GETTER)
@Retention(RUNTIME)
@Constraint(validatedBy = [GreetingNameValidator::class])
@MustBeDocumented
annotation class GreetingName(
        val groups: Array<KClass<*>> = [],
        val payload: Array<KClass<out Payload>> = [],
        val message: String = "") {
}

enum class ValidationErrorType {
    REQUIRED
}

class GreetingNameValidator : ConstraintValidator<GreetingName, String> {
    override fun isValid(value: String?,
            context: ConstraintValidatorContext): Boolean {
        if (null == value) return context.failedBecause(REQUIRED)
        if (value.isBlank()) return context.failedBecause(REQUIRED)
        return true
    }
}

fun ConstraintValidatorContext.failedBecause(errorType:
ValidationErrorType): Boolean {
    this.disableDefaultConstraintViolation()
    this.buildConstraintViolationWithTemplate(errorType.toString())
            .addConstraintViolation()
    return false
}
