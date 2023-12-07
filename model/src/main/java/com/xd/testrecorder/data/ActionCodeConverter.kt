package com.xd.testrecorder.data

import androidx.compose.ui.text.capitalize

sealed class ActionCodeConverter {
    lateinit var options: CodeConverterOptions

    fun toCode(action: Action): String {
        var code = ""
        if (action.viewContentDescription.isNotEmpty()) {
            code = "cClickWithContentDescription(\"${action.viewContentDescription}\")"
        } else if (action.viewText.isNotEmpty()) {
            code = "clickWithText(\"${action.viewText}\")"
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

data class CodeConverterOptions(var useView: Boolean = false, var lang: String = "kotlin")

data object KotlinConverter : ActionCodeConverter() {
    override fun getFun(name: String): String {
        return "@Test\nfun test${name.capitalize(androidx.compose.ui.text.intl.Locale.current)}}"
    }
}

data object JavaConverter : ActionCodeConverter() {
    override fun getFun(name: String): String {
        return "@Test\npublic void test${name.capitalize(androidx.compose.ui.text.intl.Locale.current)}}"
    }
}