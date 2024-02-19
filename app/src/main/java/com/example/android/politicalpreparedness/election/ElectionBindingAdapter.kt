package com.example.android.politicalpreparedness.election

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.example.android.politicalpreparedness.representative.model.Representative
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@BindingAdapter("bindElectionDate")
fun bindElectionDate(textView: TextView, date: Date) {
    val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy - HH:mm zzz", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("America/New_York") // Set the time zone to EDT
    textView.text = dateFormat.format(date)
}

@BindingAdapter("dataElection")
fun bindData(recyclerView: RecyclerView, data: List<Election>?) {
    (recyclerView.adapter as ElectionListAdapter).submitList(data)
}

@BindingAdapter("dataRepresentative")
fun bindDataRepresentative(recyclerView: RecyclerView, data: List<Representative>?) {
    (recyclerView.adapter as RepresentativeListAdapter).submitList(data)
}

@BindingAdapter("bindTextButton")
fun bindData(button: Button, isFollow: Boolean) {
    if (isFollow) {
        button.text = button.context.getString(R.string.voter_unfollow)
    } else {
        button.text = button.context.getString(R.string.add_to_follow)
    }
}


@BindingAdapter("link")
fun bindLinkClick(textView: TextView, url: String?) {
    textView.setOnClickListener {
        if (!url.isNullOrEmpty()) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            textView.context.startActivity(intent)
        }
    }
}

