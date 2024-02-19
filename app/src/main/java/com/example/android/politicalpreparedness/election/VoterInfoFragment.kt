package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding

class VoterInfoFragment : Fragment() {

    private lateinit var binding: FragmentVoterInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVoterInfoBinding.inflate(inflater)
        val viewModelFactory = VoterInfoViewModel.Factory(
            VoterInfoFragmentArgs.fromBundle(requireArguments()).argElection,
            requireActivity().application
        )
        val viewModel = ViewModelProvider(
            this, viewModelFactory
        )[VoterInfoViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.followButton.setOnClickListener {
            viewModel.followActionElection()
        }

        return binding.root
    }
}