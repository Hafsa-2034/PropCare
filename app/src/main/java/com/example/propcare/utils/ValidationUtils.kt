package com.example.propcare.utils

data class ValidationResult(val isValid: Boolean, val error: String? = null)

object ValidationUtils {
    private fun required(value: String, field: String, minLength: Int = 1): ValidationResult =
        when {
            value.isBlank() -> ValidationResult(false, "$field cannot be empty")
            value.trim().length < minLength ->
                ValidationResult(false, "$field must be at least $minLength characters")
            else -> ValidationResult(true)
        }

    fun validateTitle(title: String) = required(title, "Title", 3)
    fun validateDescription(description: String) = required(description, "Description", 10)
    fun validateUnitNumber(unit: String) = required(unit, "Unit number")

    /** True only if every field passes. Handy for enabling the Submit button. */
    fun isComplaintValid(title: String, description: String, unit: String): Boolean =
        validateTitle(title).isValid &&
            validateDescription(description).isValid &&
            validateUnitNumber(unit).isValid
}
