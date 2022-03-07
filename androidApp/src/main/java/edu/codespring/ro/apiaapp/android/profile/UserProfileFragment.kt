package edu.codespring.ro.apiaapp.android.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import edu.codespring.ro.apiaapp.android.MainActivity
import edu.codespring.ro.apiaapp.android.R
import edu.codespring.ro.apiaapp.android.databinding.FragmentUserProfileBinding

class UserProfileFragment : Fragment() {

    private var binding: FragmentUserProfileBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false)

        val adapter = ViewPagerAdapter((context as MainActivity).supportFragmentManager, lifecycle)
        binding?.viewPager?.adapter = adapter

        binding?.viewPager?.let { viewPager ->
            binding?.tabLayout?.let { tabLayout ->
                TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                    tab.text = when (position) {
                        0 -> resources.getString(R.string.active_packages)
                        1 -> resources.getString(R.string.planned_packages)
                        else -> resources.getString(R.string.active_packages)
                    }
                }.attach()
            }
        }
        binding?.editButton?.setOnClickListener {
            findNavController().navigate(R.id.action_userProfileFragment_to_editProfileFragment)
        }
        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
