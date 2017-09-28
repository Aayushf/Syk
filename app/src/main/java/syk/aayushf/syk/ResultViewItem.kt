package syk.aayushf.syk

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.mikepenz.fastadapter.items.AbstractItem
import org.jetbrains.anko.find

/**
 * Created by aayushf on 27/9/17.
 */
class ResultViewItem(val count: String = "0", val author: String = "PNAME", val response: String = "Response_Text") : AbstractItem<ResultViewItem, ResultViewItem.ViewHolder>() {
    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    override fun getType(): Int {
        return 0
    }

    override fun getLayoutRes(): Int {
        return R.layout.result_item
    }

    override fun bindView(holder: ViewHolder, payloads: MutableList<Any>?) {
        super.bindView(holder, payloads)
        holder.counttv.text = count
        holder.authortv.text = author
        holder.responsetv.text = response

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val counttv = itemView.find<TextView>(R.id.counttvresult)
        val authortv = itemView.find<TextView>(R.id.tvauthoresult)
        val responsetv = itemView.find<TextView>(R.id.responsetvresult)

    }
}