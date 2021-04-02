package com.cyn.mt

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_test.view.*

private const val ARG_PARAM1 = "param1"

/**
 * Fragment
 */
class TestFragment : Fragment() {
    private var param1: String? = null
    lateinit var inflate: View
    lateinit var activity: Activity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as Activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        inflate = inflater.inflate(R.layout.fragment_test, container, false)
        initView()
        initData()
        return inflate
    }

    private fun initView() {
        //recyclerView layoutManager
        inflate.rcData.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        inflate.rcData.adapter = RcAdapter(activity)
    }

    private fun initData() {

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            TestFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }
}