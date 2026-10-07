package com.tyro.candlepsych

data class Prediction(val bullish:Int,val bearish:Int,val signal:String,val confidence:String)

object Predictor {
    fun fromCandle(close:Float,open:Float):Prediction {
        val bullish=if(close>=open) 65 else 35
        val bearish=100-bullish
        val signal=when {
            bullish>=60 -> "BULLISH"
            bearish>=60 -> "BEARISH"
            else -> "NO SIGNAL"
        }
        val confidence=if(signal=="NO SIGNAL") "LOW" else "MEDIUM"
        return Prediction(bullish,bearish,signal,confidence)
    }
}