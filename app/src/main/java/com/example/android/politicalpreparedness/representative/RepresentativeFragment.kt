package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.example.android.politicalpreparedness.representative.model.Representative
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mikepenz.itemanimators.SlideRightAlphaAnimator
import java.util.Locale


class RepresentativeFragment : Fragment() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var currentLocation: Location
    private lateinit var binding: FragmentRepresentativeBinding
    private lateinit var contxt: Context
    private val viewModel: RepresentativeViewModel by lazy {
        ViewModelProvider(this)[RepresentativeViewModel::class.java]
    }

    companion object {
        const val REQUEST_LOCATION_PERMISSION = 1
        const val ADDRESS_ONE = "LINE-1"
        const val ADDRESS_TWO = "LINE-2"
        const val CITY = "CITY"
        const val ZIP = "ZIP"
        const val STATE = "STATE"
        const val PROGRESS = "PROGRESS"
        const val START = "START"
        const val END = "END"
        const val LIST_DATA = "LIST_DATA"

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(ADDRESS_ONE, binding.addressLine1Input.text.toString())
        outState.putString(ADDRESS_TWO, binding.addressLine2Input.text.toString())
        outState.putString(CITY, binding.cityInput.text.toString())
        outState.putString(STATE, binding.stateTemp.text.toString())
        outState.putString(ZIP, binding.zipcodeInput.text.toString())
        outState.putFloat(PROGRESS, binding.motion.progress)
        outState.putInt(START, binding.motion.startState)
        outState.putInt(END, binding.motion.endState)
        outState.putParcelableArrayList(LIST_DATA, ArrayList(viewModel.representatives.value))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val adapter = RepresentativeListAdapter()
        binding = FragmentRepresentativeBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.representativeRecycle.adapter = adapter
        binding.buttonLocation.setOnClickListener {
            checkLocationPermissions()
            binding.myRepresentatives.text = getString(R.string.my_representatives_header_text)
            myRepresentativeAnimation()
        }
        representativesSearchAnimation()
        myRepresentativeAnimation()
        representativeButtonsAnimation()
        representativeSearchAnimations()
        binding.representativeRecycle.itemAnimator = SlideRightAlphaAnimator()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        binding.stateSelect.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                binding.stateTemp.text = selectedItem
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing or handle if needed
            }
        }
        binding.buttonSearch.setOnClickListener {
            val address = Address(
                binding.addressLine1Input.text.toString(),
                binding.addressLine2Input.text.toString(),
                binding.cityInput.text.toString(),
                binding.stateTemp.text.toString(),
                binding.zipcodeInput.text.toString()
            )
            binding.myRepresentatives.text = getString(R.string.my_representatives_header_text)
            myRepresentativeAnimation()
            hideKeyboard()
            viewModel.fetchRepresentatives(address.toFormattedString())
        }
        restoreDataBundle(savedInstanceState)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        representativesSearchAnimation()
        myRepresentativeAnimation()
        representativeButtonsAnimation()
        representativeSearchAnimations()
    }

    private fun restoreDataBundle(savedInstanceState: Bundle?) {
        val address = Address(
            line1 = savedInstanceState?.getString(ADDRESS_ONE),
            line2 = savedInstanceState?.getString(ADDRESS_TWO),
            city = savedInstanceState?.getString(CITY),
            state = savedInstanceState?.getString(STATE),
            zip = savedInstanceState?.getString(ZIP)
        )
        val data: ArrayList<Representative>? = savedInstanceState?.getParcelableArrayList(LIST_DATA)
        viewModel.representatives.value = data
        binding.address = address
        binding.motion.post {
            if (savedInstanceState != null) {
                binding.motion.setTransition(
                    savedInstanceState.getInt(
                        START, 0
                    ), savedInstanceState.getInt(END, 0)
                )
                binding.motion.progress = savedInstanceState.getFloat(PROGRESS, 0F)
            }
        }
        binding.motion.progress = savedInstanceState?.getFloat(PROGRESS) ?: 0F
        selectLocation(address.state)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        contxt = context
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.size > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }

    private fun checkLocationPermissions(): Boolean {
        return if (isPermissionGranted()) {
            getLocation()
            return true
        } else {
            requestPermissions(
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
            return true
        }
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) === PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                currentLocation = location
                val address = geoCodeLocation(currentLocation)
                binding.address = address
                selectLocation(address.state)
                viewModel.fetchRepresentatives(address.toFormattedString())
            }
        }
    }

    private fun selectLocation(state: String?) {
        if (!state.isNullOrEmpty()) {
            val adapter = ArrayAdapter(
                contxt,
                android.R.layout.simple_spinner_item,
                contxt.resources.getStringArray(R.array.states)
            )
            val position: Int = adapter.getPosition(state)
            binding.stateSelect.setSelection(position)
        }
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(contxt, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
            ?.map { address ->
                Address(
                    address.thoroughfare,
                    address.subThoroughfare,
                    address.locality,
                    address.adminArea,
                    address.postalCode
                )
            }?.first() ?: Address("", "", "", "", "")
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }


    private fun representativesSearchAnimation() {
        binding.representativeSearch.apply {
            alpha = 0f
            translationY = 50f
            animate()
                .alpha(1f)
                .translationYBy(-50f)
                .setDuration(1500)
        }
    }

    private fun myRepresentativeAnimation() {
        binding.myRepresentatives.apply {
            alpha = 0f
            translationY = 50f
            animate()
                .alpha(1f)
                .translationYBy(-50f)
                .setDuration(1500)
        }
    }

    private fun representativeButtonsAnimation() {
        binding.buttonSearch.apply {
            alpha = 0f
            translationY = 50f
            animate()
                .alpha(1f)
                .translationYBy(-50f)
                .setDuration(1500)
        }
        binding.buttonLocation.apply {
            alpha = 0f
            translationY = 50f
            animate()
                .alpha(1f)
                .translationYBy(-50f)
                .setDuration(1500)
        }
    }

    private fun representativeSearchAnimations() {
        binding.addressLine1Input.apply {
            alpha = 0f
            translationY = 50f
            animate()
                .alpha(1f)
                .translationYBy(-50f)
                .setDuration(1500)
        }
        binding.addressLine2Input.apply {
            alpha = 0f
            translationY = 50f
            animate()
                .alpha(1f)
                .translationYBy(-50f)
                .setDuration(1500)
        }
        binding.cityInput.apply {
            alpha = 0f
            translationY = 50f
            animate()
                .alpha(1f)
                .translationYBy(-50f)
                .setDuration(1500)
        }
        binding.stateTemp.apply {
            alpha = 0f
            translationY = 50f
            animate()
                .alpha(1f)
                .translationYBy(-50f)
                .setDuration(1500)
        }
        binding.zipcodeInput.apply {
            alpha = 0f
            translationY = 50f
            animate()
                .alpha(1f)
                .translationYBy(-50f)
                .setDuration(1500)
        }
        binding.stateSelect.apply {
            alpha = 0f
            translationY = 50f
            animate()
                .alpha(1f)
                .translationYBy(-50f)
                .setDuration(1500)
        }
    }
}