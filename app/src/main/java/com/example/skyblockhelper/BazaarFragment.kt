package com.example.skyblockhelper

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.skyblockhelper.databinding.FragmentBazaarBinding
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

        binding.btnLoad.setOnClickListener {
            loadBazaar()
        }

        loadBazaar()
    }

    private fun loadBazaar() {
        binding.progressBar.visibility = View.VISIBLE
        binding.bazaarCard.visibility = View.GONE
        binding.btnLoad.isEnabled = false

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.getBazaar(HypixelApi.API_KEY)

                withContext(Dispatchers.Main) {
                    if (response.success && response.products != null) {
                        displayBazaar(response.products)
                    } else {
                        Toast.makeText(context, "Błąd pobierania danych bazaaru", Toast.LENGTH_SHORT).show()
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
        binding.bazaarCard.visibility = View.VISIBLE

        val text = StringBuilder()
        text.append("BAZAAR HYPIXEL SKYBLOCK\n\n")
        text.append("Łączna liczba produktów: ${numberFormat.format(products.size)}\n\n")
        text.append("─────────────────────\n\n")
        text.append("TOP 15 PRODUKTÓW \n\n")

        val topProducts = products.values
            .sortedByDescending { it.quickStatus.buyVolume }
            .take(15)

        topProducts.forEachIndexed { index, product ->
            val name = product.product_id
                .replace("_", " ")
                .split(" ")
                .joinToString(" ") { it.lowercase().replaceFirstChar { c -> c.uppercase() } }

            text.append("${index + 1}. $name\n")
            text.append("Kupno: ${formatPrice(product.quickStatus.buyPrice)} monet\n")
            text.append("Sprzedaż: ${formatPrice(product.quickStatus.sellPrice)} monet\n")

            val profit = product.quickStatus.sellPrice - product.quickStatus.buyPrice
            val profitPercent = if (product.quickStatus.buyPrice > 0) {
                (profit / product.quickStatus.buyPrice) * 100
            } else 0.0

            text.append("Zysk: ${formatPrice(profit)} (${String.format("%.1f", profitPercent)}%)\n")
            text.append("Wolumen: ${numberFormat.format(product.quickStatus.buyVolume)}\n\n")
        }

        text.append("Tip: Produkty z dużym wolumenem są najczęściej kupowane!")

        binding.bazaarText.text = text.toString()
    }

    private fun formatPrice(price: Double): String {
        return when {
            price >= 1_000_000_000 -> String.format("%.2fB", price / 1_000_000_000)
            price >= 1_000_000 -> String.format("%.2fM", price / 1_000_000)
            price >= 1_000 -> String.format("%.2fK", price / 1_000)
            else -> String.format("%.2f", price)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}