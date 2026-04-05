package pro.roldex.golosovania

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NotificationsAdapter(
    private val onDeleteClick: (NotificationResponse) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<NotificationResponse> = emptyList()

    fun submitList(items: List<NotificationResponse>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = if (items.isEmpty()) 1 else items.size

    override fun getItemViewType(position: Int): Int {
        return if (items.isEmpty()) VIEW_TYPE_EMPTY else VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_EMPTY) {
            val view = LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_1, parent, false)
            EmptyHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_notification, parent, false)
            NotificationHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is EmptyHolder -> holder.bind()
            is NotificationHolder -> holder.bind(items[position], onDeleteClick)
        }
    }

    class EmptyHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {
            val text = itemView.findViewById<TextView>(android.R.id.text1)
            text.text = itemView.context.getString(R.string.empty_notifications)
            text.textSize = 18f
            text.setTextColor(itemView.context.getColor(R.color.blue_regular_hint))
        }
    }

    class NotificationHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val text = view.findViewById<TextView>(R.id.notificationText)
        private val date = view.findViewById<TextView>(R.id.notificationDate)
        private val delete = view.findViewById<TextView>(R.id.deleteNotification)

        fun bind(
            item: NotificationResponse,
            onDeleteClick: (NotificationResponse) -> Unit
        ) {
            text.text = item.text
            date.text = item.date
            delete.setOnClickListener { onDeleteClick(item) }
        }
    }

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_ITEM = 1
    }
}
