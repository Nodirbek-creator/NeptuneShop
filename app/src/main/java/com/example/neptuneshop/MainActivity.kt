package com.example.neptuneshop

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.neptuneshop.network.ApiService
import com.example.neptuneshop.network.RetrofitBuilder
import com.example.neptuneshop.screens.LoginScreen
import com.example.neptuneshop.screens.HomeScreen
import com.example.neptuneshop.screens.ProductInfo
import com.example.neptuneshop.screens.ProfileScreen
import com.example.neptuneshop.screens.Routes
import com.example.neptuneshop.screens.SearchScreen
import com.example.neptuneshop.screens.SplashScreen
import com.example.neptuneshop.screens.sign_in.GoogleAuthUiClient
import com.example.neptuneshop.screens.sign_in.SignInViewModel
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val apiService = RetrofitBuilder.getInstance().create(ApiService::class.java)
    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = "splash",
            ) {
                composable(Routes.LoginScreen.route) {
                    val viewModel = viewModel<SignInViewModel>()
                    val state by viewModel.state.collectAsStateWithLifecycle()

                    //WHAT A FUCK IS THIS????
                    //ah now I get it :) this is just dialog launcher which shows up at the bottom of the screen

                    val launcher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.StartIntentSenderForResult(),
                        onResult = { result ->
                            if(result.resultCode == RESULT_OK) {
                                lifecycleScope.launch {
                                    val signInResult = googleAuthUiClient.signInWithIntent(
                                        intent = result.data ?: return@launch
                                    )
                                    viewModel.onSignInResult(signInResult)
                                }
                            }
                        }
                    )

                    LaunchedEffect(key1 = state.isSignInSuccessful) {
                        if(state.isSignInSuccessful) {

                            val sharedPref = applicationContext.getSharedPreferences("user_profile", Context.MODE_PRIVATE)
                            val user = googleAuthUiClient.getSignedInUser()
                            Toast.makeText(
                                applicationContext,
                                "SignIn successful id:${user?.userId}",
                                Toast.LENGTH_SHORT
                            ).show()
                            with(sharedPref.edit()) {
                                putString("userId", user?.userId)
                                putString("name", user?.username)
                                putString("email", user?.email)
                                putString("mobile", user?.phoneNumber)
                                putString("profilePic", user?.profilePic)
                                apply()
                            }


                            navController.navigate(Routes.HomeScreen.route)
                            viewModel.resetState()
                        }
                    }

                    LoginScreen(
                        state = state,
                        navController = navController,
                        apiService = apiService,
                        onGoogleClick = {
                            lifecycleScope.launch {
                                val signInIntentSender = googleAuthUiClient.signIn()
                                launcher.launch(
                                    IntentSenderRequest.Builder(
                                        signInIntentSender ?: return@launch
                                    ).build()
                                )
                            }
                        }
                    )
                }

                composable(Routes.HomeScreen.route){
                    HomeScreen(
                        navController = navController,
                        apiService = apiService,
                        onSignOut = {
                            lifecycleScope.launch {
                                googleAuthUiClient.signOut()
                                Toast.makeText(
                                    applicationContext,
                                    "Signed Out successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    )
                }
                composable(Routes.ProfileScreen.route){
                    ProfileScreen(
                        navController = navController,
                        onSignOut = {
                            lifecycleScope.launch {
                                googleAuthUiClient.signOut()
                                Toast.makeText(
                                    applicationContext,
                                    "Signed Out successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                }
                composable(Routes.SplashScreen.route){
                    SplashScreen(
                        navController = navController,
                        googleAuthUiClient = googleAuthUiClient
                    )
                }
                composable(Routes.SearchScreen.route){
                    SearchScreen(
                        navController = navController,
                        apiService = apiService)
                }
                composable("${Routes.ProductInfo.route}/{id}"){ stackEntry ->
                    val id = stackEntry.arguments?.getString("id")
                    ProductInfo(
                        id = id!!.toInt(),
                        navController = navController,
                        apiService = apiService
                    )
                }


            }
        }
    }
}

//  Retrofit use cases
/*todo get all products*/
//        apiService.getProducts().enqueue(object : Callback<Products> {
//            override fun onResponse(
//                call: Call<Products?>,
//                response: Response<Products?>
//            ) {
//                if (response.isSuccessful) {
//                    products = (response.body()!!.products)
//                }
//            }
//
//            override fun onFailure(
//                call: Call<Products?>,
//                t: Throwable
//            ) {
//                Log.d("TAG", "onFailure: ${t.message}")
//            }
//
//        })

/*todo get product by id*/
//        val id = 6
//        apiService.getProductById(id).enqueue(object : Callback<Product> {
//            override fun onResponse(
//                call: Call<Product?>,
//                response: Response<Product?>
//            ) {
//                if (response.isSuccessful) {
//                    Log.d("TAG", "onResponse: ${response.body()}")
//                }
//
//            }
//
//            override fun onFailure(
//                call: Call<Product?>,
//                t: Throwable
//            ) {
//                Log.d("TAG", "onFailure: ${t.message}")
//            }
//        })
//     /*todo search a product*/
//        apiService.searchProducts("car").enqueue(object : Callback<Products> {
//            override fun onResponse(
//                call: Call<Products?>,
//                response: Response<Products?>
//            ) {
//                Log.d("TAG", "onResponse: ${response.body()?.products}")
//            }
//
//            override fun onFailure(
//                call: Call<Products?>,
//                t: Throwable
//            ) {
//                Log.d("TAG", "onFailure: ${t.message}")
//            }
//
//        })
//

