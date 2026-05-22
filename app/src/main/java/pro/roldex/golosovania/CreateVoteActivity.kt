package pro.roldex.golosovania

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pro.roldex.golosovania.databinding.ActivityCreateVoteBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CreateVoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateVoteBinding
    private lateinit var sessionManager: SessionManager
    private var selectedDeadline: LocalDateTime? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateVoteBinding.inflate(layoutInflater)
        sessionManager = SessionManager(this)
        setContentView(binding.root)

        binding.burger.setOnClickListener {
            finish()
        }

        binding.dateInput.setOnClickListener {
            openDatePicker()
        }

        binding.createVoteSubmitButton.setOnClickListener {
            val deadline = selectedDeadline?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).orEmpty()
            val request = CreateVoteRequest(
                title = binding.titleInput.text.toString().trim(),
                lastDate = deadline,
                choices = listOf(
                    binding.choiceOneInput.text.toString().trim(),
                    binding.choiceTwoInput.text.toString().trim(),
                    binding.choiceThreeInput.text.toString().trim(),
                    binding.choiceFourInput.text.toString().trim()
                ).filter { it.isNotBlank() }
            )

            if (request.title.isBlank() || request.lastDate.isBlank() || request.choices.size < 2) {
                toast(getString(R.string.create_vote_validation))
            } else {
                createVote(request)
            }
        }
    }

    private fun openDatePicker() {
        val now = selectedDeadline ?: LocalDateTime.now().plusHours(1)
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                openTimePicker(year, month, dayOfMonth)
            },
            now.year,
            now.monthValue - 1,
            now.dayOfMonth
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

    private fun createVote(request: CreateVoteRequest) {
        ApiClient.votesApi(this).createVote(request)
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
                        toast(ApiErrorParser.message(response) { getString(R.string.request_failed, it) })
                    }
                }

                override fun onFailure(call: Call<VoteDetailsResponse>, t: Throwable) {
                    toast(getString(R.string.network_error, t.message ?: getString(R.string.request_failed, -1)))
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
}
