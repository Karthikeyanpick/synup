package com.example.synup.ui.main

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.synup.R
import com.example.synup.data.model.ExcludedList
import com.example.synup.data.model.VariantGroups
import com.example.synup.data.model.VariantList
import com.example.synup.data.model.Variations
import com.example.synup.data.source.remote.handler.response.VariantResponse
import com.rmkrishna.recycler.bind
import com.rmkrishna.recycler.update
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.layout_recyclerview.view.*

class MainFragment : Fragment() {

    private var mainViewModel: MainViewModel? = null
    private var list: ArrayList<ExcludedList>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (isNetworkAvailable(activity!!)) {
            mainViewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)

            mainViewModel!!.init()

            mainViewModel!!.getVariantRepository()?.observe(this, Observer {
                list = arrayListOf<ExcludedList>()
                for (i in it.variants.exclude_list) {
                    for (j in i) {
                        list!!.add(j)
                    }
                }
                listRecyclerView.update(it.variants.variant_groups)
            })
        } else {
            Toast.makeText(activity, "Check Your Internet Connection!!", Toast.LENGTH_LONG).show()
        }

        listRecyclerView.bind(
            arrayListOf<VariantGroups>(),
            ContextCompat.getColor(activity!!, R.color.divider)
        ).map(R.layout.layout_recyclerview, { true }) { item: VariantGroups ->

            groupTxt.text = item.name

            checkbox1.text = item.variations[0].name + "(" + item.variations[0].inStock + ")"
            checkbox2.text = item.variations[1].name + "(" + item.variations[0].inStock + ")"
            checkbox3.text = item.variations[2].name + "(" + item.variations[0].inStock + ")"

            checkbox1.id = item.variations[0].id.toInt()
            checkbox2.id = item.variations[1].id.toInt()
            checkbox3.id = item.variations[2].id.toInt()

            price1.text = "\u20B9 " + item.variations[0].price.toString()
            price2.text = "\u20B9 " + item.variations[1].price.toString()
            price3.text = "\u20B9 " + item.variations[2].price.toString()

        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }
}