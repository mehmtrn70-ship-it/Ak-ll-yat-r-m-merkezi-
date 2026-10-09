package com.akilliyatirim.merkezi

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class MainActivity : Activity() {
    private val navy = Color.rgb(16, 24, 39)
    private val green = Color.rgb(40, 190, 140)
    private val ink = Color.rgb(28, 39, 54)
    private val muted = Color.rgb(105, 119, 139)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = navy
        window.navigationBarColor = navy
        showHome()
    }

    private fun showHome() {
        val page = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(24), dp(20), dp(28))
            setBackgroundColor(Color.rgb(246, 248, 251))
        }
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(24), dp(20), dp(24))
            setBackgroundColor(navy)
        }
        header.addView(label("AKILLI YATIRIM MERKEZİ", 13, green, true))
        header.addView(label("Yatırım kararlarını\nveriyle planla.", 28, Color.WHITE, true))
        header.addView(label("Fırsatları incele, riski hesapla, hedeflerini takip et.", 14, Color.rgb(204, 215, 228), false))
        page.addView(header, matchWrap())
        page.addView(label("Yatırım araçların", 20, ink, true).apply { setPadding(0, dp(24), 0, dp(12)) })

        val cards = listOf(
            Triple("Fırsat analizi", "Piyasa verileri ve analizler için merkez", "Fırsatları incele"),
            Triple("Alım ve risk planı", "Hedef fiyat ve olası zarar senaryosu", "Plan oluştur"),
            Triple("Sermaye simülatörü", "Örnek getiri, kayıp ve birikim hesabı", "Hesapla"),
            Triple("İzleme listem", "Takip etmek istediğin varlıkları düzenle", "Listeyi aç")
        )
        cards.forEachIndexed { index, item ->
            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(dp(16), dp(16), dp(16), dp(16))
                setBackgroundColor(Color.WHITE)
                elevation = dp(2).toFloat()
            }
            card.addView(label(item.first, 17, ink, true))
            card.addView(label(item.second, 13, muted, false).apply { setPadding(0, dp(7), 0, dp(12)) })
            card.addView(actionButton(item.third) { openModule(index) }, matchWrap())
            val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            params.bottomMargin = dp(14)
            page.addView(card, params)
        }
        page.addView(label("İlk sürüm prototipidir. Canlı piyasa verisi henüz bağlı değildir; yatırım tavsiyesi veya kazanç garantisi sunmaz.", 12, muted, false))
        setContentView(ScrollView(this).apply { addView(page) })
    }

    private fun openModule(index: Int) {
        when (index) {
            1 -> showRiskCalculator()
            2 -> showReturnCalculator()
            0 -> showInfo("Fırsat analizi", "Bu bölüm güvenilir piyasa veri kaynağı bağlandıktan sonra çalışacak. Şimdilik gerçek zamanlı fiyat veya sinyal gösterilmez.")
            else -> showInfo("İzleme listem", "İzleme listesine kayıt özelliği sonraki adımda eklenecek.")
        }
    }

    private fun showRiskCalculator() {
        val page = basePage("Alım ve risk planı")
        val entry = numberField("Örnek alım fiyatı")
        val target = numberField("Hedef fiyat")
        val stop = numberField("Zarar durdurma fiyatı")
        page.addView(entry); page.addView(target); page.addView(stop)
        val result = label("Değerleri girip hesapla. Bu yalnızca senaryo hesabıdır.", 14, muted, false)
        page.addView(result)
        page.addView(actionButton("Risk / getiri oranını hesapla") {
            val e = parse(entry); val t = parse(target); val s = parse(stop)
            result.text = if (e == null || t == null || s == null || e <= 0 || t <= e || s <= 0 || s >= e) {
                "Geçerli değerler gir: hedef alım fiyatından yüksek, zarar durdurma alım fiyatından düşük olmalı."
            } else {
                val gain = (t - e) / e * 100
                val loss = (e - s) / e * 100
                val ratio = (t - e) / (e - s)
                "Olası hedef getirisi: %.2f%%\nOlası kayıp: %.2f%%\nRisk/getiri oranı: 1 : %.2f".format(gain, loss, ratio)
            }
        })
        page.addView(actionButton("Ana ekrana dön") { showHome() })
        setContentView(ScrollView(this).apply { addView(page) })
    }

    private fun showReturnCalculator() {
        val page = basePage("Sermaye simülatörü")
        val capital = numberField("Başlangıç sermayesi (TL)")
        val percent = numberField("Örnek getiri oranı (%)")
        page.addView(capital); page.addView(percent)
        val result = label("Bu hesap tahmindir; getiri garanti değildir.", 14, muted, false)
        page.addView(result)
        page.addView(actionButton("Senaryoyu hesapla") {
            val c = parse(capital); val p = parse(percent)
            result.text = if (c == null || p == null || c < 0 || p < -100) {
                "Lütfen geçerli sermaye ve getiri oranı gir."
            } else {
                val change = c * p / 100
                "Örnek değişim: %.2f TL\nSenaryo sonu tutarı: %.2f TL\nKomisyon, vergi ve enflasyon dahil değildir.".format(change, c + change)
            }
        })
        page.addView(actionButton("Ana ekrana dön") { showHome() })
        setContentView(ScrollView(this).apply { addView(page) })
    }

    private fun showInfo(title: String, message: String) {
        val page = basePage(title)
        page.addView(label(message, 15, ink, false).apply { setPadding(0, 0, 0, dp(20)) })
        page.addView(actionButton("Ana ekrana dön") { showHome() })
        setContentView(ScrollView(this).apply { addView(page) })
    }

    private fun basePage(title: String) = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(dp(20), dp(28), dp(20), dp(28))
        setBackgroundColor(Color.rgb(246, 248, 251))
        addView(label(title, 24, ink, true).apply { setPadding(0, 0, 0, dp(20)) })
    }

    private fun numberField(fieldHint: String) = EditText(this).apply {
        hint = fieldHint
        inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
        textSize = 16f
        setSingleLine(true)
        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.bottomMargin = dp(12)
        layoutParams = params
    }

    private fun parse(field: EditText): Double? = field.text.toString().replace(',', '.').toDoubleOrNull()

    private fun actionButton(value: String, action: () -> Unit) = Button(this).apply {
        text = value
        isAllCaps = false
        setTextColor(Color.WHITE)
        backgroundTintList = android.content.res.ColorStateList.valueOf(navy)
        setOnClickListener { action() }
    }

    private fun label(value: String, size: Int, color: Int, bold: Boolean) = TextView(this).apply {
        text = value
        textSize = size.toFloat()
        setTextColor(color)
        if (bold) setTypeface(typeface, Typeface.BOLD)
    }

    private fun matchWrap() = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
