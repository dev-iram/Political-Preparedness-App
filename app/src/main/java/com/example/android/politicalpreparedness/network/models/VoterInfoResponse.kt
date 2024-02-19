package com.example.android.politicalpreparedness.network.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class VoterInfoResponse(
    val election: Election,
    val state: List<State>? = null
)

data class VoteInfo(val election: Election, val state: List<State>? = null)

fun VoterInfoResponse.asVoteInfo(): VoteInfo {
    return VoteInfo(this.election, this.state)
}
