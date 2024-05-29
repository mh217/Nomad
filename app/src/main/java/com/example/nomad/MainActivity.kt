package com.example.nomad

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nomad.ui.theme.NomadTheme
import com.example.nomad.ui.theme.justanotherhand
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth


class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            val navController = rememberNavController()
            //val currentUser = FirebaseAuth.getInstance().currentUser
            NomadTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(navController = navController, startDestination = "first_screen") {
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
                    }

                    }

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
                label = { Text("Name") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = surname,
                onValueChange = { surname = it },
                label = { Text("Surname") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") }
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