package com.example.neptuneshop.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Scale
import com.example.neptuneshop.AppDatabase
import com.example.neptuneshop.R
import com.example.neptuneshop.model.FavProduct
import com.example.neptuneshop.model.Product
import com.example.neptuneshop.model.ProductX
import com.example.neptuneshop.network.ApiService
import com.example.neptuneshop.ui.theme.myBlue
import com.example.neptuneshop.ui.theme.myRed
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductInfo(
    id:Int,
    navController: NavHostController,
    apiService: ApiService,
    db:AppDatabase
) {
    var product by remember { mutableStateOf<Product?>(null) }
    var isFavorite by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val favDao = db.favoriteDao()
    val buyDao = db.purchaseDao()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                apiService.getProductById(id).enqueue(object : Callback<Product>{
                    override fun onResponse(call: Call<Product>, response: Response<Product>) {
                        if(response.isSuccessful){
                            product = response.body()!!
                            isFavorite = db.favoriteDao().favoriteById(product!!.id) != null
                        }
                    }

                    override fun onFailure(call: Call<Product>, t: Throwable) {
                        Log.d("fetch Failed", "${t.message}")
                    }

                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
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
                IconButton(
                    onClick = {}
                ) {
                    IconButton(
                        onClick = {
                            isFavorite = !isFavorite
                            if(!isFavorite){
                                favDao.deleteFavById(product!!.id)
                            }
                            else{
                                if(product != null){
                                    favDao.addFavorite(
                                        favProduct = FavProduct(
                                            id = product!!.id,
                                            thumbnail = product!!.images[0],
                                            title = product!!.title,
                                            rating = product!!.rating,
                                            stock = product!!.stock,
                                            price = product!!.price,
                                            discountPercentage = product!!.discountPercentage
                                        )
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if(isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "",
                            modifier = Modifier.size(24.dp),
                            tint = if(isFavorite) myRed else Color.Black
                        )
                    }
                }
            }
        },
        modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars)
    ) { values ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(values)
                .verticalScroll(rememberScrollState())
        ) {
            if(product == null){
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = myBlue,)
                }
            }
            else{
                ProductImageSlider(imageUrls = product!!.images)
                Spacer(Modifier.height(8.dp))
                ProductDetailsSection(product!!)
                ReviewSection(product!!)
                var count by remember { mutableStateOf(0) }
                Text("Amount: ", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween // Distributes items: start, center, end
                ) {
                    Button(
                        onClick = { if(count>0){ count-- }},
                        colors = ButtonDefaults.textButtonColors(containerColor = myBlue, contentColor = Color.White)
                        ) {
                        Text(text = "-")
                    }

                    Text(text = count.toString(), fontSize = 18.sp)

                    Button(
                        onClick = { if(count<product!!.stock){count++}},
                        colors = ButtonDefaults.textButtonColors(containerColor = myBlue, contentColor = Color.White)) {
                        Text(text = "+")
                    }
                }
                Button(
                    onClick = {
                        if(count>0){
                            buyDao.buyProduct(
                                productX = ProductX(
                                    id = product!!.id,
                                    price = product!!.price,
                                    quantity = count,
                                    thumbnail = product!!.images[0],
                                    title = product!!.title,
                                    discountPercentage = product!!.discountPercentage,
                                    discountedTotal = (product!!.price*count)*(100-product!!.discountPercentage),
                                    total = (product!!.price * count)
                                )
                            )
                            Toast.makeText(context, "Added successfully:)", Toast.LENGTH_SHORT).show()
                        }
                        else{
                            Toast.makeText(context, "Insert the correct amount!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = myBlue,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                ) {
                    Text("Add to Cart", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
            }
        }
    }

}
@Composable
fun ProductImageSlider(imageUrls: List<String>) {
    val pagerState = rememberPagerState(pageCount = { imageUrls.size })

    Column {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth().height(250.dp))
        { page ->
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .data(imageUrls[page])
                    .size(300,200)
                    .scale(Scale.FILL)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.loading),
                contentDescription = "Product Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
        if(imageUrls.size>1){
            Row(
                Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(imageUrls.size) { index ->
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
}
@Composable
fun ProductDetailsSection(product: Product) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        //Title
        Text(
            text = product.title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Rating & Reviews
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Star, contentDescription = "Rating", tint = Color(0xFFFFD700))
            Text(
                text = "${product.rating}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp)
            )
            Text(
                text = "${product.reviews.size} Reviews",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Price Section
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "$${String.format("%.2f", product.price - (product.price * product.discountPercentage / 100))}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "$${product.price}",
                fontSize = 14.sp,
                color = Color.Gray,
                textDecoration = TextDecoration.LineThrough,
                modifier = Modifier.padding(start = 8.dp)
            )
            Text(
                text = "${product.discountPercentage}% OFF",
                fontSize = 14.sp,
                color = Color.Red,
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
                    append("Brand: ")
                    withStyle(SpanStyle(
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )){
                        append(product.brand)
                    }
                },
                fontSize = 18.sp,
                color = Color.Gray)
            Text(
                text = "Only ${product.stock} Left",
                fontSize = 16.sp,
                color = Color.Red,
                fontWeight = FontWeight.Bold
            )
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
                    text = "Product details:",
                    fontSize = 20.sp,
                    color = Color.Black
                )
            }
            Text(
                text = "- "+product.shippingInformation,
                modifier = Modifier.padding(start = 8.dp),
                fontSize = 16.sp,
                color = Color.Gray
            )
            Text(
                text = "- "+product.warrantyInformation,
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 8.dp),
                color = Color.Gray
            )
            Text(
                text = "- "+product.returnPolicy,
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 8.dp),
                color = Color.Gray
            )
        }

    }
}
@Composable
fun ReviewSection(product: Product) {
    var date = formatDate(product.reviews[0].date)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Section Title
        Text(
            text = "Ratings & Reviews",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Overall Rating Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "${product.rating}/5",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${product.reviews.size} Ratings",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            Button(
                onClick = { /* Handle Rating Action */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEF1F6))
            ) {
                Text(text = "Rate", color = Color.Blue)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display First Review (Following Example Photo)
        if (product.reviews.isNotEmpty()) {
            val review = product.reviews.first()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                // Reviewer Name as Comment Title
                Text(
                    text = review.reviewerName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Star Rating Row
                Row {
                    repeat(review.rating) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Review Comment
                Text(
                    text = review.comment,
                    fontSize = 14.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Reviewer Email (Replaces Name in Gray)
                Text(
                    text = "${review.reviewerEmail},  $date",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
fun formatDate(inputDate: String): String {
    // Parse the input date string
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    inputFormat.timeZone = TimeZone.getTimeZone("UTC") // Set UTC timezone

    val date = inputFormat.parse(inputDate) ?: return ""

    // Extract day, month, and year
    val calendar = Calendar.getInstance().apply { time = date }
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val month = SimpleDateFormat("MMMM", Locale.US).format(date)
    val year = calendar.get(Calendar.YEAR)

    // Get ordinal suffix (1st, 2nd, 3rd, 4th, etc.)
    val ordinal = when {
        day in 11..13 -> "th"
        day % 10 == 1 -> "st"
        day % 10 == 2 -> "nd"
        day % 10 == 3 -> "rd"
        else -> "th"
    }

    // Return formatted date
    return "$day$ordinal $month, $year"
}