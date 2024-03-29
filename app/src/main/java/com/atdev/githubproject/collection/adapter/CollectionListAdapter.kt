package com.atdev.githubproject.collection.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.atdev.githubproject.R
import com.atdev.githubproject.search.listeners.AdapterDeleteItemClickListener
import com.atdev.githubproject.search.model.RepositoryCollectionEntity
import com.squareup.picasso.Picasso
import kotlin.properties.Delegates

class CollectionListAdapter(
    private val listener: AdapterDeleteItemClickListener
) :
    RecyclerView.Adapter<CollectionListAdapter.ViewHolder>() {

    var dataSet: List<RepositoryCollectionEntity> by Delegates.observable(listOf()) { _, _, _ ->
        notifyDataSetChanged()
    }

    inner class ViewHolder(
        view: View,
    ) : RecyclerView.ViewHolder(view) {
        val owner: TextView = view.findViewById(R.id.owner)
        val name: TextView = view.findViewById(R.id.name)
        val watchers: TextView = view.findViewById(R.id.watchers)
        var forks: TextView = view.findViewById(R.id.forks)
        var language: TextView = view.findViewById(R.id.language)
        var profileImage: ImageView = view.findViewById(R.id.profileImage)
        var delete: ImageView = view.findViewById(R.id.delete)

        init {
            delete.isVisible = true

            itemView.setOnClickListener {
                val intent =
                    Intent(Intent.ACTION_VIEW, Uri.parse(dataSet[adapterPosition].html_url))
                view.context.startActivity(intent)
            }

            delete.setOnClickListener {
                listener.onItemDeleteClickListener(dataSet[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_item_repository, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        Picasso.get().load(dataSet[position].avatar_url).noFade().fit().centerCrop()
            .into(viewHolder.profileImage)

        viewHolder.owner.text = dataSet[position].owner_login
        viewHolder.name.text = dataSet[position].name

        viewHolder.watchers.text = dataSet[position].watchers_count
        viewHolder.forks.text = dataSet[position].forks_count

        if (dataSet[position].language.isNullOrEmpty()) {
            viewHolder.language.visibility = View.VISIBLE
            viewHolder.language.text = dataSet[position].language
        }
    }

    override fun getItemCount() = dataSet.size
}
