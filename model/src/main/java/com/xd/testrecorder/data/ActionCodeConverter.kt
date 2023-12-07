package com.xd.testrecorder.data

import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale

sealed class ActionCodeConverter {
    lateinit var options: CodeConverterOptions

    fun toCode(action: Action): String {
        var code = ""
        if (action.viewContentDescription.isNotEmpty()) {
            code = "clickContentDescription(\"${action.viewContentDescription}\")"
        } else if (action.viewText.isNotEmpty()) {
            code = "clickText(\"${action.viewText}\")"
        }
        return code
    }

    abstract fun getFun(name: String): String

    companion object {
        fun getConverter(options: CodeConverterOptions): ActionCodeConverter {
            val converter: ActionCodeConverter = if (options.lang == "Kotlin") {
                KotlinConverter
            } else {
                JavaConverter
            }
            converter.options = options
            return converter
        }
    }
}

data class CodeConverterOptions(var useView: Boolean = false, var lang: String = "Kotlin")

data object KotlinConverter : ActionCodeConverter() {
    override fun getFun(name: String): String {
        return "@Test\nfun test${name.capitalize(Locale.current)}() {"
    }
}

data object JavaConverter : ActionCodeConverter() {
    override fun getFun(name: String): String {
        return "@Test\npublic void test${name.capitalize(Locale.current)}() {"
    }
}