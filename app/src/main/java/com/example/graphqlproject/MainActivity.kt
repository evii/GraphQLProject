package com.example.graphqlproject

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.response.CustomTypeAdapter
import com.apollographql.apollo.response.CustomTypeValue
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient

class MainActivity : AppCompatActivity() {

    val AUTH_TOKEN = "GITHUB TOKEN MUST BE INSERTED HERE"
    val BASE_URL = "https://api.github.com/graphql"
    val TAG = "GraphQlQuery"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    fun makeQuery(view: View) {
        val client=setupApollo()

        client.query(FindQuery    //From the auto generated class
            .builder()
            .name(repository_name_et.text.toString()) //Passing required arguments
            .owner(owner_name_et.text.toString()) //Passing required arguments
            .build())
            .enqueue(object : ApolloCall.Callback<FindQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    Log.v("$TAG onFailure", e.message.toString() + " " + e.cause.toString())
                }
                override fun onResponse(response: Response<FindQuery.Data>) {
                    Log.v("$TAG onResponse", " " + response.data()?.repository())
                    runOnUiThread {
                        repository_name_tv.text = response.data()?.repository()?.name()
                        desription_tv.text = response.data()?.repository()?.description()
                        forks_tv.text = response.data()?.repository()?.forkCount().toString()
                        url_tv.text = response.data()?.repository()?.url().toString()
                    }
                }
            })
    }

    private fun setupApollo(): ApolloClient {
        val okHttp = OkHttpClient
            .Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val builder = original.newBuilder().method(
                    original.method,
                    original.body
                )
                builder.addHeader(
                    "Authorization"
                    , "Bearer $AUTH_TOKEN"
                )
                chain.proceed(builder.build())
            }
            .build()
        return ApolloClient.builder()
            .serverUrl(BASE_URL)
            .okHttpClient(okHttp)
            .build()
    }
}
