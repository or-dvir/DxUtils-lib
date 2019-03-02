package or_dvir.hotmail.com.dxutils.test

import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

object SMyRetrofit
{
    private const val TIMEOUT_SECONDS: Long = 15
    val githubService: IGithubService

    init
    {
        val retrofit = Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(JacksonConverterFactory.create())
//                .client(OkHttpClient.Builder()
//                                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
//                                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
//                                .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
//                                .build())
                .build()

        githubService = retrofit.create(IGithubService::class.java)
    }
}