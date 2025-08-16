package com.example.rvcopilot.ui.user

import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rvcopilot.R
import com.example.rvcopilot.ui.components.TopBar
import com.example.rvcopilot.model.UserViewModel
import com.example.rvcopilot.ui.theme.Azure

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
fun UserBioScreen(
    userViewModel: UserViewModel,
    onSaveClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val currentUser by userViewModel.currentUser.collectAsState()
    LaunchedEffect(currentUser) {
        println("UserBioScreen: currentUser loaded = $currentUser")
    }
    // Elvis: when no bio is available then outline box shows empty text
    val savedBio = currentUser?.userBio ?: ""



    //var userBio by remember { mutableStateOf("") }
    /**
     * user is loaded after the screen starts
     * so this must be launched after the screen starts
     *
     * if the currentUser changes after a save
     * userBio will automatically reset to match the latest bio
     * */
    var userBio by remember(currentUser) {
        mutableStateOf(currentUser?.userBio ?: "")
    }

    LaunchedEffect(savedBio) {
        userBio = savedBio
    }



    Scaffold(
        topBar = {
            Box {
                Image(
                    painter = painterResource(id = R.drawable.rv_avatar_3252),
                    contentDescription = "TopBar Background",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )
                TopBar(
                    title = "Edit Your Bio",
                    onBackClick = onBackClick
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            //Background image for screen body
            Image(
                painter = painterResource(id = R.drawable.rv_avatar_3244),
                contentDescription = "Background Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            //Foreground content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Your Bio",
                    fontSize = 26.sp,
                    color = Color.White
                )

                OutlinedTextField(
                    value = userBio,
                    onValueChange = { userBio = it },
                    label = { Text("Tell us about yourself") },
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 4.dp,
                            color = Azure,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .height(260.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (currentUser != null) {
                            onSaveClick(userBio)
                            Toast.makeText(context, "Bio saved!", Toast.LENGTH_SHORT).show()
                        } else {
                            println("can't update bio, currentUser is null")
                        }
                   },
                    colors = ButtonDefaults.buttonColors(containerColor = Azure),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Save Bio", fontSize = 18.sp, color = Color.White)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text( // display the current bio in the database
                    text = "Saved Bio: $savedBio",
                    fontSize = 22.sp,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    }
}