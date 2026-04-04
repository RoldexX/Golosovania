package pro.roldex.golosovania

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface VotesApi {
    @POST("votes")
    fun createVote(@Body request: CreateVoteRequest): Call<VoteDetailsResponse>

    @GET("votes")
    fun getVotes(): Call<List<VoteSummaryResponse>>

    @GET("votes/my")
    fun getMyVotes(): Call<List<VoteSummaryResponse>>

    @GET("votes/participated")
    fun getParticipatedVotes(): Call<List<VoteSummaryResponse>>

    @GET("votes/{voteId}")
    fun getVoteDetails(@Path("voteId") voteId: Long): Call<VoteDetailsResponse>

    @PATCH("votes/{voteId}")
    fun updateVote(
        @Path("voteId") voteId: Long,
        @Body request: UpdateVoteRequest
    ): Call<VoteDetailsResponse>

    @DELETE("votes/{voteId}")
    fun deleteVote(@Path("voteId") voteId: Long): Call<DeleteVoteResponse>

    @POST("votes/{voteId}/vote")
    fun vote(@Path("voteId") voteId: Long, @Body request: VoteRequest): Call<VoteActionResponse>

    @GET("votes/{voteId}/results")
    fun getVoteResults(@Path("voteId") voteId: Long): Call<VoteResultsResponse>
}
