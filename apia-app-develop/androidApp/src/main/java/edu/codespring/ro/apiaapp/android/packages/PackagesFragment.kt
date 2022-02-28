package edu.codespring.ro.apiaapp.android.packages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.Menu
import android.view.MenuInflater
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.codespring.ro.apiaapp.android.R
import edu.codespring.ro.apiaapp.android.databinding.FragmentPackagesBinding
import edu.codespring.ro.apiaapp.android.utils.getErrorMessage
import edu.codespring.ro.apiaapp.constants.ApiErrors
import edu.codespring.ro.apiaapp.data.model.PackageData
import edu.codespring.ro.apiaapp.ui.packages.PackageView
import edu.codespring.ro.apiaapp.ui.packages.PackagePresenter


class PackagesFragment : Fragment(), PackageView {
    companion object {
        private const val BACK_BUTTON_NEEDED = "backButtonNeeded"
        private const val DETAIL_ID = "detailId"
    }

    private lateinit var presenter: PackagePresenter

    private val adapter: PackagesAdapter = PackagesAdapter {
        presenter.openDetailedPackage(it)
    }

    private var recyclerView: RecyclerView? = null
    private var binding: FragmentPackagesBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.selection_menu, menu)

        // Search
        val searchView = menu.findItem(R.id.actionSearch).actionView as SearchView
        searchView.queryHint = getString(R.string.search_by_title)
        searchView.isIconified = true

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                presenter.updatePackagesList(newText)
                return false
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.actionFilter -> {
                val bundle = bundleOf(BACK_BUTTON_NEEDED to true)
                findNavController().navigate(R.id.action_packagesFragment_to_selectionFragment, bundle)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        presenter = PackagePresenter()
        presenter.attachView(this)

        binding = FragmentPackagesBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding?.recyclerView
        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = LinearLayoutManager(context)
        // Init. list
        presenter.loadPackagesList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
        binding = null
    }

    override fun setPackagesList(packageList: List<PackageData>) {
        binding?.progress?.visibility = View.GONE
        binding?.recyclerView?.visibility = View.VISIBLE
        adapter.submitList(packageList)
    }

    override fun navigateToDetails(it: Int) {
        val bundle = bundleOf(DETAIL_ID to it)
        findNavController().navigate(R.id.action_packagesFragment_to_packageDetailFragment, bundle)
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
