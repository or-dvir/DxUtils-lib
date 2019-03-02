package or_dvir.hotmail.com.dxutils.test

import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

object SMyRetrofit
{
    val githubService: IGithubService

    init
    {
        val retrofit = Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(JacksonConverterFactory.create())
                .build()

        githubService = retrofit.create(IGithubService::class.java)
    }
}