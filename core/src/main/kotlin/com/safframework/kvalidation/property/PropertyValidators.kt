package com.safframework.kvalidation.property

/**
 * Created by tony on 2019-07-06.
 */
class PropertyValidator<T> (

    private val validationProcessItems: MutableList<ValidationProcessItem<T>> = mutableListOf(),
    private val fieldNames: List<String> = emptyList()) {

    fun mustBe(specName: String = "", assertionFun: T.() -> Boolean): ValidationSpec<T> {

        val spec = ValidationSpec(specName = specName, assertionFun = assertionFun, fieldNames = fieldNames)
        validationProcessItems.add(spec)
        return spec
    }

    fun fieldName(fieldName: String, fFlock: PropertyValidator<T>.() -> Unit) {
        val fieldValidator = PropertyValidator(validationProcessItems, listOf(fieldName))
        fFlock.invoke(fieldValidator)
    }

    fun fieldNames(vararg fieldNames: String, fFlock: PropertyValidator<T>.() -> Unit) {
        val fieldValidator = PropertyValidator(validationProcessItems, fieldNames.toList())
        fFlock.invoke(fieldValidator)
    }

    private fun execValidate(target: T, validateAll: Boolean = false): List<ValidationError> {

        val errors = mutableListOf<ValidationError>()

        validationProcessItems.forEach {

            when(it) {
                is ValidationSpec<T> -> {

                    val error = ValidationError(
                        specName = it.specName,
                        fieldNames = it.fieldNames,
                        errorMessage = it.showMessage(target)
                    )
                    errors.add(error)

                    if (!validateAll) return errors
                }
            }
        }

        return errors
    }

    /**
     * execute validation
     */
    fun validateAll(target: T) = ValidationErrors(execValidate(target = target, validateAll = true))

    fun validateUntilFirst(target: T) = ValidationErrors(execValidate(target = target))

    fun isValid(target: T) = !validateUntilFirst(target).hasErrors()
}