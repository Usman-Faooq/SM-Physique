package com.buzzwaretech.gymphysique.activities

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager2.widget.ViewPager2
import com.buzzwaretech.gymphysique.R
import com.buzzwaretech.gymphysique.adapters.MainPagerAdapter
import com.buzzwaretech.gymphysique.classes.Constants
import com.buzzwaretech.gymphysique.databinding.ActivityDashBoardBinding
import com.buzzwaretech.gymphysique.fragments.HomeFragment
import com.buzzwaretech.gymphysique.fragments.KartFragment
import com.buzzwaretech.gymphysique.fragments.ProfileFragment
import com.buzzwaretech.gymphysique.fragments.SearchFragment
import com.buzzwaretech.gymphysique.stripe.CustomerResponse
import com.buzzwaretech.gymphysique.stripe.EmpeheralKeyProvider
import com.buzzwaretech.gymphysique.stripe.StripeController
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response

class DashBoardActivity : AppCompatActivity() {

    lateinit var binding : ActivityDashBoardBinding

    private val PERMISSION_REQUEST_CODE = 100
    private val PERMISSIONS = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_MEDIA_IMAGES,
        android.Manifest.permission.READ_MEDIA_VIDEO,
        android.Manifest.permission.POST_NOTIFICATIONS,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val adapter = MainPagerAdapter(supportFragmentManager, lifecycle)
        binding.viewPager.adapter = adapter

        setListener()
        requestPermissionsIfNotGranted()

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                // This method will be called when a new page is selected
                onPageChanged(position)
            }
        })

    }

    private fun onPageChanged(position: Int) {
        // Do something with the position, such as updating UI elements or triggering actions
        // For example:
        when (position) {
            0 -> loadHome()
            1 -> loadKart()
            2 -> loadSearch()
            3 -> loadProfile()
        }
    }

    private fun requestPermissionsIfNotGranted() {
        val permissionsToRequest = PERMISSIONS.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), PERMISSION_REQUEST_CODE)
        }
    }


    private fun setListener() {

        binding.homeTab.setOnClickListener {
            loadHome()
            binding.viewPager.setCurrentItem(0, true)
        }

        binding.kartTab.setOnClickListener {
            loadKart()
            binding.viewPager.setCurrentItem(1, true)
        }

        binding.searchTab.setOnClickListener {
            loadSearch()
            binding.viewPager.setCurrentItem(2, true)
        }

        binding.profileTab.setOnClickListener {
            loadProfile()
            binding.viewPager.setCurrentItem(3, true)
        }

    }

/*    private fun loadFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment)

        //transaction.addToBackStack(null);
        transaction.commit()
    }*/

    private fun loadHome(){
        binding.homeTab.setImageResource(R.drawable.home_selected)
        binding.kartTab.setImageResource(R.drawable.icon_kart)
        binding.searchTab.setImageResource(R.drawable.icon_search)
        binding.profileTab.setImageResource(R.drawable.icon_profile)
    }

    private fun loadKart(){
        binding.homeTab.setImageResource(R.drawable.icon_home)
        binding.kartTab.setImageResource(R.drawable.kart_selected)
        binding.searchTab.setImageResource(R.drawable.icon_search)
        binding.profileTab.setImageResource(R.drawable.icon_profile)
    }

    private fun loadSearch(){
        binding.homeTab.setImageResource(R.drawable.icon_home)
        binding.kartTab.setImageResource(R.drawable.icon_kart)
        binding.searchTab.setImageResource(R.drawable.search_selected)
        binding.profileTab.setImageResource(R.drawable.icon_profile)
    }

    private fun loadProfile(){
        binding.homeTab.setImageResource(R.drawable.icon_home)
        binding.kartTab.setImageResource(R.drawable.icon_kart)
        binding.searchTab.setImageResource(R.drawable.icon_search)
        binding.profileTab.setImageResource(R.drawable.profile_selected)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            // Check if all permissions are granted
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // All permissions granted, proceed with your work
            } else {
                // Permissions denied, handle the situation
                // You may display a message or close the application
                /*Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show()
                // Check which permissions were denied
                for ((index, result) in grantResults.withIndex()) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        val deniedPermission = permissions[index]
                        Log.d("LOGGER_Denied Permission", "Permission $deniedPermission was denied")
                    }
                }*/
            }
        }
    }

}