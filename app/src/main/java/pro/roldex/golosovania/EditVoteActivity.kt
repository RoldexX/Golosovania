package pro.roldex.golosovania

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pro.roldex.golosovania.databinding.ActivityEditVoteBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EditVoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditVoteBinding
    private lateinit var sessionManager: SessionManager
    private var voteId: Long = -1L
    private var selectedDeadline: LocalDateTime? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditVoteBinding.inflate(layoutInflater)
        sessionManager = SessionManager(this)
        setContentView(binding.root)

        voteId = intent.getLongExtra(EXTRA_VOTE_ID, -1L)
        if (voteId <= 0L) {
            toast("Invalid vote id")
            finish()
            return
        }

        binding.burger.setOnClickListener {
            finish()
        }

        binding.dateInput.setOnClickListener {
            openDatePicker()
        }

        binding.saveVoteButton.setOnClickListener {
            val deadline = selectedDeadline?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).orEmpty()
            val request = UpdateVoteRequest(
                title = binding.titleInput.text.toString().trim(),
                lastDate = deadline
            )

            if (request.title.isBlank() || request.lastDate.isBlank()) {
                toast("Title and deadline are required")
            } else {
                updateVote(request)
            }
        }

        loadVote()
    }

    private fun loadVote() {
        ApiClient.votesApi(this).getVoteDetails(voteId)
            .enqueue(object : Callback<VoteDetailsResponse> {
                override fun onResponse(
                    call: Call<VoteDetailsResponse>,
                    response: Response<VoteDetailsResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { vote ->
                            binding.titleInput.setText(vote.title)
                            selectedDeadline = parseServerDate(vote.lastDate)
                            binding.dateInput.setText(
                                selectedDeadline?.format(
                                    DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
                                ).orEmpty()
                            )
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

    private fun openDatePicker() {
        val current = selectedDeadline ?: LocalDateTime.now().plusHours(1)
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                openTimePicker(year, month, dayOfMonth)
            },
            current.year,
            current.monthValue - 1,
            current.dayOfMonth
        ).show()
    }

    private fun openTimePicker(year: Int, month: Int, dayOfMonth: Int) {
        val current = selectedDeadline ?: LocalDateTime.now().plusHours(1)
        TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                selectedDeadline = LocalDateTime.of(
                    year,
                    month + 1,
                    dayOfMonth,
                    hourOfDay,
                    minute
                )
                binding.dateInput.setText(
                    selectedDeadline?.format(
                        DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
                    ).orEmpty()
                )
            },
            current.hour,
            current.minute,
            true
        ).show()
    }

    private fun updateVote(request: UpdateVoteRequest) {
        ApiClient.votesApi(this).updateVote(voteId, request)
            .enqueue(object : Callback<VoteDetailsResponse> {
                override fun onResponse(
                    call: Call<VoteDetailsResponse>,
                    response: Response<VoteDetailsResponse>
                ) {
                    if (response.isSuccessful) {
                        setResult(RESULT_OK)
                        finish()
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

    private fun parseServerDate(value: String): LocalDateTime? {
        return runCatching {
            LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        }.recoverCatching {
            LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))
        }.getOrNull()
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
    }
}
