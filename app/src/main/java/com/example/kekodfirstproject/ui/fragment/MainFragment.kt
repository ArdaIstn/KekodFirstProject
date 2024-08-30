package com.example.kekodfirstproject.ui.fragment

import android.os.Bundle

import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import com.example.kekodfirstproject.MainActivity
import com.example.kekodfirstproject.R
import com.example.kekodfirstproject.databinding.FragmentMainBinding
import com.example.kekodfirstproject.ui.viewmodel.MainViewModel

import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar


class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var bottomNav: BottomNavigationView
    private val viewModel: MainViewModel by activityViewModels()
    private val maxItems = 5

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeSwitchStates()
        setupSwitchListeners()
    }

    private fun setupUI() {
        bottomNav = (requireActivity() as MainActivity).findViewById(R.id.bottomNavigation)
        bottomNav.setOnItemSelectedListener { item ->
            navigateToFragment(item.itemId)
            true
        }
    }

    private fun observeSwitchStates() {
        viewModel.switchStates.observe(viewLifecycleOwner) { switchStates ->
            switchStates.forEach { (id, isChecked) ->
                getSwitchById(id)?.apply {
                    setOnCheckedChangeListener(null)
                    this.isChecked = isChecked
                    setOnCheckedChangeListener { _, checked ->
                        handleSwitchChange(this, checked)
                        viewModel.updateSwitchState(id, checked)
                    }
                    handleEgoSwitchState(id, isChecked)
                }
            }
        }
    }

    private fun setupSwitchListeners() {
        getSwitches().forEach { switch ->
            switch.setOnCheckedChangeListener { _, isChecked ->
                handleSwitchChange(switch, isChecked)
                viewModel.updateSwitchState(switch.id, isChecked)
            }
        }
    }

    private fun handleSwitchChange(switch: SwitchCompat, isChecked: Boolean) {
        if (switch.id == R.id.swEgo) return

        val navFragmentId = getNavFragmentId(switch)

        if (isChecked) {
            if (bottomNav.menu.size() < maxItems) {
                addMenuItem(switch.text.toString(), getIconForSwitch(switch), navFragmentId)
            } else {
                Snackbar.make(binding.root, "Maksimum menü öğesi sınırına ulaşıldı.", Snackbar.LENGTH_SHORT).show()
                switch.isChecked = false
            }
        } else {
            removeMenuItem(navFragmentId)
        }
    }

    private fun handleEgoSwitchState(id: Int, isChecked: Boolean) {
        if (id == R.id.swEgo) {
            setOtherSwitchesEnabled(!isChecked)
            bottomNav.visibility = if (isChecked) View.GONE else View.VISIBLE
            binding.ivMain.setImageResource(if (isChecked) R.drawable.ego_image else R.drawable.well_done_image)
        }
    }

    private fun navigateToFragment(fragmentId: Int) {
        val navController = (requireActivity().supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment).navController
        if (navController.currentDestination?.id != fragmentId) {
            navController.navigate(fragmentId)
        }
    }

    private fun getSwitchById(id: Int): SwitchCompat? {
        return when (id) {
            R.id.swHappiness -> binding.swHappiness
            R.id.swOptimism -> binding.swOptimism
            R.id.swKindness -> binding.swKindness
            R.id.swGiving -> binding.swGiving
            R.id.swRespect -> binding.swRespect
            R.id.swEgo -> binding.swEgo
            else -> null
        }
    }

    private fun getSwitches(): List<SwitchCompat> {
        return listOf(
            binding.swHappiness,
            binding.swOptimism,
            binding.swKindness,
            binding.swGiving,
            binding.swRespect,
            binding.swEgo
        )
    }

    private fun getNavFragmentId(switch: SwitchCompat): Int {
        return when (switch.id) {
            R.id.swHappiness -> R.id.happinessFragment
            R.id.swOptimism -> R.id.optimismFragment
            R.id.swKindness -> R.id.kindnessFragment
            R.id.swGiving -> R.id.givingFragment
            R.id.swRespect -> R.id.respectFragment
            else -> R.id.mainFragment
        }
    }

    private fun getIconForSwitch(switch: SwitchCompat): Int {
        return when (switch.id) {
            R.id.swGiving -> R.drawable.giving_image
            R.id.swHappiness -> R.drawable.happiness_image
            R.id.swKindness -> R.drawable.kindness_image
            R.id.swOptimism -> R.drawable.hope_image
            R.id.swRespect -> R.drawable.respect_image
            else -> R.drawable.baseline_home_24
        }
    }

    private fun addMenuItem(title: String, iconResId: Int, navFragmentId: Int) {
        if (bottomNav.menu.findItem(navFragmentId) == null) {
            bottomNav.menu.add(Menu.NONE, navFragmentId, Menu.NONE, title).setIcon(iconResId)
        }
    }

    private fun removeMenuItem(navFragmentId: Int) {
        bottomNav.menu.removeItem(navFragmentId)
    }

    private fun setOtherSwitchesEnabled(isEnabled: Boolean) {
        getSwitches().filterNot { it.id == R.id.swEgo }.forEach { it.isEnabled = isEnabled }
    }
}