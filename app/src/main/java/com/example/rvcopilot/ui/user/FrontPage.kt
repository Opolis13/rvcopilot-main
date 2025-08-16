package com.example.rvcopilot.ui.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rvcopilot.R
import com.example.rvcopilot.ui.components.TopBar
import com.example.rvcopilot.model.UserViewModel
import com.example.rvcopilot.ui.theme.Azure
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
 * - lambdas: anonymous function.  block of code that acts like a variable
 *  * () -> Unit:  Lambda expression.  ()= function does not accept arguments
 *  *  * Unit = 'Void' expression. Function does not return anything.
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
fun FrontPageScreen(
    userViewModel: UserViewModel,
    onBackClick: () -> Unit,
    onStartClick: () -> Unit,
    onCreateAccountClick: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundRes = if (isDarkTheme) R.drawable.bgnight else R.drawable.bgday
    val useDarkIcons = !isSystemInDarkTheme()

    Scaffold(
        topBar = {}
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Background
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

            // Login Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .align(Alignment.Center)
                    .background(Color.White.copy(alpha = 0.91f), RoundedCornerShape(16.dp))
                    .padding(24.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Log in",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF340B9D)
                    )

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username or Email") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF3F51B5),
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color(0xFF020000),
                            unfocusedLabelColor = Color.Gray,
                            cursorColor = Color(0xFF3F51B5)
                        )
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF3F51B5),
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color(0xFF020000),
                            unfocusedLabelColor = Color.Gray,
                            cursorColor = Color(0xFF3F51B5)
                        )
                    )

                    if (loginError) {
                        Text(
                            text = "Invalid username or password",
                            color = Color.Red,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = {
                            if (username.isNotBlank() && password.isNotBlank()) {
                                scope.launch {
                                    userViewModel.findUserByUsername(username) { user ->
                                        if (user?.password == password) {
                                            userViewModel.setCurrentUser(user)
                                            loginError = false
                                            onStartClick()
                                        } else {
                                            loginError = true
                                        }
                                    }
                                }
                            } else {
                                loginError = true
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
                        Text("Login", style = MaterialTheme.typography.bodyLarge, color = Color.White)
                    }

                    TextButton(
                        onClick = onCreateAccountClick
                    ) {
                        Text(
                            text = "Create an account",
                            fontSize = 16.sp,
                            color = Color(0xFF448AFF),
                            textDecoration = TextDecoration.Underline,
                            fontWeight = FontWeight.Bold

                        )
                    }
                }
            }
        }
    }
}



