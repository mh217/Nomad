package com.example.nomad

import android.content.Context
import android.graphics.drawable.Icon
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.example.nomad.ui.theme.NomadTheme
import com.example.nomad.ui.theme.justanotherhand
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


data class CardData(var id: String ="", val name: String ="", val img: String="", val opis: String="")
data class HotelData(var id: String ="", val name: String ="", val img: String="", val price: String="")
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            val navController = rememberNavController()
            val currentUser = FirebaseAuth.getInstance().currentUser
            NomadTheme {
                Scaffold (
                    bottomBar = { BottomNavigationBar(navController) }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        NavigationHost(navController = navController)
                    }
                }

                }
            }
        }
    }

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Search : BottomNavItem("main_screen", Icons.Default.Search, "Search")
    object Map : BottomNavItem("map_screen", Icons.Default.LocationOn, "Map")
    object Profile : BottomNavItem("profile_screen", Icons.Default.Person, "Profile")
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    BottomNavigation (
        backgroundColor = Color(0xFFE8AD92)
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route


        val items = listOf(
            BottomNavItem.Search,
            BottomNavItem.Map,
            BottomNavItem.Profile
        )

        items.forEach { item ->
            BottomNavigationItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = null) },
                label = { Text(item.label) }
            )
        }
    }
}

@Composable
fun NavigationHost(navController: NavHostController) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    NavHost(navController, startDestination = if(currentUser != null) "main_screen" else "first_screen") {
        composable("login_screen") {
            LoginScreen(navController = navController)
        }
        composable("registration_screen") {
            RegistrationScreen(navController = navController)
        }
        composable("first_screen") {
            HomeScreen(navController = navController)
        }
        composable("main_screen") {
            MainScreen(navController = navController)
        }
        composable("profile_screen") {
            ProfileScreen(navController = navController)
        }
        composable("map_screen") {
            MapScreen(navController = navController)
        }
        composable("city_detail_screen/{city}") { backStackEntry ->
            val city = backStackEntry.arguments?.getString("city")
            city?.let {cityName->
                CityDetailScreen(city = cityName,navController = navController)
            }
        }
    }
}
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Composable
fun BackgroundImage(modifier: Modifier) {
    Box(modifier) {
        Image (
            painter = painterResource(id = R.drawable.backgroundimg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            alpha = 0.1F
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NomadTheme {
        Greeting("Android")
    }
}

@Composable
fun MainScreen(navController: NavController) {
    var cardList by remember { mutableStateOf(listOf<CardData>()) }
    val db = FirebaseFirestore.getInstance()
    var search by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val places = mutableListOf<CardData>()
        db.collection("Places").get()
            .addOnSuccessListener { doc ->
                for (document in doc) {
                    val card = document.toObject(CardData::class.java)
                    card.id = document.id
                    places.add(card)
                }
                cardList = places
            }
            .addOnFailureListener { e ->
                Log.e("Data", "Data failed", e)
            }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (cardList.isEmpty()) {
            Text("Loading...")
        } else {
            Column {
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    label = { Text("Search", fontSize = 20.sp) },
                    textStyle = TextStyle(fontSize = 15.sp),
                    modifier = Modifier
                        .padding(start = 15.dp, end = 20.dp)
                        .fillMaxWidth(),
                    leadingIcon = {Icon(imageVector = Icons.Filled.Search, contentDescription = null, tint = Color(0xFFED8C5E))}
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    if(search.isEmpty()) {
                        itemsIndexed(cardList) { index, card ->
                            CardItem(card = card,navController)
                        }
                    }
                    else {
                        val filteredList = cardList.filter {it.name.lowercase().startsWith(search.lowercase())}
                        itemsIndexed(filteredList) { index, card ->
                            CardItem(card = card,navController)
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun CardItem(card: CardData,navController: NavController) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium
    ) {
        Card(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFED8C5E)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(corner = CornerSize(16.dp))
            ) {
            Row {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(card.img),
                        contentDescription = "Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(corner = CornerSize(16.dp)))
                            .clickable {
                                val city = card.name
                                navController.navigate("city_detail_screen/$city")
                            }
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                         text = card.name,
                         style = MaterialTheme.typography.headlineMedium,
                         modifier = Modifier.padding(bottom = 8.dp, start = 5.dp),
                         fontFamily = justanotherhand,
                         color = Color.White,
                        )

                }
            }
        }
    }
}

@Composable
fun HotelCards(card: HotelData) {
    Surface(
        modifier = Modifier
            .width(350.dp)
            .padding(8.dp),
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium
    ) {
        Card(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .width(200.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFED8C5E)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(corner = CornerSize(16.dp))
        ) {
            Row {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(card.img),
                        contentDescription = "Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(350.dp)
                            .height(150.dp)
                            .clip(RoundedCornerShape(corner = CornerSize(16.dp)))
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = card.name,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(bottom = 8.dp, start = 5.dp),
                        fontFamily = justanotherhand,
                        color = Color.White,
                    )
                    Spacer(modifier = Modifier.height(1.dp))
                    Text(
                        text = "Cijena noćenja: ${card.price}€",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(bottom = 8.dp, start = 5.dp),
                        fontFamily = justanotherhand,
                        color = Color.White,
                        fontSize = 20.sp
                    )

                }
            }
        }
    }
}

@Composable
fun CityDetailScreen(city: String, navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    var cityImage by remember { mutableStateOf("") }
    var name by remember {mutableStateOf("")}
    var opis by remember {mutableStateOf("")}
    var hotelList by remember { mutableStateOf(listOf<HotelData>()) }

    LaunchedEffect(city) {
        db.collection("Places").document(city).get()
            .addOnSuccessListener { doc->
                cityImage = doc.get("img").toString()
                name = doc.get("name").toString()
                opis = doc.get("opis").toString()
            }
        val hotels = mutableListOf<HotelData>()
        db.collection("Hotels").whereEqualTo("city",city).get()
            .addOnSuccessListener { doc->
                for (document in doc) {
                    val hotel = document.toObject(HotelData::class.java)
                    hotel.id = document.id
                    hotels.add(hotel)
                }
                hotelList = hotels
            }
    }
    Column {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Image(
                painter = rememberAsyncImagePainter(cityImage),
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(corner = CornerSize(5.dp)))
            )
            Text(
                text = name,
                fontSize = 80.sp,
                style = MaterialTheme.typography.headlineMedium.copy(fontFamily = justanotherhand),
                color = Color.White,
            )
        }
        Text(
            text = opis,
            style = MaterialTheme.typography.headlineMedium.copy(fontFamily = justanotherhand),
            fontSize = 20.sp,
            modifier = Modifier.padding(10.dp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        LazyRow(
            modifier = Modifier.fillMaxSize(),
        ) {
            itemsIndexed(hotelList) { index, card ->
                HotelCards(card = card)
            }
        }


    }
}

@Composable
fun ProfileScreen(navController: NavController) {
    Text(text = "Profil")
}

@Composable
fun MapScreen(navController :NavController) {
    Text(text = "Mapa")
}

@Composable
fun HomeScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        BackgroundImage(modifier = Modifier.fillMaxSize())
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(100.dp)
                    .clip(shape = RoundedCornerShape(8.dp))
            )
            Text(
                text = "Nomad",
                style = MaterialTheme.typography.headlineLarge.copy(fontFamily = justanotherhand),
                textAlign = TextAlign.Left,
                fontSize = 80.sp,
                )
            Text(
                text = "Find your paradise.",
                style = MaterialTheme.typography.headlineMedium.copy(fontFamily = justanotherhand),
                color = Color.White,
                fontSize = 50.sp
            )
            Spacer(
                modifier = Modifier.height(300.dp)
            )
            Button(
                onClick = {navController.navigate("registration_screen")},
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFED8C5E)))
            {
                Text(text = "Registracija", fontSize = 25.sp, fontFamily = justanotherhand)
            }
            Button(
                onClick = {navController.navigate("login_screen")},
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8AD92)))
            {
                Text(text = "Prijava", fontSize = 25.sp, fontFamily = justanotherhand)
            }
        }
    }
}

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember {mutableStateOf("")}
    var password by remember {mutableStateOf("")}
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        BackgroundImage(modifier = Modifier.fillMaxSize())
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(9.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Prijava",
                style = MaterialTheme.typography.headlineLarge.copy(fontFamily = justanotherhand),
                textAlign = TextAlign.Center,
                fontSize = 80.sp,
                color = Color.White,
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", fontSize = 20.sp) },
                textStyle = TextStyle(fontSize = 15.sp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Surname", fontSize = 20.sp) },
                textStyle = TextStyle(fontSize = 15.sp),
                visualTransformation = PasswordVisualTransformation(),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { signIn(context, email, password, navController) }) {
                Text("Login")
            }
        }
    }
}

@Composable
fun RegistrationScreen(navController: NavController) {
    var email by remember {mutableStateOf("")}
    var password by remember {mutableStateOf("")}
    var name by remember {mutableStateOf("")}
    var surname by remember {mutableStateOf("")}
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        BackgroundImage(modifier = Modifier.fillMaxSize())
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(9.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Registracija",
                style = MaterialTheme.typography.headlineLarge.copy(fontFamily = justanotherhand),
                textAlign = TextAlign.Center,
                fontSize = 80.sp,
                color = Color.White,
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name", fontSize = 20.sp) },
                textStyle = TextStyle(fontSize = 15.sp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = surname,
                onValueChange = { surname = it },
                label = { Text("Surname", fontSize = 20.sp) },
                textStyle = TextStyle(fontSize = 15.sp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", fontSize = 20.sp) },
                textStyle = TextStyle(fontSize = 15.sp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", fontSize = 20.sp) },
                visualTransformation = PasswordVisualTransformation(),
                textStyle = TextStyle(fontSize = 15.sp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { register(context, name, surname, email, password, navController) }) {
                Text("Register")
            }

        }
    }
}

private fun register(context: Context, name: String, surname: String, email: String, password: String, navController: NavController) {
    var db = FirebaseFirestore.getInstance()
    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
        .addOnCompleteListener { task ->
            if(task.isSuccessful) {
                val user = FirebaseAuth.getInstance().currentUser
                val userData = hashMapOf(
                    "Name" to name,
                    "Surname" to surname,
                    "Email" to email
                )

                user?.uid?.let {
                    db.collection("Users")
                        .document(it)
                        .collection("Details")
                        .document("UserDetails")
                        .set(userData)
                }
                Toast.makeText(context, "Register successfully", Toast.LENGTH_SHORT).show()
                navController.navigate("main_screen")
            }
            else {
                Toast.makeText(context, "Registration failed", Toast.LENGTH_SHORT).show()
            }
        }
        .addOnFailureListener {e->
            Log.e("Refistration", "Registration failed", e)
            Toast.makeText(context, "Registration failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
}

private fun signIn(context: Context, email: String, password: String, navController: NavController) {
    FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
        .addOnCompleteListener {task ->
            if(task.isSuccessful) {
                navController.navigate("main_screen")
            }
            else {
                Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show()
                navController.navigate("first_screen")
            }
        }

}

