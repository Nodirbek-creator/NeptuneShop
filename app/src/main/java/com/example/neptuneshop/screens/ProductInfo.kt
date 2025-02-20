package com.example.neptuneshop.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Scale
import com.example.neptuneshop.R
import com.example.neptuneshop.model.Product
import com.example.neptuneshop.network.ApiService
import com.example.neptuneshop.ui.theme.myBlue
import com.example.neptuneshop.ui.theme.myRed
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductInfo(
    id:Int,
    navController: NavHostController,
    apiService: ApiService,
) {
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var isFavorite by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isLoading = true
        try{
            apiService.getProductById(id).enqueue(object : Callback<Product>{
                override fun onResponse(call: Call<Product>, response: Response<Product>) {
                    if(response.isSuccessful){
                        isLoading = false
                        products = listOf(response.body()!!)
                    }
                }

                override fun onFailure(call: Call<Product>, t: Throwable) {
                    Log.d("fetch Failed", "${t.message}")
                }

            })
        }
        catch (e:Exception){
            Log.d("fetch Failed", "${e.message}")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigate(Routes.HomeScreen.route)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Black
                        )
                    }
                },
                actions = {

                    Row(
                        modifier = Modifier.width(300.dp).padding(start = 40.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(7.dp)
                    ) {
                        if (isFavorite) {
                            IconButton(
                                onClick = { isFavorite = !isFavorite },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    null,
                                    tint = myRed,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        } else {
                            IconButton(
                                onClick = { isFavorite = !isFavorite },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FavoriteBorder,
                                    null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        IconButton (
                            onClick = { },

                            ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                null,
                                tint = Color.Black,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Button(
                            onClick = {

                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White
                            ),
                            shape = CircleShape
                        ) {
                            Image(
                                painter = painterResource(R.drawable.shopping_bag),
                                null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        contentWindowInsets = WindowInsets.systemBars
    ) {
            values ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(values)
                .verticalScroll(rememberScrollState())
        ) {
            val product = products[0]
            if(isLoading){
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = myBlue,
                        modifier = Modifier.size(160.dp)
                    )
                }
            }
            else{
                ProductImageSection(product.images[0])
                ProductDetailsSection(product)
                ProductDescription(product)
                RatingsAndReviews(product)
                ActionButtons()
            }
        }
    }

}

@Composable
fun ProductImageSection(
    imageUrl:String
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .data(imageUrl)
            .size(300,200)
            .scale(Scale.FILL)
            .crossfade(true)
            .build(),
        contentDescription = "Product Image",
        modifier = Modifier.fillMaxWidth().height(480.dp),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun ProductDetailsSection(
    product: Product
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(product.brand, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(product.title, color = Color.Gray, fontSize = 16.sp)
        Row(modifier = Modifier.padding(top = 8.dp)) {
            Text("$${product.price}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.width(8.dp))
            Text("$${String.format("%.2f",(product.price / (1 - product.discountPercentage/100)))}", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.width(8.dp))
            Text("${product.discountPercentage}% off", fontSize = 14.sp, color = Color.Green)
        }
    }
}

@Composable
fun ProductDescription(
    product: Product
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Product Details", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        Text("WarrantyInformation: ${product.warrantyInformation}\nShippingInformation: ${product.shippingInformation}\nAvailabilityStatus: ${product.availabilityStatus}\n*ReturnPolicy: ${product.returnPolicy}", color = Color.Gray, fontSize = 16.sp)
    }
}

@Composable
fun RatingsAndReviews(
    product: Product
) {
    val review = product.reviews[0]
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Ratings & Reviews (${product.reviews.size} ratings)", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        Row(modifier = Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("${review.rating}/5", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Overall Rating", fontSize = 14.sp, color = Color.Gray)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(review.reviewerName, fontSize = 20.sp, color = Color.Black)
        Text(review.comment, fontSize = 16.sp, color = Color.Gray)
    }
}


@Composable
fun ActionButtons() {
    Row(modifier = Modifier.padding(16.dp)) {
        Button(
            onClick = { /* TODO */ },
            modifier = Modifier.weight(1f).padding(end = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow)
        ) { Text("Add to cart", color = Color.White) }
        Button(
            onClick = { /* TODO */ },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
        ) { Text("Buy Now", color = Color.White) }
    }
}
