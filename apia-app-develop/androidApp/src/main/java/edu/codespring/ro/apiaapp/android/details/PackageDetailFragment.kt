package edu.codespring.ro.apiaapp.android.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import edu.codespring.ro.apiaapp.android.R
import edu.codespring.ro.apiaapp.android.databinding.FragmentDetailsBinding
import edu.codespring.ro.apiaapp.android.utils.getErrorMessage
import edu.codespring.ro.apiaapp.constants.ApiErrors
import edu.codespring.ro.apiaapp.auth.AuthManager
import edu.codespring.ro.apiaapp.data.model.PackageData
import edu.codespring.ro.apiaapp.ui.packageDetail.PackageDetailPresenter
import edu.codespring.ro.apiaapp.ui.packageDetail.PackageDetailView

class PackageDetailFragment : Fragment(), PackageDetailView {
    companion object {
        private const val DETAIL_ID = "detailId"
        private const val TEXT_SIZE = 18f
    }

    private lateinit var presenter: PackageDetailPresenter

    private var binding: FragmentDetailsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        presenter = PackageDetailPresenter()
        presenter.attachView(this)

        binding = FragmentDetailsBinding.inflate(inflater, container, false)

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = arguments?.getInt(DETAIL_ID)
        if (args != null) {
            presenter.loadListElementById(args)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
        binding = null
    }

    override fun loadPackage(detailsData: PackageData) {
        if (AuthManager.isAuthenticated()) {
            presenter.checkPackageInPlannerAlready(detailsData.id)
            presenter.checkPackageInActive(detailsData.id)
            binding?.addButton?.visibility = View.VISIBLE
        } else {
            binding?.addButton?.visibility = View.GONE
        }

        binding?.collapsingToolbar?.title = detailsData.name
        binding?.collapsingToolbar?.setExpandedTitleColor(
            resources.getColor(
                R.color.white,
                requireActivity().theme
            )
        )
        binding?.collapsingToolbar?.setCollapsedTitleTextColor(
            resources.getColor(R.color.white, requireActivity().theme)
        )

        binding?.backButton?.setOnClickListener {
            findNavController().navigate(R.id.action_packageDetailFragment_to_packagesFragment)
        }

        binding?.category?.text = detailsData.category
        binding?.value?.text = "${detailsData.value} €"
        binding?.relevantYears?.text = detailsData.relevantYears
        binding?.range?.text = detailsData.range
        binding?.township?.text = detailsData.townships?.joinToString { "${it.name} ${it.code}" }
        when (detailsData.supportCategory) {
            SupportCategories.CULTIVATION.toString() -> binding?.supportCategory?.text =
                resources.getString(R.string.cultivationSupportCategory)
            SupportCategories.ANIMAL_HUSBANDRY.toString() -> binding?.supportCategory?.text =
                resources.getString(R.string.animalSupportCategory)
        }
        binding?.deadline?.text = detailsData.deadline

        generateViews(detailsData)
    }

    private fun generateViews(detailsData: PackageData) {
        val requirementsTitle = TextView(context)
        requirementsTitle.textSize = TEXT_SIZE
        @Suppress("MagicNumber")
        requirementsTitle.setPadding(40, 10, 20, 10)
        requirementsTitle.setTextColor(
            resources.getColor(
                R.color.colorDetailsTitle,
                requireActivity().theme
            )
        )
        requirementsTitle.text = getString(R.string.requirements)
        binding?.linearLayout?.addView(requirementsTitle)

        detailsData.requirements.forEach {
            val requirementsText = TextView(context)
            requirementsText.textSize = TEXT_SIZE
            @Suppress("MagicNumber")
            requirementsText.setPadding(60, 10, 20, 10)
            requirementsText.text = "➤$it"
            binding?.linearLayout?.addView(requirementsText)
        }

        val rulesTitle = TextView(context)
        rulesTitle.textSize = TEXT_SIZE
        @Suppress("MagicNumber")
        rulesTitle.setPadding(40, 10, 20, 10)
        rulesTitle.setTextColor(
            resources.getColor(
                R.color.colorDetailsTitle,
                requireActivity().theme
            )
        )
        if (detailsData.rules?.size != 0) {
            rulesTitle.text = getString(R.string.rules)
            binding?.linearLayout?.addView(rulesTitle)
        }

        detailsData.rules?.forEach {
            val rulesText = TextView(context)
            rulesText.textSize = TEXT_SIZE
            @Suppress("MagicNumber")
            rulesText.setPadding(60, 10, 20, 10)
            rulesText.text = "➤$it"
            binding?.linearLayout?.addView(rulesText)
        }

        generateRequirementViews(detailsData)

        binding?.cordLayout?.visibility = View.VISIBLE
        binding?.errorText?.visibility = View.GONE
        binding?.progress?.visibility = View.GONE
    }

    private fun generateRequirementViews(detailsData: PackageData) {
        val requiredDocumentsTitle = TextView(context)
        requiredDocumentsTitle.textSize = TEXT_SIZE
        @Suppress("MagicNumber")
        requiredDocumentsTitle.setPadding(40, 10, 20, 10)
        requiredDocumentsTitle.setTextColor(
            resources.getColor(
                R.color.colorDetailsTitle,
                requireActivity().theme
            )
        )
        requiredDocumentsTitle.text = getString(R.string.requiredDocuments)
        binding?.linearLayout?.addView(requiredDocumentsTitle)

        detailsData.requiredDocuments.forEach {
            val requiredDocumentsText = TextView(context)
            requiredDocumentsText.textSize = TEXT_SIZE
            @Suppress("MagicNumber")
            requiredDocumentsText.setPadding(60, 10, 20, 10)
            requiredDocumentsText.text = "➤${it.name}"
            binding?.linearLayout?.addView(requiredDocumentsText)
        }

        generateConditionsView(detailsData)

        val detailsTitle = TextView(context)
        detailsTitle.textSize = TEXT_SIZE
        @Suppress("MagicNumber")
        detailsTitle.setPadding(40, 10, 20, 10)
        detailsTitle.setTextColor(
            resources.getColor(
                R.color.colorDetailsTitle,
                requireActivity().theme
            )
        )
        detailsTitle.text = getString(R.string.details)
        binding?.linearLayout?.addView(detailsTitle)

        val details = TextView(context)
        details.textSize = TEXT_SIZE
        @Suppress("MagicNumber")
        details.setPadding(60, 10, 20, 10)
        details.text = detailsData.description
        binding?.linearLayout?.addView(details)
    }

    private fun generateConditionsView(detailsData: PackageData) {
        val preconditionsTitle = TextView(context)
        preconditionsTitle.textSize = TEXT_SIZE
        @Suppress("MagicNumber")
        preconditionsTitle.setPadding(40, 10, 20, 10)
        preconditionsTitle.setTextColor(
            resources.getColor(
                R.color.colorDetailsTitle,
                requireActivity().theme
            )
        )
        preconditionsTitle.text = getString(R.string.preconditions)
        binding?.linearLayout?.addView(preconditionsTitle)

        detailsData.preconditions.forEach {
            val preconditionsText = TextView(context)
            preconditionsText.textSize = TEXT_SIZE
            @Suppress("MagicNumber")
            preconditionsText.setPadding(60, 10, 20, 10)
            preconditionsText.text = "➤${it.name}"
            binding?.linearLayout?.addView(preconditionsText)
        }

        val postProductionTitle = TextView(context)
        postProductionTitle.textSize = TEXT_SIZE
        @Suppress("MagicNumber")
        postProductionTitle.setPadding(40, 10, 20, 10)
        postProductionTitle.setTextColor(
            resources.getColor(
                R.color.colorDetailsTitle,
                requireActivity().theme
            )
        )
        postProductionTitle.text = getString(R.string.postProduction)
        binding?.linearLayout?.addView(postProductionTitle)

        val postProduction = TextView(context)
        postProduction.textSize = TEXT_SIZE
        @Suppress("MagicNumber")
        postProduction.setPadding(60, 10, 20, 10)
        postProduction.text = detailsData.postProduction
        binding?.linearLayout?.addView(postProduction)
    }

    override fun setUpLoadingScreen() {
        binding?.cordLayout?.visibility = View.GONE
        binding?.addButton?.visibility = View.GONE
        binding?.errorText?.visibility = View.GONE
        binding?.progress?.visibility = View.VISIBLE
    }

    override fun setUpErrorScreen(error: ApiErrors) {
        binding?.cordLayout?.visibility = View.GONE
        binding?.progress?.visibility = View.GONE
        binding?.addButton?.visibility = View.GONE

        binding?.errorText?.text = getErrorMessage(requireContext(), error)
        binding?.errorText?.visibility = View.VISIBLE
    }

    override fun successToastMessage() {
        val message = getString(R.string.added_to_planner)

        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun errorToastMessage(error: ApiErrors) {
        val toastMessage = getErrorMessage(requireContext(), error)

        Toast.makeText(
            requireContext(),
            toastMessage,
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun buttonAddToPlannerEnabled(id : Long) {
        binding?.addButton?.text = getString(R.string.add_to_planner)
        binding?.addButton?.setOnClickListener {
            presenter.addPackageToPlanner(id)
        }
    }

    override fun buttonNavigateToPlannerEnabled() {
        binding?.addButton?.text = getString(R.string.navigate_to_planner)
        binding?.addButton?.setOnClickListener {
            findNavController().navigate(R.id.plannedPackagesFragment)
        }
    }

    override fun buttonNavigateToActiveEnabled() {
        binding?.addButton?.text = getString(R.string.navigate_to_active)
        binding?.addButton?.setOnClickListener {
            findNavController().navigate(R.id.activePackagesFragment)
        }
    }
}
