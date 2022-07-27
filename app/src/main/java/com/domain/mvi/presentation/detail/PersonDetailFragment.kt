package com.domain.mvi.presentation.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.domain.mvi.R
import com.domain.mvi.common.Person
import com.domain.mvi.databinding.FragmentPersonDetailBinding
import com.domain.mvi.util.SnackbarUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PersonDetailFragment : Fragment() {

    private val args: PersonDetailFragmentArgs by navArgs()
    private val viewModel: PersonDetailViewModel by viewModels()

    private var _binding: FragmentPersonDetailBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        args.id?.let { id ->
            viewModel.setEvent(PersonDetailContract.PersonDetailEvent.LoadPersonEvent(id))
        } ?: viewModel.setEvent(PersonDetailContract.PersonDetailEvent.CreateNewPerson)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.nameLayout.isErrorEnabled = !state.isValidName
                    binding.surnameLayout.isErrorEnabled = !state.isValidSurname
                    binding.ageLayout.isErrorEnabled = !state.isValidAge
                    binding.progress.visibility = if(state.isProcessing) View.VISIBLE else View.GONE

                    when (state.state) {
                        PersonDetailContract.ScreenState.LOADING -> {
                        }
                        PersonDetailContract.ScreenState.LOADED -> {
                            with(binding) {
                                state.person?.let { person ->
                                    name.setText(person.name)
                                    surname.setText(person.surname)
                                    age.setText(person.age.toString())
                                }
                                deleteButton.visibility = View.VISIBLE
                                saveButton.visibility = View.VISIBLE
                            }
                        }
                        PersonDetailContract.ScreenState.ERROR -> {
                            if (!state.isValidName) {
                                binding.nameLayout.error = getString(R.string.invalid_name)
                            }
                            if (!state.isValidSurname) {
                                binding.surnameLayout.error = getString(R.string.invalid_surname)
                            }
                            if (!state.isValidAge) {
                                binding.ageLayout.error = getString(R.string.invalid_age)
                            }
                        }
                        PersonDetailContract.ScreenState.NEW -> {
                            with(binding) {
                                deleteButton.visibility = View.GONE
                                saveButton.visibility = View.VISIBLE
                                name.setText("")
                                surname.setText("")
                                age.setText("")
                            }
                        }
                        PersonDetailContract.ScreenState.UPDATED -> {
                            binding.deleteButton.visibility = View.VISIBLE
                            binding.saveButton.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.effect.collect { effect ->
                    when (effect) {
                        PersonDetailContract.PersonDetailEffect.PersonDeletedEffect -> {
                            SnackbarUtils.showSnackBar(
                                fragment = this@PersonDetailFragment,
                                view = binding.personDetailLayout,
                                message = R.string.delete_message,
                                anchorViewId = binding.saveButton.id
                            )
                        }
                        PersonDetailContract.PersonDetailEffect.PersonUpdatedEffect -> {
                            SnackbarUtils.showSnackBar(
                                fragment = this@PersonDetailFragment,
                                view = binding.personDetailLayout,
                                message = R.string.update_message,
                                anchorViewId = binding.saveButton.id
                            )
                        }
                    }
                }
            }
        }

        binding.deleteButton.setOnClickListener {
            viewModel.setEvent(PersonDetailContract.PersonDetailEvent.DeletePersonEvent)
        }
        binding.saveButton.setOnClickListener {
            val person = Person(
                id = viewModel.uiState.value.person?.id,
                name = binding.name.text.toString(),
                surname = binding.surname.text.toString(),
                age = if (binding.age.text.toString().isBlank()) 0 else binding.age.text.toString()
                    .toInt()
            )

            viewModel.setEvent(PersonDetailContract.PersonDetailEvent.UpdatePersonEvent(person))
        }
    }
}