package edu.codespring.ro.apiaapp.android.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import edu.codespring.ro.apiaapp.android.R
import edu.codespring.ro.apiaapp.android.databinding.FragmentGuestProfileBinding
import edu.codespring.ro.apiaapp.android.utils.getErrorMessage
import edu.codespring.ro.apiaapp.auth.AuthManager
import edu.codespring.ro.apiaapp.constants.ApiErrors
import edu.codespring.ro.apiaapp.constants.AuthErrors
import edu.codespring.ro.apiaapp.constants.Errors
import edu.codespring.ro.apiaapp.data.model.AuthData
import edu.codespring.ro.apiaapp.ui.guestProfile.GuestProfilePresenter
import edu.codespring.ro.apiaapp.ui.guestProfile.GuestProfileView

class GuestProfileFragment : Fragment(), GuestProfileView {
    private var binding: FragmentGuestProfileBinding? = null

    private lateinit var presenter: GuestProfilePresenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        presenter = GuestProfilePresenter()
        presenter.attachView(this)

        // If user is logged in, redirect him to his profile page
        if (AuthManager.isAuthenticated()) {
            findNavController().navigate(R.id.action_guestProfileFragment_to_userProfileFragment)
        }

        binding = FragmentGuestProfileBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.profileButtonSignin?.setOnClickListener {
            presenter.login()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun displayLoginSuccess() {
        findNavController().navigate(R.id.action_guestProfileFragment_to_userProfileFragment)
        Toast.makeText(
            requireContext(),
            getString(R.string.login_success),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun errorToastMessage(error: Errors) {
        val toastMessage: String = getErrorMessage(requireContext(), error)

        toastMessage.let {
            Toast.makeText(
                requireContext(),
                toastMessage,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
