package edu.codespring.ro.apiaapp.android.selection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.codespring.ro.apiaapp.android.R
import edu.codespring.ro.apiaapp.android.databinding.FragmentSelectionBinding
import edu.codespring.ro.apiaapp.android.utils.getErrorMessage
import edu.codespring.ro.apiaapp.constants.ApiErrors
import edu.codespring.ro.apiaapp.data.model.County
import edu.codespring.ro.apiaapp.data.model.Township
import edu.codespring.ro.apiaapp.ui.selection.SelectionPresenter
import edu.codespring.ro.apiaapp.ui.selection.SelectionView

class SelectionFragment : Fragment(), SelectionView {
    companion object {
        private const val BACK_BUTTON_NEEDED = "backButtonNeeded"
    }

    private lateinit var presenter: SelectionPresenter
    private var currentPage: Int = 0
    private var isError: Boolean = false

    private val adapter: SelectionAdapter = SelectionAdapter {
        presenter.onItemSelected(it, currentPage)
    }
    private var recyclerView: RecyclerView? = null
    private var binding: FragmentSelectionBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        presenter = SelectionPresenter()
        presenter.attachView(this)

        binding = FragmentSelectionBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    private fun setupToolbarSearchView(view: View) {
        val toolbarSearchView = view.findViewById<SearchView>(R.id.selectionToolbarSearchView)
        val toolbarTitle = view.findViewById<TextView>(R.id.selectionToolbarTitle)

        toolbarSearchView.isIconified = true
        toolbarSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (currentPage == 0) {
                    presenter.updateCountySelection(newText)
                } else if (currentPage == 1) {
                    presenter.updateTownshipSelection(newText)
                }
                return false
            }
        })

        toolbarSearchView.setOnSearchClickListener {
            toolbarTitle.maxWidth = 0
            toolbarTitle.visibility = View.INVISIBLE
            toolbarSearchView.maxWidth = Integer.MAX_VALUE
        }

        toolbarSearchView.setOnCloseListener {
            toolbarTitle.maxWidth = android.R.attr.defaultWidth
            toolbarTitle.visibility = View.VISIBLE
            false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // this function sets up the functionality of the search feature
        setupToolbarSearchView(view)

        // Show back button if needed (known from args)
        val backButton = view.findViewById<ImageView>(R.id.selectionToolbarBackButton)
        if (arguments?.getBoolean(BACK_BUTTON_NEEDED) == false) {
            backButton.visibility = View.GONE
        } else {
            backButton.visibility = View.VISIBLE
        }

        // Set back button behaviour
        view.findViewById<ImageView>(R.id.selectionToolbarBackButton).setOnClickListener {
            (activity as AppCompatActivity).onBackPressed()
        }

        recyclerView = binding?.recyclerView
        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = LinearLayoutManager(context)

        binding?.selectText?.text = getString(R.string.please_select_your_county)
        binding?.selectSubmitButton?.text = getString(R.string.next)

        binding?.selectSubmitButton?.setOnClickListener {
            if (isError) {
                when (currentPage) {
                    0 -> presenter.loadCountiesList()
                    1 -> presenter.loadTownshipList()
                }
            } else {
                if (!presenter.validateSelection(currentPage)) {
                    // Show some UI error in the future
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.select_to_continue),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    if (currentPage == 1) {
                        findNavController().navigate(R.id.action_selectionFragment_to_packagesFragment)

                        presenter.saveSettings()

                        Toast.makeText(
                            requireContext(),
                            "Settings saved successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        setUpLoadingScreen()
                        binding?.selectSubmitButton?.text = getString(R.string.select)
                        binding?.selectText?.text = getString(R.string.please_select_your_township)
                        binding?.selectBackButton?.visibility = View.VISIBLE
                        presenter.loadTownshipList()
                        currentPage = 1
                    }
                }
            }
        }
        binding?.selectBackButton?.setOnClickListener {
            binding?.selectSubmitButton?.text = getString(R.string.next)
            binding?.selectText?.text = getString(R.string.please_select_your_county)
            binding?.selectBackButton?.visibility = View.GONE

            presenter.loadCountiesList()
            currentPage = 0
        }

        presenter.loadCountiesList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
        binding = null
    }

    override fun setCountiesList(selectionList: List<County>) {
        adapter.submitList(selectionList)
        showContent()
    }

    override fun setTownshipsList(selectionList: List<Township>) {
        adapter.submitList(selectionList)
        showContent()
    }

    override fun showContent() {
        if (currentPage == 1) {
            binding?.selectBackButton?.visibility = View.VISIBLE
        }
        binding?.recyclerView?.visibility = View.VISIBLE
        binding?.selectText?.visibility = View.VISIBLE
        binding?.errorText?.visibility = View.GONE
        binding?.progress?.visibility = View.GONE
    }

    override fun setUpLoadingScreen() {
        binding?.recyclerView?.visibility = View.GONE
        binding?.selectText?.visibility = View.GONE
        binding?.errorText?.visibility = View.GONE
        binding?.progress?.visibility = View.VISIBLE
    }

    override fun setUpErrorScreen(error: ApiErrors) {
        isError = true
        binding?.selectSubmitButton?.text = getString(R.string.retry)
        if (currentPage == 1) {
            binding?.selectBackButton?.visibility = View.GONE
        }
        binding?.recyclerView?.visibility = View.GONE
        binding?.selectText?.visibility = View.GONE
        binding?.progress?.visibility = View.GONE

        binding?.errorText?.text = getErrorMessage(requireContext(), error)
        binding?.errorText?.visibility = View.VISIBLE
    }
}
