package com.example.neptuneshop.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Scale
import com.example.neptuneshop.AppDatabase
import com.example.neptuneshop.R
import com.example.neptuneshop.model.Cart
import com.example.neptuneshop.model.Product
import com.example.neptuneshop.model.ProductX
import com.example.neptuneshop.network.ApiService
import com.example.neptuneshop.ui.theme.myBlue
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.reflect.typeOf

@Composable
fun OrdersScreen(
    apiService: ApiService,
    db:AppDatabase,
    navController: NavHostController,
    onSignOut:() -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val sharedPreferences = context.getSharedPreferences("user_profile", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("userId", "")?.toInt()
    val usersName = sharedPreferences.getString("name","")

    var cart by remember { mutableStateOf<Cart?>(null) }

    LaunchedEffect(Unit) {
        apiService.getCartById(userId!!).enqueue(object : Callback<Cart>{
            override fun onResponse(p0: Call<Cart>, p1: Response<Cart>) {
                if(p1.isSuccessful){
                    cart = p1.body()!!
                }
            }

            override fun onFailure(p0: Call<Cart>, p1: Throwable) {
                Log.d("cartFailed","${p1.message}")
                Toast.makeText(context,"id: ${userId!!::class.simpleName ?: "Null"}", Toast.LENGTH_SHORT).show()
            }


        })
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            val items = listOf(
                MenuOption(
                    "Shop by Categories",
                    R.drawable.category,
                    Routes.CategoriesScreen.route
                ),
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
                                        val sharedPreferences = context.getSharedPreferences(
                                            "user_profile",
                                            Context.MODE_PRIVATE
                                        )
                                        with(sharedPreferences.edit()) {
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
                    if (item.title == "Saved Cards") {
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
                        text = "Orders",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                    )
                }
            },
            bottomBar = { BottomNavBar(navController) }
        ) { values ->
            Column(modifier = Modifier.fillMaxSize().padding(values)) {
                if(cart == null){
                    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = myBlue)
                    }
                }
                else{
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        //Title
                        Text(
                            text = "Cart#: ${cart!!.id}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))



                        Spacer(modifier = Modifier.height(8.dp))

                        // Price Section
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Total: $${cart!!.total.toInt()}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = buildAnnotatedString {
                                    append("Discount included: ")
                                    withStyle(SpanStyle(color = Color.Red)){
                                        append("$${cart!!.discountedTotal.toInt()}")
                                    }
                                },
                                fontSize = 18.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Stock Availability & Brand
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = buildAnnotatedString {
                                    append("Owner: ")
                                    withStyle(
                                        SpanStyle(
                                            color = Color.Black,
                                            fontWeight = FontWeight.Bold
                                        )
                                    ){
                                        append(usersName)
                                    }
                                },
                                fontSize = 18.sp,
                                color = Color.Gray)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Additional Product Info
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Text(
                                    text = "Cart details:",
                                    fontSize = 20.sp,
                                    color = Color.Black
                                )
                            }
                            Text(
                                text = "- Total products: ${cart!!.totalProducts}",
                                modifier = Modifier.padding(start = 8.dp),
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "- Total quantity: ${cart!!.totalQuantity}",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(start = 8.dp),
                                color = Color.Gray
                            )
                        }

                    }
                    Spacer(Modifier.height(8.dp))
                    HorizontalDivider(
                        thickness = 2.dp,
                        color = Color.LightGray
                    )
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(cart!!.products){
                            OrderCard(it!!) { navController.navigate("${Routes.ProductInfo.route}/${it.id}")}
                        }
                        items(db.purchaseDao().getAllPurchased()){
                            OrderCard(it) { navController.navigate("${Routes.ProductInfo.route}/${it.id}") }
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun OrderCard(
    product: ProductX,
    onClick:()->Unit){
    Card (
        modifier = Modifier.padding(8.dp).size(230.dp,280.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        onClick = {onClick()}) {
        Column(modifier = Modifier.fillMaxSize().padding(start = 4.dp)) {
            Box {
                AsyncImage(
                    modifier = Modifier.size(300.dp, 140.dp),
                    model = ImageRequest.Builder(LocalContext.current)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .data(product.thumbnail)
                        .size(300,200)
                        .scale(Scale.FILL)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.loading),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(5.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Spacer(modifier = Modifier.width(40.dp))
                }
            }
            Spacer(Modifier.height(14.dp))
            Text(
                text = product.title,
                modifier = Modifier.width(160.dp),
                fontSize = 15.sp
            )
            Spacer(Modifier.height(10.dp))
            Row (verticalAlignment = Alignment.CenterVertically){
                Text("$${product.price}", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Text("$${String.format("%.2f",(product.price / (1 - product.discountPercentage/100)))}", style = TextStyle(textDecoration = TextDecoration.LineThrough))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "${product.discountPercentage}% off", color = Color(0xFFB96F0E),
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${product.quantity}", color = Color.Red,fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Text("$${String.format("%.2f",product.total)}", fontSize = 18.sp, color = Color.Gray, textDecoration = TextDecoration.LineThrough)
                Spacer(modifier = Modifier.width(10.dp))
                Text("$${String.format("%.2f",product.discountedTotal)}", fontSize = 18.sp,color = Color.Black, fontWeight = FontWeight.Bold )
            }

        }
    }
}