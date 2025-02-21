package com.example.neptuneshop.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.neptuneshop.R
import com.example.neptuneshop.model.Product
import com.example.neptuneshop.model.Products
import com.example.neptuneshop.network.ApiService
import com.example.neptuneshop.ui.theme.myBlue
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun CategoriesScreen(
    navController: NavHostController,
    apiService: ApiService,
    onSignOut:() -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var selectedCategory by remember { mutableStateOf("all") }
    var isLoading by remember { mutableStateOf(true) }
    var allProducts by remember { mutableStateOf<List<Product>>(emptyList()) }

    LaunchedEffect(selectedCategory) {
        try {
            isLoading = true
            if(selectedCategory == "all"){
                apiService.getProducts().enqueue(object : Callback<Products> {
                    override fun onResponse(
                        call: Call<Products?>,
                        response: Response<Products?>
                    ) {
                        if (response.isSuccessful) {
                            allProducts = (response.body()!!.products)
                            isLoading = false
                        }
                    }

                    override fun onFailure(
                        call: Call<Products?>,
                        t: Throwable
                    ) {
                        Log.d("TAG", "onFailure: ${t.message}")
                    }

                })
            }
            else{
                apiService.getProductsByCategory(category = selectedCategory).enqueue(object : Callback<Products> {
                    override fun onResponse(
                        call: Call<Products?>,
                        response: Response<Products?>
                    ) {
                        if (response.isSuccessful) {
                            allProducts = (response.body()!!.products)
                            isLoading = false
                        }
                    }

                    override fun onFailure(
                        call: Call<Products?>,
                        t: Throwable
                    ) {
                        Log.d("TAG", "onFailure: ${t.message}")
                    }

                })
            }

        } catch (e: Exception) {
            Log.d("unexpected error","${e.message}")
        }
    }
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            val items = listOf(
                MenuOption("Shop by Categories", R.drawable.category, Routes.CategoriesScreen.route),
                MenuOption("My Orders", R.drawable.clock, Routes.OrdersScreen.route),
                MenuOption("Favorites", R.drawable.heart, Routes.FavoritesScreen.route),
                MenuOption("Profile", R.drawable.account, Routes.ProfileScreen.route),
                MenuOption("Contributors", R.drawable.conversation, Routes.Contributors.route),
                MenuOption("Sign Out", R.drawable.exit, Routes.LoginScreen.route)

            )
            ModalDrawerSheet(drawerContainerColor = Color.White) {
                ProfileSection(navController)
                items.forEach { item ->
                    NavigationDrawerItem(
                        label = {
                            Text(item.title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        },
                        icon = {
                            Image(
                                painter = painterResource(id = item.icon),
                                contentDescription = item.title,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        selected = item.route == currentRoute,
                        onClick = {
                            navController.navigate(item.route)
                            scope.launch { drawerState.close() }
                            when (item.title) {
                                "Sign Out" -> {
                                    scope.launch {
                                        val sharedPreferences = context.getSharedPreferences("user_profile", Context.MODE_PRIVATE)
                                        with(sharedPreferences.edit()){
                                            putString("name", "")
                                            putString("email", "")
                                            putString("mobile", "")
                                            putString("location", "")
                                            putString("gender", "")
                                            putString("profilePic", "")
                                            apply()
                                        }
                                        onSignOut()
                                    }
                                }
                            }
                        }
                    )
                    if(item.title == "Saved Cards"){
                        HorizontalDivider(
                            thickness = 2.dp,
                            color = Color.LightGray
                        )
                    }
                }
            }
        },
        modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Scaffold(
            topBar = {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Black
                        )
                    }
                    Text(
                        text = "Categories",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                    )
                }
            },
            bottomBar = {
                BottomNavBar(navController = navController)
            }
        ) {values ->
            if(isLoading){
                Column(Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center) {
                    CircularProgressIndicator(color = myBlue,)
                }
            }
            else{
                Row(modifier = Modifier.fillMaxSize().padding(values)) {
                    LazyColumn(
                        modifier = Modifier.fillMaxHeight().width(96.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val list = listOf(
                            Categories(R.drawable.all, "All","all"),
                            Categories(R.drawable.fashion, "Fashion","tops"),
                            Categories(R.drawable.perfume, "Perfume","fragrances"),
                            Categories(R.drawable.appliances, "Appliances","kitchen-accessories"),
                            Categories(R.drawable.furniture, "Furniture","furniture"),
                            Categories(R.drawable.electronics, "Electronics","smartphones"),
                            Categories(R.drawable.groceries, "Groceries","groceries"),
                            Categories(R.drawable.accessories, "Accessories","mobile-accessories"),
                        )
                        items(list) {
                            Button(
                                onClick = {
                                    selectedCategory = it.category
                                },
                                border = if(it.category == selectedCategory) BorderStroke(width = 1.dp, color = myBlue) else BorderStroke(0.dp, Color.Transparent),
                                colors = ButtonDefaults.textButtonColors(
                                    containerColor = Color.White
                                ),
                                shape = CircleShape,
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                                modifier = Modifier
                                    .size(80.dp) // Adjust size as needed
                                    .shadow(8.dp, CircleShape), // Elevated effect
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Image(
                                        painter = painterResource(id = it.image),
                                        contentDescription = it.title,
                                        modifier = Modifier.size(24.dp) // Adjust image size as needed
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = it.title, textAlign = TextAlign.Center, fontSize = 10.sp, color = Color.Black)
                                }
                            }
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    LazyColumn {
                        items(allProducts){
                            ProductCard(it) {
                                navController.navigate("${Routes.ProductInfo.route}/${it.id}")
                            }
                        }
                    }
                }
            }
        }
    }
}