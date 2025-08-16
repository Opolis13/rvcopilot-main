package com.example.rvcopilot.ui.user

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rvcopilot.R
import com.example.rvcopilot.ui.components.TopBar
import com.example.rvcopilot.data.UserRepository
import com.example.rvcopilot.model.UserViewModel
import com.example.rvcopilot.ui.theme.Azure
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import com.google.accompanist.systemuicontroller.rememberSystemUiController


/**
 * ----viewModel exposes the state to the ui,
 * it persists the state through config changes.
 * viewModel handles user actions and updates the state
 * the updated state is fed back to the ui to render.
 * Note: this viewModel works with 'CluesViewModel', 'CluesUiState' and 'CluesApp'
 * (which contain the use case class) to get data and transform
 * it into the ui state.  (data class --> viewModel --> ui elements)
 * https://developer.android.com/jetpack/androidx/releases/collection?hl=en
 * https://developer.android.com/topic/architecture/ui-layer
 *
 * --state variables
 * 'by' is used to delegate the get and set operations to state object
 * 'remember' ensures that the state value persists across recompositions
 * 'false' is used to make sure that all the buttons start out with an initial state of not selected.
 *
 * --coroutines
 * this is related to the Composable lifecycle.
 * it is used to delay the action of the button so that the highlighting is visible
 *
 * --context
 * an interface that provides access to application-specific
 * resources and system services.
 * for example: manage ui related components dynamically,
 * accessing string resources.
 * - stringResource() requires 'context'
 * - openUrl(context, url) uses the 'Intent.ACTION_VIEW'
 * to launch on external browser.
 */

/**
 * @Composable marks this function as a
 * Jetpack Compose User Interface function
 *
 * some functions don't allow try-catch within the composable because
 *  * there may be a deferred function such as images (doesn't run immediately)
 * */

/**
 * () -> Unit:  Lambda expression.  ()= function does not accept arguments
 * Unit = 'Void' expression like used in C99.  Function does not return anything.
 * onStartClicked: () -> Unit.   This is a callback for the start button
 * */
/**
 * Scaffold accepts the topBar composable as a parameter
 * topBar needs Scaffold to run
 * Scaffold also accepts different parts of the user interface ui,
 * in this case topBar with a title, onBackClick
 * see this url for an example:
 * https://developer.android.com/develop/ui/compose/components/scaffold
 *
 * */


@Composable
fun CreateAccountScreen(
    onBackClick: () -> Unit,
    onAccountCreated: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val userRepository = remember { UserRepository(FirebaseFirestore.getInstance()) }
    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModel.provideFactory(userRepository)
    )
    val scope = rememberCoroutineScope()

    val isDarkTheme = isSystemInDarkTheme()
    val backgroundRes = if (isDarkTheme) R.drawable.bgnight else R.drawable.bgday
    val systemUiController = rememberSystemUiController()

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = !isDarkTheme
        )
    }

    Scaffold(
        topBar = {
            TopBar(title = "Create Account", onBackClick = onBackClick)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)   // creates the insets created by the Scaffold topbar
            // .padding (paddingValues) ensures topbar is displayed in the correct visable area of screen
        ) {
            Image(
                painter = painterResource(id = backgroundRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .align(Alignment.Center)
                    .background(Color(0xFFFAFAF8).copy(alpha = 0.96f), RoundedCornerShape(16.dp))
                    .padding(24.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Welcome Aboard!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3F51B5)
                    )

                    Text(
                        text = "Create your account to start planning RV adventures.",
                        fontSize = 15.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Choose a Username") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF3F51B5),
                            unfocusedBorderColor = Color.Gray,
                            cursorColor = Color(0xFF3F51B5),
                            focusedLabelColor = Color(0xFF3F51B5),
                            unfocusedLabelColor = Color.DarkGray
                        )
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Choose a Password") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF3F51B5),
                            unfocusedBorderColor = Color.Gray,
                            cursorColor = Color(0xFF3F51B5),
                            focusedLabelColor = Color(0xFF3F51B5),
                            unfocusedLabelColor = Color.DarkGray
                        )
                    )

                    Button(
                        onClick = {
                            if (username.isBlank() || password.isBlank()) {
                                errorMessage = "Please enter both fields"
                            } else {
                                scope.launch {
                                    userViewModel.findUserByUsername(username) { existingUser ->
                                        if (existingUser != null) {
                                            errorMessage = "Username already exists"
                                        } else {
                                            userViewModel.addUser(
                                                username = username,
                                                password = password,
                                                userBio = ""
                                            )
                                            errorMessage = null
                                            Toast.makeText(
                                                context,
                                                "Account created successfully!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            onAccountCreated()
                                        }
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3F51B5),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Create Account",
                            color = Color(0xFFffffff),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold)
                    }

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = Color.Red,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}