package pro.roldex.golosovania

data class ChoiceResponse(
    val id: Long,
    val title: String
)

data class CreateVoteRequest(
    val title: String,
    val lastDate: String,
    val choices: List<String>
)

data class VoteSummaryResponse(
    val id: Long,
    val ownerId: Long,
    val title: String,
    val lastDate: String,
    val choicesCount: Int
)

data class VoteDetailsResponse(
    val id: Long,
    val ownerId: Long,
    val title: String,
    val lastDate: String,
    val choices: List<ChoiceResponse>
)

data class UpdateVoteRequest(
    val title: String,
    val lastDate: String
)

data class DeleteVoteResponse(
    val success: Boolean
)

data class VoteRequest(
    val choiceId: Long
)

data class VoteActionResponse(
    val success: Boolean,
    val voteId: Long,
    val choiceId: Long
)

data class VoteResultItemResponse(
    val choiceId: Long,
    val title: String,
    val votesCount: Long
)

data class VoteResultsResponse(
    val voteId: Long,
    val title: String,
    val totalVotes: Long,
    val results: List<VoteResultItemResponse>
)
