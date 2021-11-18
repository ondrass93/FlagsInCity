package com.example.flagsincity.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.flagsincity.R
import com.example.flagsincity.database.HistoryDatabase
import com.example.flagsincity.databinding.FragmentHistoryBinding


class HistoryFragment : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentHistoryBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_history, container, false)

        val application = requireNotNull(this.activity).application

        // Create an instance of the ViewModel Factory.
        val dataSource = HistoryDatabase.getInstance(application).historyDatabaseDao
        val viewModelFactory = HistoryViewModelFactory(dataSource, application)

        // Get a reference to the ViewModel associated with this fragment.
        val historyViewModel =
            ViewModelProvider(
                this, viewModelFactory).get(HistoryViewModel::class.java)

        binding.lifecycleOwner = this
        // To use the View Model with data binding, you have to explicitly
        // give the binding object a reference to it.
        binding.historyViewModel = historyViewModel

        val adapter = HistoryActionAdapter()
        binding.historyList.adapter = adapter

        historyViewModel.actions.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        return binding.root
    }


}