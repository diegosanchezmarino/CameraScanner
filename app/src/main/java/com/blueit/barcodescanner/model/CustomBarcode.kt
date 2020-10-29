package com.blueit.barcodescanner.model

class CustomBarcode {

    var rawValue: String
    var isCorrect: Boolean

    constructor(rawValue: String) {
        this.rawValue = rawValue
        this.isCorrect = false
    }


    override fun hashCode(): Int {
        return rawValue.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        var result = false

        if ((other is CustomBarcode) && other.rawValue == this.rawValue)
            result = true

        return result
    }


}