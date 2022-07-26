package com.domain.mvi.presentation.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.domain.mvi.common.Person
import com.domain.mvi.databinding.PersonListItemBinding

class PersonListAdapter constructor(val handleClick: (id: String) -> Unit) :
    ListAdapter<Person, PersonListAdapter.PersonListViewHolder>(DiffUtilCallback()) {

    private class DiffUtilCallback : DiffUtil.ItemCallback<Person>() {
        override fun areItemsTheSame(oldItem: Person, newItem: Person): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Person, newItem: Person): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonListViewHolder {
        return PersonListViewHolder(
            PersonListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            handleClick
        )
    }

    override fun onBindViewHolder(holder: PersonListViewHolder, position: Int) {
        holder.populate(getItem(position))
    }

    inner class PersonListViewHolder(
        private val binding: PersonListItemBinding,
        val handleClick: (id: String) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun populate(person: Person) {
            binding.personItem.setOnClickListener {
                handleClick(person.id!!)
            }
            binding.personName.text = "${person.name}, ${person.surname}"
            binding.personAge.text = person.age.toString()
        }
    }
}