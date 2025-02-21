package com.example.neptuneshop.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Scale
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.neptuneshop.R
import com.example.neptuneshop.model.Product
import com.example.neptuneshop.model.Products
import com.example.neptuneshop.network.ApiService
import com.example.neptuneshop.ui.theme.myBlue
import com.example.neptuneshop.ui.theme.myRed
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@Composable
fun HomeScreen(
    navController: NavHostController,
    onSignOut:() -> Unit,
    apiService: ApiService,
) {
    var allProducts by remember { mutableStateOf<List<Product>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var selectedCategory by remember { mutableStateOf("all") }


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
            errorMessage = "Error: ${e.message}"
        }
    }

    Column (
        modifier = Modifier.fillMaxSize()
    ) {
        when{
            isLoading ->{
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = myBlue)
                }
            }
            errorMessage != null ->{
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val error by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.error))
                    LottieAnimation(
                        composition = error,
                        modifier = Modifier.size(300.dp),
                        iterations = LottieConstants.IterateForever
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = errorMessage!!,
                        fontSize = 16.sp,
                        color = myRed
                    )
                }
            }
            else ->{
                val items = listOf(
                    MenuOption("Shop by Categories", R.drawable.category, Routes.CategoriesScreen.route),
                    MenuOption("My Orders", R.drawable.clock, Routes.OrdersScreen.route),
                    MenuOption("Favorites", R.drawable.heart, Routes.FavoritesScreen.route),
                    MenuOption("Profile", R.drawable.account, Routes.ProfileScreen.route),
                    MenuOption("Contributors", R.drawable.conversation, Routes.Contributors.route),
                    MenuOption("Sign Out", R.drawable.exit, Routes.LoginScreen.route)
                )
                val drawerState = rememberDrawerState(DrawerValue.Closed)
                val context = LocalContext.current
                val scope = rememberCoroutineScope()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
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
                ) {
                    Scaffold(
                        contentWindowInsets = WindowInsets.systemBars,
                        bottomBar = {
                            BottomNavBar(navController)
                        }
                    ) { paddingValues ->
                        Column(
                            modifier = Modifier.fillMaxSize().padding(paddingValues).background(Color.White)
                        ) {
                            Spacer(Modifier.height(10.dp))
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = {
                                    scope.launch {
                                        drawerState.open()
                                    }
                                }) {Icon(Icons.Default.Menu, contentDescription = null, tint = Color.Gray) }
                                Spacer(Modifier.width(6.dp))
                                Text("Home", fontWeight = FontWeight.Bold)
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                                    Image(painter = painterResource(R.drawable.notification), contentDescription = null,Modifier.size(25.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Image(painter = painterResource(R.drawable.bag), contentDescription = null,Modifier.size(30.dp))

                                }
                            }
                            //search field
                            Card(
                                onClick = {navController.navigate(Routes.SearchScreen.route)},
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.Transparent
                                )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                        .border(1.dp, Color.Gray, shape = RoundedCornerShape(10.dp))
                                        .height(50.dp)
                                        .background(Color.White),
                                    contentAlignment = Alignment.CenterStart,
                                ) {
                                    Row(modifier = Modifier.padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Search, contentDescription = null, tint = Color.DarkGray)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text("Search Anything...", fontSize = 15.sp, color = Color.Gray)
                                    }
                                }
                            }
                            //categories section
                            Row(Modifier.fillMaxWidth()) {
                                Spacer(Modifier.width(4.dp))
                                Column {
                                    Spacer(Modifier.width(10.dp))
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Text("Categories")
                                        TextButton(onClick = {}, contentPadding = PaddingValues(vertical = 0.dp, horizontal = 4.dp)) { Text(text="View All", fontSize = TextUnit.Unspecified) }
                                    }
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ){
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
                                                colors = ButtonDefaults.textButtonColors(
                                                    containerColor = Color.White
                                                ),
                                                border = if(it.category == selectedCategory) BorderStroke(width = 1.dp, color = myBlue) else BorderStroke(0.dp, Color.Transparent),
                                                shape = CircleShape,
                                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                                                modifier = Modifier
                                                    .size(80.dp), // Adjust size as needed
//                                                    .shadow(8.dp, CircleShape), // Elevated effect
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
                                    Spacer(Modifier.height(16.dp))
                                }
                            }
                            LazyColumn(modifier = Modifier.fillMaxSize().weight(1f)) {
                                item { Banner() }
                                item {
                                    //Highest rating items
                                    val highratedItems = allProducts.sortedByDescending { it.rating }.take(5)
                                    if(highratedItems.isNotEmpty()){
                                        Column(
                                            modifier = Modifier.padding(8.dp)
                                        ) {
                                            TitleText("Recommended for you")
                                            LazyRow {
                                                items(highratedItems){
                                                    ProductCard(it) {
                                                        navController.navigate("${Routes.ProductInfo.route}/${it.id}")
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                item {
                                    //most discounted items
                                    val discountedItems = allProducts.sortedByDescending{ it.discountPercentage }.take(5)
                                    if(discountedItems.isNotEmpty()){
                                        Column(
                                            modifier = Modifier.padding(8.dp)
                                        ) {
                                            TitleText("Deal of the day")
                                            LazyRow {
                                                items(discountedItems){
                                                    ProductCard(it) {
                                                        navController.navigate("${Routes.ProductInfo.route}/${it.id}")
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                item {
                                    //cheapest items
                                    val cheapestItems = allProducts.sortedBy { it.price }
                                    if(cheapestItems.isNotEmpty()){
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            TitleText("Budget picks")
                                            LazyRow {
                                                items(allProducts.sortedBy { it.price }.take(5)){
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
                    }
                }
            }
        }
    }
}

data class MenuOption(val title: String, val icon: Int, val route: String)



@Composable
fun ProductCard(
    product: Product,
    onClick:() ->Unit){
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
                        .data(product.images[0])
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
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text("${product.rating}", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(10.dp))
                Text("(${product.stock})")
            }

        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Banner() {
    val banners= listOf(R.drawable.banner_lipstick,R.drawable.banner_phone,R.drawable.banner_perfume1,R.drawable.banner_phone2, R.drawable.banner_perfume2)
    val pagerState = rememberPagerState(pageCount = { banners.size })

    Column {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) { page ->
            Box(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Image(
                    painter = painterResource(id = banners[page]),
                    contentDescription = "Banner $page",
                    modifier = Modifier.fillMaxWidth().height(256.dp).align(Alignment.Center))
            }
        }

        Row(
            Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(banners.size) { index ->
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(if (pagerState.currentPage == index) myBlue else Color.LightGray, CircleShape),
                )
                Spacer(Modifier.width(8.dp))
            }
        }
    }
}


data class Categories(val image:Int, val title:String, val category: String)


@Composable
fun TitleText(
    title:String
){
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,horizontalArrangement = Arrangement.Start) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(start = 16.dp))
    }
}



