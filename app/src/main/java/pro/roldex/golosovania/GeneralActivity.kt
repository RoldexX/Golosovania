package pro.roldex.golosovania

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import pro.roldex.golosovania.databinding.ActivityGeneralBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GeneralActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGeneralBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var votesAdapter: VotesAdapter
    private var allVotes: List<VoteSummaryResponse> = emptyList()
    private var participatedVoteIds: Set<Long> = emptySet()
    private var screenMode: String = MODE_ALL

    private val createVoteLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                loadVotes()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGeneralBinding.inflate(layoutInflater)
        sessionManager = SessionManager(this)
        setContentView(binding.root)

        if (sessionManager.getToken().isNullOrBlank()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        screenMode = intent.getStringExtra(EXTRA_MODE) ?: MODE_ALL
        votesAdapter = VotesAdapter { vote, hasVoted -> openVoteDetails(vote, hasVoted) }

        binding.votesRecycler.layoutManager = LinearLayoutManager(this)
        binding.votesRecycler.adapter = votesAdapter

        binding.burger.setOnClickListener {
            startActivity(Intent(this@GeneralActivity, ProfileActivity::class.java))
        }

        binding.createVoteFab.setOnClickListener {
            createVoteLauncher.launch(Intent(this, CreateVoteActivity::class.java))
        }

        binding.searchInput.doAfterTextChanged { text ->
            renderVotes(filterVotes(text?.toString().orEmpty()))
        }
    }

    override fun onResume() {
        super.onResume()
        loadVotes()
    }

    private fun loadVotes() {
        loadParticipatedVotes {
            val call = when (screenMode) {
                MODE_MY -> ApiClient.votesApi(this).getMyVotes()
                MODE_PARTICIPATED -> ApiClient.votesApi(this).getParticipatedVotes()
                else -> ApiClient.votesApi(this).getVotes()
            }

            call.enqueue(object : Callback<List<VoteSummaryResponse>> {
                override fun onResponse(
                    call: Call<List<VoteSummaryResponse>>,
                    response: Response<List<VoteSummaryResponse>>
                ) {
                    if (response.isSuccessful) {
                        allVotes = response.body().orEmpty()
                        renderVotes(filterVotes(binding.searchInput.text?.toString().orEmpty()))
                    } else if (response.code() == 401) {
                        sessionManager.clearSession()
                        redirectToMain()
                    } else {
                        toast(ApiErrorParser.message(response) { getString(R.string.request_failed, it) })
                    }
                }

                override fun onFailure(call: Call<List<VoteSummaryResponse>>, t: Throwable) {
                    toast(getString(R.string.network_error, t.message ?: getString(R.string.request_failed, -1)))
                }
            })
        }
    }

    private fun loadParticipatedVotes(onLoaded: () -> Unit) {
        ApiClient.votesApi(this).getParticipatedVotes()
            .enqueue(object : Callback<List<VoteSummaryResponse>> {
                override fun onResponse(
                    call: Call<List<VoteSummaryResponse>>,
                    response: Response<List<VoteSummaryResponse>>
                ) {
                    if (response.isSuccessful) {
                        participatedVoteIds = response.body().orEmpty().map { it.id }.toSet()
                        onLoaded()
                    } else if (response.code() == 401) {
                        sessionManager.clearSession()
                        redirectToMain()
                    } else {
                        toast(ApiErrorParser.message(response) { getString(R.string.request_failed, it) })
                    }
                }

                override fun onFailure(call: Call<List<VoteSummaryResponse>>, t: Throwable) {
                    toast(getString(R.string.network_error, t.message ?: getString(R.string.request_failed, -1)))
                }
            })
    }

    private fun filterVotes(query: String): List<VoteSummaryResponse> {
        if (query.isBlank()) return allVotes
        return allVotes.filter { it.title.contains(query.trim(), ignoreCase = true) }
    }

    private fun renderVotes(votes: List<VoteSummaryResponse>) {
        val emptyMessage = when (screenMode) {
            MODE_MY -> getString(R.string.empty_created_votes)
            MODE_PARTICIPATED -> getString(R.string.empty_participated_votes)
            else -> getString(R.string.empty_votes)
        }

        val votedIdsForScreen = when (screenMode) {
            MODE_PARTICIPATED -> votes.map { it.id }.toSet()
            else -> participatedVoteIds
        }

        votesAdapter.submitList(
            votes = votes,
            votedIds = votedIdsForScreen,
            emptyMessage = emptyMessage
        )
    }

    private fun openVoteDetails(vote: VoteSummaryResponse, hasVoted: Boolean) {
        val intent = Intent(this, VoteDetailsActivity::class.java)
        intent.putExtra(VoteDetailsActivity.EXTRA_VOTE_ID, vote.id)
        intent.putExtra(VoteDetailsActivity.EXTRA_CAN_MANAGE, screenMode == MODE_MY)
        intent.putExtra(VoteDetailsActivity.EXTRA_HAS_VOTED, hasVoted)
        startActivity(intent)
    }

    private fun redirectToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val EXTRA_MODE = "extra_mode"
        const val MODE_ALL = "all"
        const val MODE_MY = "my"
        const val MODE_PARTICIPATED = "participated"
    }
}
