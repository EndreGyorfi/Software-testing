package edu.codespring.ro.apiaapp.android.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.codespring.ro.apiaapp.android.R
import edu.codespring.ro.apiaapp.android.databinding.FragmentActivePackagesBinding
import edu.codespring.ro.apiaapp.android.utils.getErrorMessage
import edu.codespring.ro.apiaapp.auth.AuthManager
import edu.codespring.ro.apiaapp.constants.ApiErrors
import edu.codespring.ro.apiaapp.constants.Errors
import edu.codespring.ro.apiaapp.data.model.ActivePackageData
import edu.codespring.ro.apiaapp.ui.activePackages.ActivePackagesPresenter
import edu.codespring.ro.apiaapp.ui.activePackages.ActivePackagesView

class ActivePackagesFragment : Fragment(), ActivePackagesView {

    private var binding: FragmentActivePackagesBinding? = null
    private var recyclerView: RecyclerView? = null
    private lateinit var presenter: ActivePackagesPresenter
    private val adapter: ActivePackagesAdapter = ActivePackagesAdapter { i: Int, action: ActionEnum ->
        chooseOption(i, action)
    }

    companion object {
        private const val DETAIL_ID = "detailId"
        private const val SHOW_ADD_BUTTON = "showAddButton"
    }

    private fun chooseOption(i: Int, action: ActionEnum) {
        when (action) {
            ActionEnum.DETAIL -> presenter.openDetailedPackage(i)
            else -> throw IllegalStateException("Action $action is not handled.")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        presenter = ActivePackagesPresenter()
        presenter.attachView(this)

        binding = FragmentActivePackagesBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding?.fieldRecyclerView
        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = LinearLayoutManager(context)
    }

    override fun onResume() {
        super.onResume()

        // Load packages if user is authenticated
        if (AuthManager.isAuthenticated()) {
            presenter.loadActivePackagesList()
        }
    }

    override fun navigateToDetails(it: Int) {
        val bundle = bundleOf(ActivePackagesFragment.DETAIL_ID to it, ActivePackagesFragment.SHOW_ADD_BUTTON to false)
        findNavController().navigate(R.id.action_userProfileFragment_to_packageDetailFragment, bundle)
    }

    override fun setActivePackagesList(activePackageList: List<ActivePackageData>) {
        binding?.progress?.visibility = View.GONE
        binding?.fieldRecyclerView?.visibility = View.VISIBLE

        adapter.submitList(activePackageList)
    }

    override fun errorToast(error: Errors) {
        val toastMessage: String = getErrorMessage(requireContext(), error)

        toastMessage.let {
            Toast.makeText(
                requireContext(),
                toastMessage,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun setUpLoadingScreen() {
        binding?.fieldRecyclerView?.visibility = View.GONE
        binding?.progress?.visibility = View.VISIBLE
    }

    override fun setUpErrorScreen(error: ApiErrors) {
        binding?.fieldRecyclerView?.visibility = View.GONE
        binding?.progress?.visibility = View.GONE

        binding?.errorText?.text = getErrorMessage(requireContext(), error)
        binding?.errorText?.visibility = View.VISIBLE
    }
}
