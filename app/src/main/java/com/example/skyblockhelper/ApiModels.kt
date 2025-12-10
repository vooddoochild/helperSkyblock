package com.example.skyblockhelper

import com.google.gson.annotations.SerializedName

data class PlayerResponse(
    val success: Boolean,
    val player: Player?
)

data class Player(
    val displayname: String,
    val uuid: String,
    val rank: String?,
    val packageRank: String?,
    val newPackageRank: String?
)
data class ElectionResponse(
    val success: Boolean,
    val mayor: Mayor?,
    val current: Election?
)

data class Mayor(
    val key: String,
    val name: String,
    val perks: List<Perk>?,
    val election: ElectionInfo?
)

data class Election(
    val year: Int,
    val votes: Int?,
    val candidates: List<Candidate>?
)

data class Candidate(
    val key: String,
    val name: String,
    val perks: List<Perk>?,
    val votes: Int
)

data class Perk(
    val name: String,
    val description: String?
)

data class ElectionInfo(
    val year: Int,
    val votes: Int?,
    val candidates: List<Candidate>?
)

data class BazaarResponse(
    val success: Boolean,
    val products: Map<String, BazaarProduct>?
)

data class BazaarProduct(
    val product_id: String,
    @SerializedName("quick_status") val quickStatus: QuickStatus
)

data class QuickStatus(
    val buyPrice: Double,
    val sellPrice: Double,
    val buyVolume: Int,
    val sellVolume: Int
)