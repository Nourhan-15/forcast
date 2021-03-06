 package com.example.forcast247


import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.forcast247.ApiResponse.Hourly
import com.example.forcast247.DataBase1.Resource
import com.example.forcast247.databinding.FragmentHomeWeatherBinding
import com.example.forcast247_app.weather.receiver.AlReciever
import com.example.forcast247_app.weather.utils.dayConverter
import com.example.forcast247_app.weather.utils.setImage
import com.example.forcast247_app.weather.utils.timeConverter
import com.example.forcast247_app.weather.view.fragments.home.HourlyAdapter
import com.example.forcast247_app.weather.viewModel.SettingViewModel
import com.example.forcast247_app.weather.viewModel.WeatherViewModel
import java.util.*


 class HomeWeather : Fragment(R.layout.fragment_home_weather) {
    lateinit var binding: FragmentHomeWeatherBinding
    private var hourAdapter: RecyclerView.Adapter<HourlyAdapter.ForecatViewHolder>? = null
    private var layoutManag: RecyclerView.LayoutManager? = null
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var settingViewModel: SettingViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    lateinit var sp: SharedPreferences
    var values: String = ""
    lateinit var calendar : Calendar
    lateinit var alarmManager:AlarmManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        calendar = Calendar.getInstance()

        weatherViewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
        settingViewModel = ViewModelProvider(this).get(SettingViewModel::class.java)

        binding = FragmentHomeWeatherBinding.inflate(layoutInflater)
        val root = binding.root
        binding.recyclerViewCurrent.isEnabled = false
        context?.let {
            weatherViewModel.getWeatherAPIData(it)
        }

        weatherViewModel.weatherLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    binding.mainContainer.visibility = View.VISIBLE
                    hideProgressBar()
                    it.data?.let {
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
                is Resource.Error -> {
                    showErrorMessage(it.message)
                }
            }
        })

        weatherViewModel.checkRoom.observe(viewLifecycleOwner, Observer {
            if (it == true){
                binding.cardHello.visibility = View.VISIBLE
            }
        })

        weatherViewModel.weatherFromRoomLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    binding.mainContainer.visibility = View.VISIBLE

                    if(it.data!!.alerts!=null){
                        it.data!!.alerts?.let { it1 ->
                            for(item in it1)
                            values = values+item.event+ " from "+weatherViewModel
                                .getDateTime(item.start.toString(),"dd-MM-yyyy  hh:mm a")+" to "+
                                    weatherViewModel.getDateTime(item.end.toString(),"dd-MM-yyyy  hh:mm a")+"\n"
                            setAlertNotification(calendar.timeInMillis, values, it1.get(0).description!!)
                        }
                    }
//                    else{
//
//                        setAlertNotification(calendar.timeInMillis, values, "no")
//
//
//                    }
                    it.data?.let {
                        displayDailyWeatherToRecycleView(it.hourly)
                        displayCurrentWeatherToCard(it)
                    }
                }
                is Resource.Loading -> {
                }
                is Resource.Error -> {
                    showErrorMessage(it.message)
                }
            }
        })

        sharedPreferences = context?.let { PreferenceManager.getDefaultSharedPreferences(it) }!!
        editor = sharedPreferences.edit()
        PreferenceManager.getDefaultSharedPreferences(requireContext()).also { sp = it }
        binding.tvAddress.text = sp.getString("address", "")
        binding.textCelcius.text = sp.getString("cel", "")
        binding.textWind.text= sp.getString("km","")


        return root
    }

    private fun displayCurrentWeatherToCard(it1: WeatherResponse) {

        binding.tvTemp.text = it1.daily.get(0).temp.day.toString()
        binding.tvTempMax.text = it1.daily.get(0).temp.max.toString()
        binding.tvTempMin.text = it1.daily.get(0).temp.min.toString()
        binding.tvHumidity.text = Math.round(it1.current.humidity).toString()
        binding.tvUpdatedAt.text = dayConverter(it1.current.dt.toLong())
        binding.tvStatus.text = it1.current.weather.get(0).description
        binding.tvWind.text = Math.round(it1.current.windSpeed).toString()
        binding.tvPressure.text = Math.round(it1.current.pressure).toString()
        binding.tvSunrise.text = timeConverter(it1.current.sunrise.toLong())
        binding.tvSunset.text = timeConverter(it1.current.sunset.toLong())
        binding.tvVis.text = it1.current.visibility.toString()//mm
        val url = it1.current.weather.get(0).icon

        setImage(binding.imgCurrentItem, url)

    }


    @SuppressLint("WrongConstant")
    private fun initUI(data: List<Hourly>) {
        context?.let {
            var hourlyAd = HourlyAdapter(data)
            hourlyAd.setData(data, it)
            binding.recyclerViewCurrent.apply {
                layoutManag = LinearLayoutManager(context, OrientationHelper.HORIZONTAL, false)
                layoutManager = layoutManag
                hourAdapter = hourlyAd
                adapter = hourAdapter
            }
        }

    }

    fun displayDailyWeatherToRecycleView(data: List<Hourly>) {
        if (data != null) {
            initUI(data)
        }
    }

    private fun showErrorMessage(message: String?) {
        System.out.println("Error is: " + message)
        binding.progressBar.visibility = View.INVISIBLE
    }


    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.INVISIBLE
    }




    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun setAlertNotification(st: Long, event: String, description: String, ) {
        val intentAlertReciever = Intent(context, AlReciever::class.java)
        intentAlertReciever.putExtra("event", event)
        intentAlertReciever.putExtra("desc", description)
        val random = Random()
        val requestCode = random.nextInt(99)
        val pendingIntentAlertReciever =
            PendingIntent.getBroadcast(context, requestCode, intentAlertReciever, 0)
        val calendar = Calendar.getInstance().timeInMillis
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar, pendingIntentAlertReciever)
            requireActivity().registerReceiver(AlReciever(), IntentFilter())

    }


}

