package edu.codespring.ro.apiaapp.android.profile

import android.app.AlertDialog
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
import edu.codespring.ro.apiaapp.android.databinding.FragmentPlannedPackagesBinding
import edu.codespring.ro.apiaapp.android.utils.getErrorMessage
import edu.codespring.ro.apiaapp.auth.AuthManager
import edu.codespring.ro.apiaapp.constants.ApiErrors
import edu.codespring.ro.apiaapp.constants.Errors
import edu.codespring.ro.apiaapp.data.model.PlannedPackageData
import edu.codespring.ro.apiaapp.ui.packagePlanner.PackagePlannerPresenter
import edu.codespring.ro.apiaapp.ui.packagePlanner.PackagePlannerView

class PlannedPackagesFragment : Fragment(), PackagePlannerView {

    private var binding: FragmentPlannedPackagesBinding? = null
    private var recyclerView: RecyclerView? = null
    private lateinit var presenter: PackagePlannerPresenter
    private val adapter: PlannedPackagesAdapter = PlannedPackagesAdapter { id: Int, action: ActionEnum ->
        chooseOption(id, action)
    }

    companion object {
        private const val DETAIL_ID = "detailId"
        private const val PACKAGE_ID = "packageId"
    }

    private fun chooseOption(id: Int, action: ActionEnum) {
        when (action) {
            ActionEnum.DETAIL -> presenter.openDetailedPackage(id)
            ActionEnum.DELETE -> deletePlannedPackage(id)
            ActionEnum.ADD_TO_ACTIVE -> addToActivePackages(id)
            ActionEnum.ADD_FIELD -> navigateToAddField(id)
            else -> throw IllegalStateException("Action $action is not handled.")
        }
    }

    private fun navigateToAddField(id: Int){
        val bundle = bundleOf(PACKAGE_ID to id)
        findNavController().navigate(R.id.addFieldToPlanner, bundle)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        presenter = PackagePlannerPresenter()
        presenter.attachView(this)

        binding = FragmentPlannedPackagesBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding?.recyclerView
        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = LinearLayoutManager(context)

        // Load packages if user is authenticated
        if (AuthManager.isAuthenticated()) {
            presenter.loadPlannedPackagesList()
        }
    }

    override fun navigateToDetails(id: Int) {
        val bundle = bundleOf(DETAIL_ID to id)
        // We are still on UserProfileFragment, because PlannedPackagesFragment is in a TabLayout!
        findNavController().navigate(R.id.action_userProfileFragment_to_packageDetailFragment, bundle)
    }

    override fun setPlannedPackagesList(packageList: List<PlannedPackageData>) {
        binding?.progress?.visibility = View.GONE
        binding?.recyclerView?.visibility = View.VISIBLE

        val sumOfplannedPackages = presenter.getSumOfPlannedPackages()
        binding?.summarizeTxt?.text = "Value of packages: $sumOfplannedPackages €"

        adapter.submitList(packageList)
    }

    override fun addToActivePackages(id: Int) {
        presenter.addToActivePackages(id.toLong())
    }

    override fun deletePlannedPackage(id: Int) {
        val builder = AlertDialog.Builder(this.activity, R.style.AlertDialogStyle)
        builder.setTitle(getString(R.string.confirm_delete))
        builder.setMessage(getString(R.string.want_to_delete))
        builder.setPositiveButton(getString(R.string.yes)) { dialog, _ ->
            if (AuthManager.isAuthenticated()) {
                presenter.deletePlannedPackage(id.toLong())
                presenter.loadPlannedPackagesList()
            }

            val sumOfplannedPackages = presenter.getSumOfPlannedPackages()
            binding?.summarizeTxt?.text = "Value of packages: $sumOfplannedPackages €"
            dialog.cancel()
        }
        builder.setNegativeButton(getString(R.string.no)) { dialog, _ ->
            dialog.cancel()
        }
        val alert: AlertDialog = builder.create()
        alert.show()
    }

    override fun successToastMessage() {
        val message = getString(R.string.added_to_active)

        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_SHORT
        ).show()
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
        binding?.recyclerView?.visibility = View.GONE
        binding?.progress?.visibility = View.VISIBLE
    }

    override fun setUpErrorScreen(error: ApiErrors) {
        binding?.recyclerView?.visibility = View.GONE
        binding?.progress?.visibility = View.GONE

        binding?.errorText?.text = getErrorMessage(requireContext(), error)
        binding?.errorText?.visibility = View.VISIBLE
    }
}
