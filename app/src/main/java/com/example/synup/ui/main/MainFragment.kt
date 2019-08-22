package com.example.synup.ui.main

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.synup.R
import com.example.synup.data.model.ExcludedList
import com.example.synup.data.model.VariantGroups
import com.rmkrishna.recycler.bind
import com.rmkrishna.recycler.update
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.layout_recyclerview.view.*

class MainFragment : Fragment() {

    private var mainViewModel: MainViewModel? = null
    private var list: ArrayList<ExcludedList>? = null
    private val idList = arrayListOf<String>()
    var isChesse = false
    var isSmall = false
    var isMustard = false

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

            radioButton1.text = item.variations[0].name + "(" + item.variations[0].inStock + ")"
            radioButton2.text = item.variations[1].name + "(" + item.variations[0].inStock + ")"
            radioButton3.text = item.variations[2].name + "(" + item.variations[0].inStock + ")"

            radioButton1.id = item.variations[0].id.toInt()
            radioButton2.id = item.variations[1].id.toInt()
            radioButton3.id = item.variations[2].id.toInt()

            price1.text = "\u20B9 " + item.variations[0].price.toString()
            price2.text = "\u20B9 " + item.variations[1].price.toString()
            price3.text = "\u20B9 " + item.variations[2].price.toString()


            for (i in 0..2) {
                idList.add(item.variations[i].id)
            }


            radioGrp.setOnCheckedChangeListener { group, checkedId ->
                val thinId = idList[0].toInt()
                val thickId = idList[1].toInt()
                val cheeseId = idList[2].toInt()

                val smallId = idList[3].toInt()
                val mediumId = idList[4].toInt()
                val largeId = idList[5].toInt()

                val manchurianId = idList[6].toInt()
                val tomatoId = idList[7].toInt()
                val mustardId = idList[8].toInt()

                val buttonThin = group.findViewById<AppCompatRadioButton>(thinId)
                val buttonThick = group.findViewById<AppCompatRadioButton>(thickId)
                val buttonCheese = group.findViewById<AppCompatRadioButton>(cheeseId)

                val buttonSmall = group.findViewById<AppCompatRadioButton>(smallId)
                val buttonMedium = group.findViewById<AppCompatRadioButton>(mediumId)
                val buttonlarge = group.findViewById<AppCompatRadioButton>(largeId)

                val buttonmanchurianId = group.findViewById<AppCompatRadioButton>(manchurianId)
                val buttontomatoId = group.findViewById<AppCompatRadioButton>(tomatoId)
                val buttonMustard = group.findViewById<AppCompatRadioButton>(mustardId)

                when (checkedId) {

                    list?.get(0)?.variation_id?.toInt() ->
                        if (isSmall) {
                            buttonCheese?.isChecked = false
                            isChesse = buttonCheese?.isChecked ?: false
                        } else {
                            buttonCheese?.isChecked = true
                            isChesse = buttonCheese?.isChecked ?: false
                        }


                    list?.get(3)?.variation_id?.toInt() ->
                        if (isSmall) {
                            buttonMustard?.isChecked = false
                            isMustard = buttonMustard?.isChecked ?: false
                        } else {
                            buttonMustard?.isChecked = true
                            isMustard = buttonMustard?.isChecked ?: false
                        }

                    list?.get(1)?.variation_id?.toInt() ->
                        if (isChesse || isMustard) {
                            buttonSmall?.isChecked = false
                            isSmall = buttonSmall?.isChecked ?: false
                        } else {
                            buttonSmall?.isChecked = true
                            isSmall = buttonSmall?.isChecked ?: false
                        }

                    thinId -> {
                        isChesse = false
                    }
                    thickId -> {
                        isChesse = false
                    }
                    mediumId -> {
                        isSmall = false
                    }
                    largeId -> {
                        isSmall = false
                    }
                    manchurianId -> {
                        isMustard = false
                    }
                    tomatoId -> {
                        isMustard = false
                    }

                    else -> {

                    }


                }


            }

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