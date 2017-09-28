package syk.aayushf.syk

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.mikepenz.fastadapter.items.AbstractItem
import org.jetbrains.anko.find

/**
 * Created by aayushf on 14/9/17.
 */
class PlayerViewItem(val p: Player) : AbstractItem<PlayerViewItem, PlayerViewItem.ViewHolder>() {
    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    override fun getLayoutRes(): Int {
        return R.layout.playerviewitem
    }

    override fun getType(): Int {
        return 0
    }

    override fun bindView(holder: ViewHolder, payloads: MutableList<Any>?) {
        super.bindView(holder, payloads)
        holder.nametv.text = p.name

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nametv = itemView.find<TextView>(R.id.nametv)


    }

}