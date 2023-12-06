package com.xd.mvvm.testrecorder.data

sealed class ActionCodeConverter {
    abstract fun toCode(action: Action): String
}

data object DefaultConverter : ActionCodeConverter() {
    override fun toCode(action: Action): String {
        var code = ""
        if(action.viewContentDescription.isNotEmpty()){
            code = "findAndClickWithContentDescription(${action.viewContentDescription})"
        } else if(action.viewText.isNotEmpty()){
            code = "findAndClickWithContentDescription(${action.viewContentDescription})"
        }
        return code
    }
}