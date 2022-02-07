package com.elevup.annotation.model

fun List<ProcessedAnnotation>.merge(enumConstant: Any? = null): MergedAnnotations {
    val names = filterIsInstance(ProcessedAnnotation.FieldName::class.java)
    val min = filterIsInstance(ProcessedAnnotation.Minimum::class.java)
    val max = filterIsInstance(ProcessedAnnotation.Maximum::class.java)
    val regexes = filterIsInstance(ProcessedAnnotation.Regex::class.java)
    val deprecated = filterIsInstance(ProcessedAnnotation.Deprecated::class.java)
    val enumNames = filterIsInstance(ProcessedAnnotation.EnumNames::class.java)

    if (names.size > 1) throw IllegalStateException("Multiple 'fieldName' candidates -> $names")
    if (min.size > 1) throw IllegalStateException("Multiple 'min' candidates -> $min")
    if (max.size > 1) throw IllegalStateException("Multiple 'max' candidates -> $max")
    if (regexes.size > 1) throw IllegalStateException("Multiple 'regexes' candidates -> $regexes")
    if (enumNames.size > 1) throw IllegalStateException("Multiple 'enumNames' candidates -> $enumNames")


    return MergedAnnotations(
        fieldName = names.getOrNull(0)?.name ?: enumNames.getOrNull(0)?.mappings?.get(enumConstant),
        min = min.getOrNull(0)?.min,
        max = max.getOrNull(0)?.max,
        regex = regexes.getOrNull(0)?.pattern,
        deprecated = deprecated.joinToString(separator = "\n") { it.message }.takeIf { deprecated.isNotEmpty() },
    )
}
