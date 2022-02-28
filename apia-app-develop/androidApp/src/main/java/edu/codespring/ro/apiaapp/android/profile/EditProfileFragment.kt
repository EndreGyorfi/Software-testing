package edu.codespring.ro.apiaapp.android.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.round
import com.skydoves.powerspinner.PowerSpinnerView
import com.squareup.picasso.Picasso
import edu.codespring.ro.apiaapp.android.R
import edu.codespring.ro.apiaapp.android.databinding.FragmentEditProfileBinding
import edu.codespring.ro.apiaapp.android.utils.getErrorMessage
import edu.codespring.ro.apiaapp.auth.AuthManager
import edu.codespring.ro.apiaapp.constants.ApiErrors
import edu.codespring.ro.apiaapp.data.model.Field
import edu.codespring.ro.apiaapp.data.model.UserData
import edu.codespring.ro.apiaapp.ui.editProfile.EditProfilePresenter
import edu.codespring.ro.apiaapp.ui.editProfile.EditProfileView

class EditProfileFragment : Fragment(), EditProfileView {

    private var binding: FragmentEditProfileBinding? = null
    private var recyclerView: RecyclerView? = null
    private lateinit var presenter: EditProfilePresenter

    private val adapter: FieldsAdapter =
        FieldsAdapter { fieldId: Int, action: ActionEnum, name: String, size: Float ->
            if (action == ActionEnum.DELETE) {
                deleteField(fieldId)
            } else if (action == ActionEnum.EDIT) {
                editField(fieldId, name, size)
            }
        }

    private fun editField(fieldId: Int, name: String, size: Float) {
        val inflater = LayoutInflater.from(this.requireContext())
        val view = inflater.inflate(R.layout.add_field, null)
        val fieldName = view.findViewById<EditText>(R.id.fieldName)
        val fieldSize = view.findViewById<EditText>(R.id.fieldSize)
        val addDialog = AlertDialog.Builder(this.requireContext())

        fieldName.setText(name)
        fieldSize.setText(size.toString())

        addDialog.setView(view)
        addDialog.setPositiveButton(getString(R.string.edit)) { dialog, _ ->
            if (fieldSize.text.toString().isNotEmpty()) {
                val spinner = view.findViewById<PowerSpinnerView>(R.id.spinnerView)
                presenter.updateField(fieldId,
                    fieldName.text.toString(),
                    convertUnits(fieldSize.text.toString().toFloat(), spinner.selectedIndex))
            } else {
                Toast.makeText(this.requireContext(),
                    getString(R.string.size_not_empty),
                    Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        addDialog.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
        }
        addDialog.create()
        addDialog.show()
    }

    private fun deleteField(fieldId: Int) {
        val builder = android.app.AlertDialog.Builder(this.activity, R.style.AlertDialogStyle)
        builder.setTitle(getString(R.string.confirm_delete))
        builder.setMessage(getString(R.string.want_to_delete))
        builder.setPositiveButton(getString(R.string.yes)) { dialog, _ ->
            presenter.deleteField(fieldId)
            dialog.cancel()
        }
        builder.setNegativeButton(getString(R.string.no)) { dialog, _ ->
            dialog.cancel()
        }
        val alert: android.app.AlertDialog = builder.create()
        alert.show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        presenter = EditProfilePresenter()
        presenter.attachView(this)

        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding!!.root
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.edit_profile_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.logOut) {
            val builder = android.app.AlertDialog.Builder(this.activity, R.style.AlertDialogStyle)
            builder.setTitle(getString(R.string.confirm))
            builder.setMessage(getString(R.string.confirm_log_out))
            builder.setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                presenter.logout()
                dialog.cancel()
            }
            builder.setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.cancel()
            }
            val alert: android.app.AlertDialog = builder.create()
            alert.show()
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.showFields()
        presenter.setUserData(AuthManager.getUserInfo())
        binding?.addFieldCardView?.setOnClickListener { addField() }
    }

    private fun addField() {
        val inflater = LayoutInflater.from(this.requireContext())
        val view = inflater.inflate(R.layout.add_field, null)
        val fieldName = view.findViewById<EditText>(R.id.fieldName)
        val fieldSize = view.findViewById<EditText>(R.id.fieldSize)
        val addDialog = AlertDialog.Builder(this.requireContext())

        addDialog.setView(view)
        addDialog.setPositiveButton(getString(R.string.done)) { dialog, _ ->
            if (fieldSize.text.toString().isNotEmpty()) {
                val name = fieldName.text.toString()
                val spinner = view.findViewById<PowerSpinnerView>(R.id.spinnerView)

                presenter.addField(name,
                    convertUnits(fieldSize.text.toString().toFloat(), spinner.selectedIndex))

                Toast.makeText(this.requireContext(),
                    getString(R.string.field_added),
                    Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                Toast.makeText(this.requireContext(),
                    getString(R.string.size_not_empty),
                    Toast.LENGTH_SHORT).show()
            }
        }
        addDialog.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
        }
        addDialog.create()
        addDialog.show()
    }

    @Suppress("MagicNumber")
    private fun convertUnits(size: Float, unit: Int): Float {
        return when (unit) {
            0 -> round((size / 100F) * 100.0F) / 100.0F
            1 -> round((size) * 100.0F) / 100.0F
            else -> round((size) * 100.0F) / 100.0F
        }
    }

    override fun setUserInfo(userInfo: UserData) {
        binding?.userName?.text = userInfo.name
        binding?.email?.text = userInfo.email
        Picasso.get().load(userInfo.pictureURL).into(binding?.profilePicture)
    }

    override fun loadFields(fields: List<Field>) {
        recyclerView = binding?.fieldRecyclerView
        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = LinearLayoutManager(context)

        adapter.submitList(fields)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun displayLogoutFailed() {
        Toast.makeText(
            requireContext(),
            getString(R.string.error),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun displayLogoutSuccess() {
        findNavController().navigate(R.id.action_editProfileFragment_to_guestProfileFragment)
        Toast.makeText(
            requireContext(),
            getString(R.string.logout_success),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun setUpErrorScreen(error: ApiErrors) {
        binding?.errorText?.visibility = View.VISIBLE
        binding?.imageView?.visibility = View.GONE
        binding?.cardView?.visibility = View.GONE
        binding?.userName?.visibility = View.GONE
        binding?.emailAddress?.visibility = View.GONE
        binding?.email?.visibility = View.GONE
        binding?.myFields?.visibility = View.GONE
        binding?.fieldRecyclerView?.visibility = View.GONE
        binding?.addFieldCardView?.visibility = View.GONE

        binding?.errorText?.text = getErrorMessage(requireContext(), error)
        binding?.errorText?.visibility = View.VISIBLE
    }

    override fun errorToastMessage(error: ApiErrors) {
        val toastMessage = getErrorMessage(requireContext(), error)

        Toast.makeText(
            requireContext(),
            toastMessage,
            Toast.LENGTH_SHORT
        ).show()
    }
}
