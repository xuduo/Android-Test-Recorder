package com.xd.testrecorder.data

sealed class ActionCodeConverter {
    lateinit var options: CodeConverterOptions
    abstract fun toCode(action: Action): String

    companion object{
        fun getConverter(options: CodeConverterOptions):ActionCodeConverter{
            val converter = DefaultConverter
            converter.options = options
            return converter
        }
    }

}

data class CodeConverterOptions(val useView: Boolean = false)

data object DefaultConverter : ActionCodeConverter() {
    override fun toCode(action: Action): String {
        var code = ""
        if (action.viewContentDescription.isNotEmpty()) {
            code = "findAndClickWithContentDescription(\"${action.viewContentDescription}\")"
        } else if (action.viewText.isNotEmpty()) {
            code = "findAndClickWithText(\"${action.viewText}\")"
        }
        return code
    }
}