package com.example.weather

import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.weather.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//api key --- 2f6872850d9f4841d0f5bbf348cec2ef

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this,R.layout.activity_main)

        fetchweatherdata("Delhi")

        searchdata()

    }

    private fun searchdata() {
        var searchview=binding.searchView
        searchview.setOnQueryTextListener(object:SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchweatherdata(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    fun fetchweatherdata(cityname:String) {
        var retrofit=Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ApiInterface::class.java)

        var response=retrofit.getweatherdata(cityname,"22fa768028c252bdf6ce30060e977a88","metric")

        response.enqueue(object :Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                var responsebody=response.body()
                if(response.isSuccessful && responsebody!=null){
                    var condition=responsebody.weather.firstOrNull()?.main?:"unknown"
                    binding.tempreture.text=" ${ responsebody.main.temp } °C"
                    binding.maxtemp.text= "${ responsebody.main.temp_max }°C"
                    binding.mintemp.text= "${ responsebody.main.temp_min }°C"
                    binding.weather.text=responsebody.weather.firstOrNull()?.main?:"unknown"
                    binding.humidity.text=responsebody.main.humidity.toString()
                    binding.windspeed.text=responsebody.wind.speed.toString()
                    binding.sunrise.text=responsebody.sys.sunrise.toString()
                    binding.sunset.text=responsebody.sys.sunset.toString()
                    binding.sea.text="${responsebody.main.pressure} hpa"
                    binding.day.text=currday(System.currentTimeMillis())
                    binding.date.text=currdate()
                    binding.cityname.text=cityname

                    changeanimationandbackground(condition)

                }
            }
            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {

            }

        })


    }

    private fun changeanimationandbackground(condition:String) {
        when(condition){
            "Clear Sky","Sunny","Clear"->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottie.setAnimation(R.raw.sun)
            }
            "Partly Clouds","Clouds","Overcast","Mist","Foggy"->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottie.setAnimation(R.raw.cloud)
            }
            "Light Rain","Heavy Rain"->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottie.setAnimation(R.raw.rain)
            }
            "Light snow","Heavy snow","Moderate snow"->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottie.setAnimation(R.raw.snow)
            }
            else->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottie.setAnimation(R.raw.sun)
            }

        }
    }

    fun currdate(): String {
         val day=SimpleDateFormat("dd MMMM yyyy",Locale.getDefault())
         return day.format((Date()))
    }

    fun currday(timestamp:Long):String{
        val day=SimpleDateFormat("EEEE",Locale.getDefault())
        return day.format((Date()))
    }
}

/*
{
    "coord": {
    "lon": 80.35,
    "lat": 26.4667
},
    "weather": [
    {
        "id": 803,
        "main": "Clouds",
        "description": "broken clouds",
        "icon": "04d"
    }
    ],
    "base": "stations",
    "main": {
    "temp": 307.08,
    "feels_like": 304.69,
    "temp_min": 307.08,
    "temp_max": 307.08,
    "pressure": 1005,
    "humidity": 15,
    "sea_level": 1005,
    "grnd_level": 990
},
    "visibility": 10000,
    "wind": {
    "speed": 4.42,
    "deg": 308,
    "gust": 6.79
},
    "clouds": {
    "all": 64
},
    "dt": 1711106021,
    "sys": {
    "country": "IN",
    "sunrise": 1711068016,
    "sunset": 1711111832
},
    "timezone": 19800,
    "id": 1267995,
    "name": "Kanpur",
    "cod": 200
}
*/