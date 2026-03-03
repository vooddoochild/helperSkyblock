package com.example.skyblockhelper

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.skyblockhelper.databinding.FragmentBazaarBinding
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.Locale

class BazaarFragment : Fragment() {

    private var _binding: FragmentBazaarBinding? = null
    private val binding get() = _binding!!
    private val api = HypixelApi.create()
    private val numberFormat = NumberFormat.getNumberInstance(Locale("pl", "PL"))

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBazaarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnLoad.setOnClickListener { loadBazaar() }
        loadBazaar()
    }

    private fun loadBazaar() {
        binding.progressBar.visibility = View.VISIBLE
        binding.bazaarContainer.removeAllViews()
        binding.btnLoad.isEnabled = false

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.getBazaar(HypixelApi.API_KEY)
                withContext(Dispatchers.Main) {
                    if (response.success && response.products != null) {
                        displayBazaar(response.products)
                    } else {
                        Toast.makeText(context, "Błąd danych", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Błąd: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } finally {
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLoad.isEnabled = true
                }
            }
        }
    }

    private fun displayBazaar(products: Map<String, BazaarProduct>) {
        val context = context ?: return
        
        val topProducts = products.values
            .sortedByDescending { it.quickStatus.buyVolume }
            .take(15)

        topProducts.forEach { product ->
            val card = MaterialCardView(context).apply {
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 0, 24)
                layoutParams = params
                radius = 32f
                cardElevation = 6f
            }

            val layout = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(40, 40, 40, 40)
            }

            val name = product.product_id
                .replace("_", " ")
                .split(" ")
                .joinToString(" ") { it.lowercase().replaceFirstChar { c -> c.uppercase() } }

            val title = TextView(context).apply {
                text = name
                textSize = 18f
                setTextColor(resources.getColor(R.color.black, null))
                setTypeface(null, android.graphics.Typeface.BOLD)
            }
            layout.addView(title)

            val prices = TextView(context).apply {
                val buy = formatPrice(product.quickStatus.buyPrice)
                val sell = formatPrice(product.quickStatus.sellPrice)
                text = "Kupno: $buy | Sprzedaż: $sell"
                textSize = 15f
                setPadding(0, 10, 0, 0)
            }
            layout.addView(prices)

            val vol = TextView(context).apply {
                text = "Wolumen: ${numberFormat.format(product.quickStatus.buyVolume)}"
                textSize = 13f
                setPadding(0, 5, 0, 0)
                setTextColor(resources.getColor(R.color.hypixel_primary, null))
            }
            layout.addView(vol)

            card.addView(layout)
            binding.bazaarContainer.addView(card)
        }
    }

    private fun formatPrice(price: Double): String {
        return when {
            price >= 1_000_000_000 -> String.format("%.1fB", price / 1_000_000_000)
            price >= 1_000_000 -> String.format("%.1fM", price / 1_000_000)
            price >= 1_000 -> String.format("%.1fK", price / 1_000)
            else -> String.format("%.2f", price)
        }
    }
}