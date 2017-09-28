package syk.aayushf.syk

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.mikepenz.fastadapter.items.AbstractItem
import org.jetbrains.anko.find

/**
 * Created by aayushf on 24/9/17.
 */
class ResponseViewItem(val r:String): AbstractItem<ResponseViewItem, ResponseViewItem.ViewHolder>() {
    override fun getType(): Int {
        return 0

    }

    override fun getLayoutRes(): Int {
        return R.layout.response_item

    }

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    override fun bindView(holder: ViewHolder, payloads: MutableList<Any>?) {
        super.bindView(holder, payloads)
        holder.tvresponse.text = r

    }

    class ViewHolder(val itemVieww: View) : RecyclerView.ViewHolder(itemVieww){
        val tvresponse = itemVieww.find<TextView>(R.id.response_tv)
    }
}