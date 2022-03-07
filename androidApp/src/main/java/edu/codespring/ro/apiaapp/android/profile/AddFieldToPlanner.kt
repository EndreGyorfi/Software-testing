package edu.codespring.ro.apiaapp.android.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.codespring.ro.apiaapp.android.databinding.FragmentAddFieldToPlannerBinding
import edu.codespring.ro.apiaapp.android.utils.getErrorMessage
import edu.codespring.ro.apiaapp.constants.ApiErrors
import edu.codespring.ro.apiaapp.data.model.Field
import edu.codespring.ro.apiaapp.ui.addField.AddFieldPresenter
import edu.codespring.ro.apiaapp.ui.addField.AddFieldView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddFieldToPlanner : Fragment(), AddFieldView {

    private var binding: FragmentAddFieldToPlannerBinding? = null
    private lateinit var presenter : AddFieldPresenter
    private var packageId : Int? = null

    private var adapter : AddFieldToPlannerAdapter = AddFieldToPlannerAdapter { fieldId: Int ->
        packageId?.let { presenter.addField(fieldId, it) }
    }

    companion object {
        private const val PACKAGE_ID = "packageId"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        presenter = AddFieldPresenter()
        presenter.attachView(this)

        binding = FragmentAddFieldToPlannerBinding.inflate(inflater, container, false)
        return binding!!.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.recyclerView?.adapter = adapter
        binding?.recyclerView?.layoutManager = LinearLayoutManager(context)

        packageId = arguments?.getInt(PACKAGE_ID)
        packageId?.let { presenter.loadFieldsList(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
        binding = null
    }

    override fun setFieldsList(fieldList: List<Field>) {
        binding?.progress?.visibility = View.GONE
        binding?.recyclerView?.visibility = View.VISIBLE

        adapter.submitList(fieldList)
    }


    override fun setUpLoadingScreen() {
        binding?.recyclerView?.visibility = View.GONE
        binding?.progress?.visibility = View.VISIBLE
    }

    override fun setUpErrorScreen(error: ApiErrors) {
        binding?.recyclerView?.visibility = View.GONE
        binding?.progress?.visibility = View.GONE
        binding?.errorText?.visibility = View.VISIBLE
        binding?.errorText?.text = getErrorMessage(requireContext(), error)
    }
}
