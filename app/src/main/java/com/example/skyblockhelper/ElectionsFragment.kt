package com.example.skyblockhelper

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.skyblockhelper.databinding.FragmentElectionsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.Locale

class ElectionsFragment : Fragment() {

    private var _binding: FragmentElectionsBinding? = null
    private val binding get() = _binding!!
    private val api = HypixelApi.create()
    private val numberFormat = NumberFormat.getNumberInstance(Locale("pl", "PL"))

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

        binding.btnLoad.setOnClickListener {
            loadElections()
        }

        loadElections()
    }

    private fun loadElections() {
        binding.progressBar.visibility = View.VISIBLE
        binding.electionsCard.visibility = View.GONE
        binding.btnLoad.isEnabled = false

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.getElection(HypixelApi.API_KEY)

                withContext(Dispatchers.Main) {
                    if (response.success) {
                        displayElections(response)
                    } else {
                        Toast.makeText(context, "Nie udało się pobrać danych wyborów", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Błąd: ${e.message}", Toast.LENGTH_LONG).show()
                    android.util.Log.e("Elections", "Error loading elections", e)
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
        binding.electionsCard.visibility = View.VISIBLE

        val text = StringBuilder()

        if (response.mayor != null) {
            text.append("AKTUALNY MAJOR\n\n")
            text.append("Imię: ${response.mayor.name}\n")
            text.append("Rok kadencji: ${response.mayor.election?.year ?: "?"}\n")
            text.append("\n")
        }


        if (response.mayor?.election != null) {
            text.append("─────────────────────\n\n")
            text.append(" KANDYDACI\n")
            text.append("Rok: ${response.mayor.election.year}\n\n")

            val candidates = response.mayor.election.candidates
            if (candidates != null && candidates.isNotEmpty()) {
                val sorted: List<Candidate> = candidates.sortedByDescending { it.votes }

                sorted.forEachIndexed { index: Int, candidate: Candidate ->
                    val medal = when (index) {
                        0 -> "🥇"
                        1 -> "🥈"
                        2 -> "🥉"
                        else -> "${index + 1}."
                    }

                    text.append("$medal ${candidate.name}\n")


                    if (candidate.perks != null && candidate.perks.isNotEmpty()) {
                        text.append("\n Perki:\n")
                        candidate.perks.forEach { perk: Perk ->
                            text.append("   • ${perk.name}\n")
                            if (!perk.description.isNullOrEmpty()) {
                                val cleanDesc = perk.description.replace("§[0-9a-fk-or]".toRegex(), "")
                                text.append("     $cleanDesc\n")
                            }
                        }
                    }
                    text.append("\n")
                }
            } else {
                text.append("Brak kandydatów\n")
            }
        }

        binding.electionsText.text = text.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}