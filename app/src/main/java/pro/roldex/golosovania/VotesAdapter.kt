package pro.roldex.golosovania

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VotesAdapter(
    private val onVoteClick: (VoteSummaryResponse, Boolean) -> Unit
) : RecyclerView.Adapter<VotesAdapter.VoteViewHolder>() {

    private var items: List<VoteSummaryResponse> = emptyList()
    private var votedIds: Set<Long> = emptySet()
    private var emptyMessage: String = ""

    fun submitList(
        votes: List<VoteSummaryResponse>,
        votedIds: Set<Long>,
        emptyMessage: String
    ) {
        items = votes
        this.votedIds = votedIds
        this.emptyMessage = emptyMessage
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = if (items.isEmpty()) 1 else items.size

    override fun getItemViewType(position: Int): Int {
        return if (items.isEmpty()) VIEW_TYPE_EMPTY else VIEW_TYPE_VOTE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoteViewHolder {
        return if (viewType == VIEW_TYPE_EMPTY) {
            val view = LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_1, parent, false)
            VoteViewHolder.Empty(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.test_view, parent, false)
            VoteViewHolder.Item(view)
        }
    }

    override fun onBindViewHolder(holder: VoteViewHolder, position: Int) {
        when (holder) {
            is VoteViewHolder.Empty -> holder.bind(emptyMessage)
            is VoteViewHolder.Item -> holder.bind(
                vote = items[position],
                hasVoted = votedIds.contains(items[position].id),
                onVoteClick = onVoteClick
            )
        }
    }

    sealed class VoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        class Empty(view: View) : VoteViewHolder(view) {
            fun bind(message: String) {
                val text = itemView.findViewById<TextView>(android.R.id.text1)
                text.text = message
                text.textSize = 18f
                text.setTextColor(itemView.context.getColor(R.color.blue_regular_hint))
            }
        }

        class Item(view: View) : VoteViewHolder(view) {
            private val card = view.findViewById<LinearLayout>(R.id.voteCard)
            private val title = view.findViewById<TextView>(R.id.vote_name)
            private val time = view.findViewById<TextView>(R.id.vote_time)
            private val status = view.findViewById<ImageView>(R.id.test_is_passed)

            fun bind(
                vote: VoteSummaryResponse,
                hasVoted: Boolean,
                onVoteClick: (VoteSummaryResponse, Boolean) -> Unit
            ) {
                title.text = vote.title
                time.text = VoteUiFormatter.deadlineText(vote.lastDate)
                status.visibility = if (hasVoted) View.VISIBLE else View.INVISIBLE
                card.setOnClickListener { onVoteClick(vote, hasVoted) }
            }
        }
    }

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_VOTE = 1
    }
}
