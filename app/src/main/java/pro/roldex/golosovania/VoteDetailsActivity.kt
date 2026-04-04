package pro.roldex.golosovania

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import pro.roldex.golosovania.databinding.ActivityVoteDetailsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VoteDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVoteDetailsBinding
    private lateinit var sessionManager: SessionManager
    private var voteId: Long = -1L
    private var selectedChoiceId: Long? = null
    private var canManage: Boolean = false
    private var hasVoted: Boolean = false
    private var isClosed: Boolean = false

    private val editVoteLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                loadVoteDetails()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVoteDetailsBinding.inflate(layoutInflater)
        sessionManager = SessionManager(this)
        setContentView(binding.root)

        voteId = intent.getLongExtra(EXTRA_VOTE_ID, -1L)
        canManage = intent.getBooleanExtra(EXTRA_CAN_MANAGE, false)
        hasVoted = intent.getBooleanExtra(EXTRA_HAS_VOTED, false)
        if (voteId <= 0L) {
            toast("Invalid vote id")
            finish()
            return
        }

        binding.burger.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        binding.voteButton.setOnClickListener {
            val choiceId = selectedChoiceId
            if (choiceId == null) {
                toast("Select an option")
            } else {
                submitVote(choiceId)
            }
        }

        binding.editVoteButton.setOnClickListener {
            val intent = Intent(this, EditVoteActivity::class.java)
            intent.putExtra(EditVoteActivity.EXTRA_VOTE_ID, voteId)
            editVoteLauncher.launch(intent)
        }

        binding.deleteVoteButton.setOnClickListener {
            confirmDelete()
        }

        binding.manageButtons.visibility = if (canManage) View.VISIBLE else View.GONE
        applyVoteState()
        loadVoteDetails()
    }

    private fun shouldShowResults(): Boolean {
        return hasVoted || isClosed
    }

    private fun applyVoteState() {
        val showResults = shouldShowResults()
        val choicesVisibility = if (showResults) View.GONE else View.VISIBLE
        binding.choicesGroup.visibility = choicesVisibility
        binding.voteButton.visibility = choicesVisibility
        binding.resultsTitle.visibility = if (showResults || canManage) View.VISIBLE else View.GONE
        if (!showResults && !canManage) {
            binding.resultsContainer.removeAllViews()
        }
    }

    private fun loadVoteDetails() {
        ApiClient.votesApi(this).getVoteDetails(voteId)
            .enqueue(object : Callback<VoteDetailsResponse> {
                override fun onResponse(
                    call: Call<VoteDetailsResponse>,
                    response: Response<VoteDetailsResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { vote ->
                            isClosed = VoteUiFormatter.isClosed(vote.lastDate)
                            binding.titleText.text = vote.title
                            binding.subtitleText.text =
                                VoteUiFormatter.detailsText(vote.lastDate, vote.choices.size)
                            applyVoteState()

                            if (!shouldShowResults()) {
                                renderChoices(vote.choices)
                            } else {
                                binding.choicesGroup.removeAllViews()
                            }

                            if (shouldShowResults() || canManage) {
                                loadResults()
                            }
                        }
                    } else if (response.code() == 401) {
                        sessionManager.clearSession()
                        redirectToMain()
                    } else {
                        toast(ApiErrorParser.message(response))
                    }
                }

                override fun onFailure(call: Call<VoteDetailsResponse>, t: Throwable) {
                    toast("Network error: ${t.message}")
                }
            })
    }

    private fun renderChoices(choices: List<ChoiceResponse>) {
        binding.choicesGroup.removeAllViews()
        selectedChoiceId = null

        choices.forEach { choice ->
            val button = RadioButton(this).apply {
                text = choice.title
                id = View.generateViewId()
                textSize = 18f
                setTextColor(getColor(R.color.blue_acsent))
                buttonTintList = getColorStateList(R.color.blue_acsent)
                setOnClickListener { selectedChoiceId = choice.id }
            }
            binding.choicesGroup.addView(button)
        }

        binding.choicesGroup.setOnCheckedChangeListener { _, checkedId ->
            val checkedButton = binding.choicesGroup.findViewById<RadioButton>(checkedId)
            val index = binding.choicesGroup.indexOfChild(checkedButton)
            if (index in choices.indices) {
                selectedChoiceId = choices[index].id
            }
        }
    }

    private fun submitVote(choiceId: Long) {
        ApiClient.votesApi(this).vote(voteId, VoteRequest(choiceId))
            .enqueue(object : Callback<VoteActionResponse> {
                override fun onResponse(
                    call: Call<VoteActionResponse>,
                    response: Response<VoteActionResponse>
                ) {
                    if (response.isSuccessful) {
                        toast("Vote submitted")
                        hasVoted = true
                        applyVoteState()
                        loadResults()
                    } else if (response.code() == 401) {
                        sessionManager.clearSession()
                        redirectToMain()
                    } else {
                        toast(ApiErrorParser.message(response))
                    }
                }

                override fun onFailure(call: Call<VoteActionResponse>, t: Throwable) {
                    toast("Network error: ${t.message}")
                }
            })
    }

    private fun loadResults() {
        ApiClient.votesApi(this).getVoteResults(voteId)
            .enqueue(object : Callback<VoteResultsResponse> {
                override fun onResponse(
                    call: Call<VoteResultsResponse>,
                    response: Response<VoteResultsResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { renderResults(it) }
                    }
                }

                override fun onFailure(call: Call<VoteResultsResponse>, t: Throwable) {
                }
            })
    }

    private fun renderResults(results: VoteResultsResponse) {
        binding.resultsTitle.visibility = View.VISIBLE
        binding.resultsContainer.removeAllViews()

        val totalVotesView = TextView(this).apply {
            text = "Total votes: ${results.totalVotes}"
            textSize = 16f
            setTextColor(getColor(R.color.blue_regular_hint))
        }
        binding.resultsContainer.addView(totalVotesView)

        results.results.forEach { item ->
            val row = TextView(this).apply {
                text = "${item.title}: ${item.votesCount}"
                textSize = 18f
                setTextColor(getColor(R.color.blue_acsent))
                gravity = Gravity.START
                setPadding(0, 12, 0, 0)
            }
            binding.resultsContainer.addView(row)
        }
    }

    private fun confirmDelete() {
        AlertDialog.Builder(this)
            .setTitle("Delete vote")
            .setMessage("Remove this vote?")
            .setPositiveButton("Delete") { _, _ -> deleteVote() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteVote() {
        ApiClient.votesApi(this).deleteVote(voteId)
            .enqueue(object : Callback<DeleteVoteResponse> {
                override fun onResponse(
                    call: Call<DeleteVoteResponse>,
                    response: Response<DeleteVoteResponse>
                ) {
                    if (response.isSuccessful) {
                        toast("Vote deleted")
                        finish()
                    } else if (response.code() == 401) {
                        sessionManager.clearSession()
                        redirectToMain()
                    } else {
                        toast(ApiErrorParser.message(response))
                    }
                }

                override fun onFailure(call: Call<DeleteVoteResponse>, t: Throwable) {
                    toast("Network error: ${t.message}")
                }
            })
    }

    private fun redirectToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val EXTRA_VOTE_ID = "extra_vote_id"
        const val EXTRA_CAN_MANAGE = "extra_can_manage"
        const val EXTRA_HAS_VOTED = "extra_has_voted"
    }
}
