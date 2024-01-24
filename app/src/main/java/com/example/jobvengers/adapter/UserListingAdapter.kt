package com.example.jobvengers.adapter

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.jobvengers.data.User
import com.example.jobvengers.databinding.ItemUserBinding

class UserListingAdapter(
    private val dataList: List<User>,
    private val listener: SendConnection? = null,
) : RecyclerView.Adapter<UserListingAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemUserBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        Log.d("efjdo",data.toString())
        holder.binding.apply {
            nameTextView.text = data.username
            designationTextView.text = data.phone_no

            chatIconImageView.setOnClickListener {
                val uri = Uri.parse("https://api.whatsapp.com/send?phone=${data.phone_no}")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                if (intent.resolveActivity(chatIconImageView.context.packageManager) != null) {
                    chatIconImageView.context.startActivity(intent)
                } else {

                }
            }

            if (data.connection_status == "accepted") {
                connectTextView.text = "Connected"
                connectTextView.isClickable = false
                connectTextView.isFocusable = false
            } else {
                connectCardView.setOnClickListener {
                    connectTextView.text = "Connected"
                    connectTextView.isClickable = false
                    connectTextView.isFocusable = false
                    listener?.sendConnection(data.job_seeker_id?.toInt() ?: 0)
                }
            }
        }
    }


    override fun getItemCount() = dataList.size
}

interface SendConnection {
    fun sendConnection(id: Int)
}