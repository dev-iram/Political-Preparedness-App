package com.example.android.politicalpreparedness

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.databinding.FragmentLaunchBinding
import com.jaredrummler.android.widget.AnimatedSvgView

class LaunchFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentLaunchBinding.inflate(inflater)
        binding.lifecycleOwner = this
//        (activity as? AppCompatActivity)?.supportActionBar?.title = ""

        val actionBar = (activity as? AppCompatActivity)?.supportActionBar
        actionBar?.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM)
        actionBar?.setCustomView(R.layout.custom_actionbar_title)

        binding.findRepresentativeButton.setOnClickListener { navToRepresentatives() }
        binding.upcomingElectionsButton.setOnClickListener { navToElections() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        view.findViewById<View>(R.id.homepage_image)?.startAnimation(aniFade)
        homeButtonsAnimation()
        homeScreenImageAnimation()
    }

//    private fun homeTitleTextAnimation() {
//        val aniFade: Animation? = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
//        view?.findViewById<View>(R.id.political_preparedness_title)?.startAnimation(aniFade)
//    }

    private fun homeScreenImageAnimation() {
        val svgView: AnimatedSvgView? = view?.findViewById(R.id.homepage_image)
        svgView?.start()
    }

    private fun homeButtonsAnimation() {
        val upcomingElectionButton: Button? = view?.findViewById(R.id.upcoming_elections_button)
        val findRepresentativeButton: Button? = view?.findViewById(R.id.find_representative_button)

        // Check if buttons are found before animating
        upcomingElectionButton?.let {
            it.alpha = 0f
            it.translationY = 50f
            it.animate()
                .alpha(1f)
                .translationYBy(-50f)
                .setDuration(1500)
        }

        findRepresentativeButton?.let {
            it.alpha = 0f
            it.translationY = 50f
            it.animate()
                .alpha(1f)
                .translationYBy(-50f)
                .setDuration(1500)
        }
    }

    override fun onResume() {
        super.onResume()
        homeButtonsAnimation()
        homeScreenImageAnimation()
    }

    private fun navToElections() {
        this.findNavController().navigate(LaunchFragmentDirections.actionLaunchFragmentToElectionsFragment())
    }

    private fun navToRepresentatives() {
        this.findNavController().navigate(LaunchFragmentDirections.actionLaunchFragmentToRepresentativeFragment())
    }
}
