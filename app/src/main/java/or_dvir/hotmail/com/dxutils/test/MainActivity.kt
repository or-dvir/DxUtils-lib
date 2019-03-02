package or_dvir.hotmail.com.dxutils.test

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import or_dvir.hotmail.com.dxutils.RetroCallback
import or_dvir.hotmail.com.dxutils.retroRequestAsync
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.http.GET

class MainActivity : AppCompatActivity()
{

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
//            showPermissionsRationaleDialog(R.string.balbla,
//                                           true,
//                                           R.string.ok,
//                                           { toast("positive") },
//                                           R.string.bye,
//                                           { toast("negative") })

//            showSimpleDialog(R.string.blabla, R.string.ok)

            retroRequestAsync("aaaaa",
                              CoroutineScope(Dispatchers.Main),
                              SMyRetrofit.githubService.sampleRequest(),
                              RetroCallback<Sample>().apply {
                                  onSuccess = { _, _, _ ->
                                      toast("success")
                                  }

                                  onErrorOrExceptionOrNullBody = { _, _, _, _ ->
                                      toast("failure")
                                  }
                              }
            )
        }
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class Sample(@JsonProperty("login")
             val _login: String)

interface IGithubService
{
    @GET("users/list")
    fun sampleRequest(): Call<Sample>
}