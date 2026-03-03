package com.example.skyblockhelper

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.skyblockhelper.databinding.FragmentElectionsBinding
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ElectionsFragment : Fragment() {

    private var _binding: FragmentElectionsBinding? = null
    private val binding get() = _binding!!
    private val api = HypixelApi.create()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentElectionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnLoad.setOnClickListener { loadElections() }
        loadElections()
    }

    private fun loadElections() {
        binding.progressBar.visibility = View.VISIBLE
        binding.electionsContainer.removeAllViews()
        binding.btnLoad.isEnabled = false

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.getElection(HypixelApi.API_KEY)
                withContext(Dispatchers.Main) {
                    if (response.success) displayElections(response)
                    else Toast.makeText(context, "Błąd danych", Toast.LENGTH_SHORT).show()
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

    private fun displayElections(response: ElectionResponse) {
        val context = context ?: return
        
        response.mayor?.let { mayor ->
            val card = MaterialCardView(context).apply {
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 0, 32)
                layoutParams = params
                radius = 32f
                cardElevation = 8f
            }

            val layout = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(40, 40, 40, 40)
            }

            val title = TextView(context).apply {
                text = "Burmistrz: ${mayor.name}"
                textSize = 22f
                setTextColor(resources.getColor(R.color.hypixel_primary, null))
                setTypeface(null, android.graphics.Typeface.BOLD)
            }
            layout.addView(title)
            
            mayor.perks?.forEach { perk ->
                val pName = TextView(context).apply {
                    text = perk.name
                    setPadding(0, 15, 0, 0)
                    textSize = 16f
                    setTypeface(null, android.graphics.Typeface.BOLD)
                }
                layout.addView(pName)
                
                val pDesc = TextView(context).apply {
                    text = perk.description?.replace("§[0-9a-fk-or]".toRegex(), "")
                    textSize = 14f
                    setPadding(10, 5, 0, 0)
                }
                layout.addView(pDesc)
            }

            card.addView(layout)
            binding.electionsContainer.addView(card)
        }
    }
}