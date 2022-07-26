package com.domain.mvi.presentation.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.domain.mvi.R
import com.domain.mvi.databinding.FragmentPersonListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PersonListFragment : Fragment() {

    private var _binding: FragmentPersonListBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<PersonListViewModel>()
    private lateinit var personAdapter: PersonListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        personAdapter = PersonListAdapter() {
            viewModel.setEvent(PersonListContract.PersonListEvent.PersonSelectedEvent(it))
        }
        with(binding.personRecycler) {
            setHasFixedSize(true)
            adapter = personAdapter
        }

        viewModel.setEvent(PersonListContract.PersonListEvent.LoadPeopleEvent(false))

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    println("---- state: $state")
                    when (state.state) {
                        PersonListContract.ScreenState.LOADING -> {
                            binding.progress.visibility = View.VISIBLE
                        }
                        PersonListContract.ScreenState.REFRESHING -> {
                            binding.personSwipeRefresh.isRefreshing = true
                        }
                        PersonListContract.ScreenState.LOADED -> {
                            binding.progress.visibility = View.GONE
                            binding.personSwipeRefresh.isRefreshing = false
                            personAdapter.submitList(state.people)
                        }
                        PersonListContract.ScreenState.ERROR -> {

                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.effect.collect { effect ->
                    when (effect) {
                        is PersonListContract.PersonListEffect.ShowPersonEffect -> {
                            val action = PersonListFragmentDirections.actionPersonListFragmentToPersonDetailFragment(effect.id)
                            findNavController().navigate(action)
                        }
                    }
                }
            }
        }

        binding.personSwipeRefresh.setOnRefreshListener {
            viewModel.setEvent(PersonListContract.PersonListEvent.LoadPeopleEvent(true))
        }

        binding.fab.setOnClickListener {
            val action = PersonListFragmentDirections.actionPersonListFragmentToPersonDetailFragment(null)
            findNavController().navigate(action)
        }
    }
}