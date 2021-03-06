package com.example.forcast247_app.weather.view.fragments.seven_days

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.forcast247.ApiResponse.Daily
import com.example.forcast247.DataBase1.Repository
import com.example.forcast247.DataBase1.Resource
import com.example.forcast247.R
import com.example.forcast247.databinding.FragmentSevenDayWeatherBinding
import com.example.forcast247_app.weather.viewModel.SettingViewModel
import com.example.forcast247_app.weather.viewModel.WeatherViewModel


class SevenDayWeather : Fragment(R.layout.fragment_seven_day_weather) {

    lateinit var binding: FragmentSevenDayWeatherBinding

    lateinit var sp: SharedPreferences
    private var adaptDaily: RecyclerView.Adapter<DailyAdapter.DailyViewHolder>? = null
    private var layoutManagDaily: RecyclerView.LayoutManager? = null
    private lateinit var weatherViewModel: WeatherViewModel
    lateinit var repo: Repository

    lateinit var model: SettingViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        model = ViewModelProvider(this).get(SettingViewModel::class.java)
        model.getAddressData().observe(viewLifecycleOwner, Observer {
            binding.tvCountry.text = it.toString()
        })
        repo = Repository()
        binding = FragmentSevenDayWeatherBinding.inflate(layoutInflater)
        val root = binding.root
        binding.recyclerViewDaily.isEnabled = false
        context?.let { weatherViewModel.getWeatherAPIData(it) }

        weatherViewModel.weatherFromRoomLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Loading -> {
                    showProgressBar()
                }
                is Resource.Success -> {
                    hideProgressBar()
                    it.data?.let { it1 -> displayDailyWeatherToRecycleView(it1.daily) }
                }
                is Resource.Error -> {
                    showErrorMessage(it.message)
                }
            }
        })

        sp = context?.let { PreferenceManager.getDefaultSharedPreferences(it) }!!
        binding.tvCountry.text = sp.getString("address", "")

        weatherViewModel.checkRoom.observe(viewLifecycleOwner, Observer {
            if (it == true){
                binding.cardSeven.visibility = View.VISIBLE
            }
        })

        return root
    }


    private fun initUI(data: List<Daily>) {
        var dailyAdapter = DailyAdapter(data)
        binding.recyclerViewDaily.apply {
            layoutManagDaily = LinearLayoutManager(context)
            dailyAdapter.setData(data, context)
            layoutManager = layoutManagDaily
            adaptDaily = dailyAdapter
            adapter = adaptDaily

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun displayDailyWeatherToRecycleView(data: List<Daily>) {
        initUI(data)
        adaptDaily!!.notifyDataSetChanged()
    }

    private fun showErrorMessage(message: String?) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show()
        System.out.println("Error is : $message")
        binding.progressBarDaily.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        binding.progressBarDaily.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBarDaily.visibility = View.INVISIBLE
    }


}

