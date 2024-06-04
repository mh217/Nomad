package com.example.nomad

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale


data class CardData(var id: String ="", val name: String ="", val img: String="", val opis: String="")
data class HotelData(var id: String ="", val name: String ="", val img: String="", val price: String="")
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            val navController = rememberNavController()
            val currentUser = FirebaseAuth.getInstance().currentUser
            val db = FirebaseFirestore.getInstance()
            var arrival by remember { mutableStateOf("") }
            val context = LocalContext.current

            NomadTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    run()
                }
            }


            if(currentUser != null) {
                currentUser?.let {
                    db.collection("Users").document(it.uid).collection("Reservations")
                        .document("Reservations").get()
                        .addOnSuccessListener { doc ->
                            arrival = doc.get("Dolazak").toString()
                        }

                    createNotificationChannel("Arrival", context)
                    val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
                    val currentDateString = dateFormat.format(Date())
                    if (arrival == currentDateString) {
                        NotificationManagerCompat.from(context).areNotificationsEnabled()
                        showSimpleNotificationWithTapAction(
                            this, "Arrival", 1, "Dolazak je zakazan danas", "Vaš dolazak je danas! Uživajte u odmoru!",
                            NotificationCompat.PRIORITY_DEFAULT
                        )
                    }
            }
                }
            }
        }
    }

@Composable
fun run() {
    val navController = rememberNavController()
    val bottomBarState = remember { mutableStateOf(true) }

    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener {_,destination,_ ->
            bottomBarState.value = when (destination.route) {
                BottomNavItem.Search.route, BottomNavItem.Map.route, BottomNavItem.Profile.route -> true
                else -> false
            }
        }
    }

    Scaffold (bottomBar = {  if(bottomBarState.value) {BottomNavigationBar(navController) }}) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavigationHost(navController = navController)

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
                alwaysShowLabel = true,
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
        composable("hotel_detail_screen/{hotel}") { backStackEntry ->
            val hotel = backStackEntry.arguments?.getString("hotel")
            hotel?.let {hotelName->
                HotelDetailScreen(hotel = hotelName,navController = navController)
            }
        }
        composable("reservation_screen/{hotel}") { backStackEntry ->
            val hotel = backStackEntry.arguments?.getString("hotel")
            hotel?.let {hotelName->
                ReservationScreen(hotel = hotelName, navController = navController)
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
fun HotelCards(card: HotelData,navController: NavController) {
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
                            .clickable {
                                val hotel = card.name
                                navController.navigate("hotel_detail_screen/$hotel")
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
                HotelCards(card = card,navController)
            }
        }


    }
}

@Composable
fun GalleryCards(image: String) {
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
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(corner = CornerSize(16.dp))
        ) {
            Row {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(image),
                        contentDescription = "Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(350.dp)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(corner = CornerSize(16.dp)))

                    )
                }

            }
        }
    }
}


@Composable
fun HotelDetailScreen(hotel: String, navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    var hotelImage by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var gallery by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(hotel) {
        db.collection("Hotels").whereEqualTo("name", hotel).get()
            .addOnSuccessListener { doc ->
                val firstHotel = doc.first()
                hotelImage = firstHotel.get("img").toString()
                name = firstHotel.get("name").toString()
                price = firstHotel.get("price").toString()
                gallery = firstHotel.get("galerija") as? List<String> ?: emptyList()
            }
    }
    Column {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Image(
                painter = rememberAsyncImagePainter(hotelImage),
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(corner = CornerSize(5.dp)))
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = name,
            fontSize = 50.sp,
            style = MaterialTheme.typography.headlineMedium.copy(fontFamily = justanotherhand),
            modifier = Modifier.padding(start = 5.dp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = "Cijena noćenja: ${price}€",
            fontSize = 30.sp,
            style = MaterialTheme.typography.headlineMedium.copy(fontFamily = justanotherhand),
            modifier = Modifier.padding(start = 5.dp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Button(
            onClick = {navController.navigate("reservation_screen/$name")},
            modifier = Modifier
                .fillMaxWidth(0.3f)
                .padding(top = 5.dp, start = 5.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFED8C5E)))
        {
            Text(text = "Rezerviraj", fontSize = 25.sp, fontFamily = justanotherhand)
        }
        Spacer(modifier = Modifier.height(3.dp))
        LazyRow(
            modifier = Modifier.fillMaxSize(),
        ) {
            itemsIndexed(gallery) { index, image ->
                GalleryCards(image = image)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationScreen(hotel : String, navController: NavController) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser
    var email by remember {mutableStateOf("")}
    var phone by remember {mutableStateOf("")}
    var name by remember {mutableStateOf("")}
    var surname by remember {mutableStateOf("")}
    var selectedGuests by remember { mutableStateOf("Select number of guests") }
    var expanded by remember { mutableStateOf(false) }
    val guestOptions = listOf("1", "2", "3", "4", "5")
    var selectedArrivalDate by remember { mutableStateOf("Vrijeme dolaska") }
    var selectedDepartureDate by remember { mutableStateOf("Vrijeme odlaska") }
    val Arrivalopening  = remember {mutableStateOf(false)}
    val Departureopening  = remember {mutableStateOf(false)}

    Column (modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = hotel,
            fontSize = 50.sp,
            style = MaterialTheme.typography.headlineMedium.copy(fontFamily = justanotherhand),
            modifier = Modifier.padding(start = 5.dp)
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
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Broj telefona", fontSize = 20.sp) },
            textStyle = TextStyle(fontSize = 15.sp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Box (modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.Center)){
            OutlinedTextField(
                value = selectedGuests,
                onValueChange = { selectedGuests = it },
                textStyle = TextStyle(fontSize = 15.sp),
                readOnly = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = "Expand Dropdown",
                        modifier = Modifier.clickable { expanded = !expanded }
                    )
                },
                modifier = Modifier
                    .clickable { expanded = !expanded }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                guestOptions.forEach { guest ->
                    DropdownMenuItem(
                        text = { Text(guest) },
                        onClick = {
                            selectedGuests = guest
                            expanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OutlinedButton(
                modifier = Modifier.wrapContentWidth(Alignment.CenterHorizontally),
                onClick = {Arrivalopening.value = true},
            ) {
                Text(text = selectedArrivalDate, fontSize = 20.sp)
            }
            OutlinedButton(
                modifier = Modifier
                    .wrapContentWidth(Alignment.CenterHorizontally),
                onClick = {Departureopening.value = true},
            ) {
                Text(text = selectedDepartureDate, fontSize = 20.sp)
            }
        }
        Button(
            onClick = {
                    val data = hashMapOf(
                        "Dolazak" to selectedArrivalDate,
                        "Odlazak" to selectedDepartureDate,
                        "Hotel" to hotel,
                        "Gosti" to selectedGuests
                    )
                    currentUser?.let {
                        db.collection("Users").document(it.uid).collection("Reservations").document("Reservations").set(data)
                    }
                    Toast.makeText(context, "Rezervacija spremljena", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()

            },
            modifier = Modifier
                .fillMaxWidth(0.3f)
                .padding(top = 5.dp, start = 5.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFED8C5E)))
        {
            Text(text = "Rezerviraj", fontSize = 25.sp, fontFamily = justanotherhand)
        }
    }


    if(Arrivalopening.value) {
        var dataPickerState  = rememberDatePickerState()
        val confirmEnabled = remember {
            derivedStateOf { dataPickerState.selectedDateMillis != null }
        }

        DatePickerDialog(onDismissRequest = { Arrivalopening.value = false }, confirmButton = {
            TextButton(onClick = {
                Arrivalopening.value = false
                var date = "No Selection"
                if(dataPickerState.selectedDateMillis != null) {
                    date = convertLongToTimeString(dataPickerState.selectedDateMillis!!)
                }
                selectedArrivalDate = date
            },
                enabled = confirmEnabled.value
            ) {
                Text(text = "Okay")

            }
        }) {
            DatePicker(state = dataPickerState)
        }
    }
    if(Departureopening.value) {
        var dataPickerState  = rememberDatePickerState()
        val confirmEnabled = remember {
            derivedStateOf { dataPickerState.selectedDateMillis != null }
        }

        DatePickerDialog(onDismissRequest = { Departureopening.value = false }, confirmButton = {
            TextButton(onClick = {
                Departureopening.value = false
                var date = "No Selection"
                if(dataPickerState.selectedDateMillis != null) {
                    date = convertLongToTimeString(dataPickerState.selectedDateMillis!!)
                }
                if(selectedArrivalDate == date) {
                    Toast.makeText(context, "Izaberite drugi datum odlaska", Toast.LENGTH_SHORT).show()
                    date = ""
                }
                else {
                    selectedDepartureDate = date
                }
            },
                enabled = confirmEnabled.value
            ) {
                Text(text = "Okay")

            }
        }) {
            DatePicker(state = dataPickerState)
        }
    }
}

fun convertLongToTimeString(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("MM/dd/yyyy")
    return format.format(date)
}


@Composable
fun ProfileScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var arrival by remember { mutableStateOf("") }
    var departure by remember { mutableStateOf("") }
    var guests by remember { mutableStateOf("") }
    var hotelName by remember { mutableStateOf("") }
    var hotelImg by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        currentUser?.let {
            db.collection("Users").document(it.uid).collection("Details").document("UserDetails").get()
                .addOnSuccessListener {doc->
                    name = doc.get("Name").toString()
                    surname = doc.get("Surname").toString()
                }
                .addOnFailureListener { e->
                    Log.e("Data", "Data failed", e)
                }
        }
        currentUser?.let{
            db.collection("Users").document(it.uid).collection("Reservations").document("Reservations").get()
                .addOnSuccessListener { doc->
                        arrival = doc.get("Dolazak").toString()
                        departure = doc.get("Odlazak").toString()
                        guests = doc.get("Gosti").toString()
                        hotelName = doc.get("Hotel").toString()

                }
                .addOnFailureListener { e->
                    Log.e("Data", "Data failed", e)
                }
        }
    }
    LaunchedEffect(hotelName) {
        db.collection("Hotels").whereEqualTo("name", hotelName).get()
            .addOnSuccessListener { doc ->
                if(!doc.isEmpty) {
                    val firstHotel = doc.documents[0]
                    hotelImg = firstHotel.get("img").toString()
                }
                else {
                    hotelImg =""
                }
            }
            .addOnFailureListener { e ->
                Log.e("Data", "Data failed", e)
            }
    }
    Column {
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(R.drawable.rect),
                contentDescription = "background",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            Box(modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .padding(8.dp)) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = name,
                        fontSize = 50.sp,
                        style = MaterialTheme.typography.headlineMedium.copy(fontFamily = justanotherhand),
                        color = Color.White,
                        modifier = Modifier.padding(5.dp)

                        )
                    Text(
                        text = surname,
                        fontSize = 50.sp,
                        style = MaterialTheme.typography.headlineMedium.copy(fontFamily = justanotherhand),
                        color = Color.White,
                        modifier = Modifier.padding(5.dp)

                        )
                }
            }
            Button(
                onClick = {FirebaseAuth.getInstance().signOut()
                    navController.navigate("first_screen")},
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(5.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8AD92)))
            {
                Text(text = "Odjava", fontSize = 25.sp, fontFamily = justanotherhand)
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Rezervacije",
            fontSize = 30.sp,
            style = MaterialTheme.typography.headlineMedium.copy(fontFamily = justanotherhand),
            modifier = Modifier.padding(10.dp)

        )
        Spacer(modifier = Modifier.height(10.dp))
        if(hotelName != "") {
            Card(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 8.dp)
                    .fillMaxWidth()
                    .height(300.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFED8C5E)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(corner = CornerSize(16.dp))
            ) {
                Row {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(hotelImg),
                            contentDescription = "Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .clip(RoundedCornerShape(corner = CornerSize(16.dp)))
                                .clickable {
                                    val hotel = hotelName
                                    navController.navigate("hotel_detail_screen/$hotel")
                                }
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = hotelName,
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(bottom = 8.dp, start = 5.dp),
                            fontFamily = justanotherhand,
                            color = Color.White,
                        )
                        Spacer(modifier = Modifier.height(1.dp))
                        Text(
                            text = "Broj gostiju: ${guests}",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(bottom = 8.dp, start = 5.dp),
                            fontFamily = justanotherhand,
                            color = Color.White,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "Dolazak: ${arrival}",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(bottom = 8.dp, start = 5.dp),
                            fontFamily = justanotherhand,
                            color = Color.White,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "Odlazak: ${departure}",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(bottom = 8.dp, start = 5.dp),
                            fontFamily = justanotherhand,
                            color = Color.White,
                            fontSize = 20.sp
                        )

                    }

                }
            }
        } else{
            Text(
                text = "Trenutno nepostoje nikakve rezervacije",
                fontSize = 30.sp,
                style = MaterialTheme.typography.headlineMedium.copy(fontFamily = justanotherhand),
                modifier = Modifier.padding(10.dp)

            )
        }
    }
}


@Composable
fun MapScreen(navController :NavController) {
    val db = FirebaseFirestore.getInstance()
    val locations = remember { mutableStateOf<List<LatLng>>(emptyList()) }
    val names = remember { mutableStateOf<List<String>>(emptyList()) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(45.10492, 14.62040), 7f)
    }

    LaunchedEffect(Unit) {
        db.collection("Hotels").get()
            .addOnSuccessListener { result ->
                val locationList = mutableListOf<LatLng>()
                val nameList = mutableListOf<String>()

                for (document in result) {
                    val lat = document.getDouble("latitude")
                    val lng = document.getDouble("longitude")
                    val name = document.getString("name")

                    if (lat != null && lng != null && name != null) {
                        locationList.add(LatLng(lat, lng))
                        nameList.add(name)
                    }
                }

                locations.value = locationList
                names.value = nameList
            }
            .addOnFailureListener { exception ->
                Log.w("MapScreen", "Error getting documents: ", exception)
            }
    }
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        locations.value.forEachIndexed { index, location ->
            val name = names.value.getOrNull(index) ?:"Nepoznato"
            Marker(
                state = MarkerState(position = location),
                title = name,
                onClick = {
                    navController.navigate("hotel_detail_screen/$name")
                    true
                }
            )
        }
    }
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
fun createNotificationChannel(channelId: String, context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "MyTestChannel"
        val descriptionText = "My important test channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
fun showSimpleNotificationWithTapAction(
    context: Context,
    channelId: String,
    notificationId: Int,
    textTitle: String,
    textContent: String,
    priority: Int = NotificationCompat.PRIORITY_DEFAULT
) {
    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent,
        PendingIntent.FLAG_IMMUTABLE)

    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.logo)
        .setContentTitle(textTitle)
        .setContentText(textContent)
        .setPriority(priority)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

    with(NotificationManagerCompat.from(context)) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return@with
        }
        notify(1, builder.build())
    }
}





