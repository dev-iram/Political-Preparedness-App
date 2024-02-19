package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener
import com.mikepenz.itemanimators.AlphaCrossFadeAnimator
import com.mikepenz.itemanimators.ScaleUpAnimator
import com.mikepenz.itemanimators.SlideRightAlphaAnimator
import org.w3c.dom.Text


class ElectionsFragment : Fragment() {

    private val viewModel: ElectionsViewModel by lazy {
        ViewModelProvider(this)[ElectionsViewModel::class.java]
    }
    private lateinit var binding: FragmentElectionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentElectionBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // Set adapter for upcoming elections RecyclerView
        binding.upcomingElectionsRecycler.adapter = ElectionListAdapter(ElectionListener {
            viewModel.goToDetailPage(it)
        })

        binding.upcomingElectionsRecycler.itemAnimator = SlideRightAlphaAnimator()

        // Apply ScaleUpAnimator to the RecyclerView
        binding.savedElectionsRecycler.itemAnimator = SlideRightAlphaAnimator()

        // Set adapter for saved elections RecyclerView
        binding.savedElectionsRecycler.adapter = ElectionListAdapter(ElectionListener {
            viewModel.goToDetailPage(it)
        })

        val actionBar = (activity as? AppCompatActivity)?.supportActionBar
        actionBar?.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM)
        actionBar?.setCustomView(R.layout.custom_actionbar_title)

        electionsTitlesAnimations()

        // Observe navigation data
        viewModel.navigateData.observe(viewLifecycleOwner) {
            it?.let {
                this.findNavController().navigate(
                    ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(it)
                )
                viewModel.setNullNav()
            }
        }
        return binding.root
    }

    private fun electionsTitlesAnimations() {
        binding.upcomingElectionsHeader.apply {
            alpha = 0f
            translationY = 50f
            animate()
                .alpha(1f)
                .translationYBy(-50f)
                .setDuration(1500)
        }

        binding.saveElectionsHeader.apply {
            alpha = 0f
            translationY = 50f
            animate()
                .alpha(1f)
                .translationYBy(-50f)
                .setDuration(1500)
        }
    }
}