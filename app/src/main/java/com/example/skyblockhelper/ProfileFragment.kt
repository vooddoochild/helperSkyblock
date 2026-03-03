package com.example.skyblockhelper

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.skyblockhelper.databinding.FragmentProfileBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val api = HypixelApi.create()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSearch.setOnClickListener {
            val nick = binding.etNick.text.toString().trim()
            if (nick.isNotEmpty()) {
                searchPlayer(nick)
            } else {
                Toast.makeText(context, "Wpisz nick gracza!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun searchPlayer(nick: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.resultCard.visibility = View.GONE
        binding.btnSearch.isEnabled = false

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.getPlayer(HypixelApi.API_KEY, nick)

                withContext(Dispatchers.Main) {
                    if (response.success && response.player != null) {
                        displayPlayer(response.player)
                    } else {
                        Toast.makeText(context, "Nie znaleziono gracza: $nick", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Błąd połączenia: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } finally {
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSearch.isEnabled = true
                }
            }
        }
    }

    private fun displayPlayer(player: Player) {
        binding.resultCard.visibility = View.VISIBLE

        val rank = when {
            player.rank != null && player.rank != "NORMAL" -> player.rank
            player.newPackageRank != null -> player.newPackageRank
            player.packageRank != null -> player.packageRank
            else -> "Brak rangi"
        }

        val formattedRank = rank.replace("_", " ")
            .split(" ")
            .joinToString(" ") { it.lowercase().replaceFirstChar { c -> c.uppercase() } }

        binding.tvDisplayName.text = player.displayname
        binding.tvRank.text = formattedRank
        binding.tvUuid.text = player.uuid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}