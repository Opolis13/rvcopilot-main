package com.example.rvcopilot.data

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import com.example.rvcopilot.ui.components.imageList
import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore  // for storing picture data
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await  // makes Firebase calls suspendable
import kotlinx.coroutines.channels.awaitClose
import android.graphics.drawable.BitmapDrawable
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import com.example.rvcopilot.ui.components.tripImageList


class PictureRepository(
    private val db: FirebaseFirestore,
) {
    enum class PictureCategory {
        TRIP, CAMPSITE
    }


    /**
     * ***Helper function for conversion of drawable resource file to URI***
     *
     *
     * --(drawable as? BitmapDrawable)?.bitmap
     * casting the drawable object into a BitMapDrawable
     * as?:  if the drawable is a BitMapDrawable,
     * the cast succeeds resulting in a BitMapDrawable Object
     * otherwise returns a null.
     *
     * ?.bitmap
     * if casting successful then this accesses the .bitmap property in BitMapDrawable
     *
     * ?: Elvis operator
     * if previous expression drawable not a BitMapDrawable
     * Elvis operator causes throw an IllegalArgumentException
     *
     * https://developer.android.com/reference/android/provider/MediaStore.Images.Media
     *
     * example:
     * https://github.com/LHM777/Scoped-Storage-Android-11-java-example-Save-bitmap-in-Android-using-MediaStore/blob/main/app/src/main/java/com/example/saveimage2021/MainActivity.java
     * reference the code inside of try-catch block
     *
     * old deprecated:
     *         val path = MediaStore.Images.Media.insertImage( //insertImage was deprecated
     *             context.contentResolver, bitmap, "TempImage", null
     *         )
     *
     *
     * */

    fun getImageUriFromDrawable(context: Context, drawableResId: Int): Uri? {
        val resources = context.resources
        val drawable = resources.getDrawable(drawableResId, null)

        // see explanation above
        val bitmap = (drawable as? BitmapDrawable)?.bitmap
            ?: throw IllegalArgumentException("Drawable must be a BitmapDrawable")
        // save the bitmap into the device photo gallery
        // see link above for reference
        // inserting into MediaStore.  the filepath string is returned
        val filename = "TempImage_${System.currentTimeMillis()}.jpg"
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        if (uri != null) {
            // only runs if openOutputStream(uri)? is not a null
            resolver.openOutputStream(uri)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
            contentValues.clear()
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(uri, contentValues, null, null)
        }
        Toast.makeText(context, "Image BITMAP saved!", Toast.LENGTH_SHORT).show()

        return uri

    }



    /**
     * insert a picture
     * store pre uploaded image URL
     *
     * */
    suspend fun insertPicture(
        picture: Pictures,
        category: PictureCategory
    ) {
        val collectionName = when (category) {
            PictureCategory.TRIP -> "tripPictures"
            PictureCategory.CAMPSITE -> "campsitePictures"
        }

        try {

            db.collection(collectionName)
                .add(picture)
                .await()
        } catch (e: Exception) {
            println("insertPicture failed: ${e.localizedMessage}")
        }
    }

    /**
     * Realtime updates to the user interface
     *
     * */

    fun getAllPictures(category: PictureCategory): Flow<List<Pictures>> = callbackFlow {
        val collection = if (category == PictureCategory.TRIP) "tripPictures" else "campsitePictures"
        val listener = db.collection(collection)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val pictures = snapshot?.documents?.mapNotNull {
                    it.toObject(Pictures::class.java)
                } ?: emptyList()

                trySend(pictures)
            }
        awaitClose { listener.remove() }
    }

    /**
     * delete a picture from the Firebase Database
     * by matching the label and the URI
     * - background function
     * */

    suspend fun deletePicture(
        picture: Pictures,
        context: Context,
        category: PictureCategory
    ) {
        val collectionName = when (category) {
            PictureCategory.TRIP -> "tripPictures"
            PictureCategory.CAMPSITE -> "campsitePictures"
        }


        try {
            println("Attempting to delete picture: Label=${picture.label}, URI=${picture.imageUri}")
            println("Deleting from collection: $collectionName (category = $category)")

            val snapshot = db.collection(collectionName)
                //.whereEqualTo("label", picture.label)
                .whereEqualTo("imageUri", picture.imageUri)
                .get()
                .await()
            if (snapshot.isEmpty) {
                Toast.makeText(context, "Repository: No matching picture found.", Toast.LENGTH_SHORT).show()
                println("No matching documents found to delete.")
            } else {

                for (document in snapshot.documents) {
                    document.reference.delete().await()
                    Toast.makeText(context, "Repository: Picture deleted from Firebase", Toast.LENGTH_SHORT).show()
                    println("Deleted document ID: ${document.id}, label: ${picture.label}")
                }
            }
        } catch (e: Exception) {
            println("deletePicture failed: ${e.localizedMessage}")
        }
    }
    /**
     * seeds initial images stored in the resource directory
     *
     * uses existing image list file but limits number of images
     *
     *  - background function
     * */
    suspend fun seedPictures(context: Context) {
        try {
            val categories = listOf(
                "campsitePictures" to imageList,
                "tripPictures" to tripImageList
            )
            for ((collectionName, imageGroup) in categories) {
                val maxSeedLimit = imageGroup.size
                //val combinedList = imageList + tripImageList
                //val maxSeedLimit = combinedList.size
                if (maxSeedLimit <= 0) {
                    println("No images to seed in $collectionName (limit too low).")
                    return
                }

                // get existing images from Firestore database
                val existingSnapshots = db.collection(collectionName)
                    .get()
                    .await()

                val existingLabels =
                    existingSnapshots.documents.mapNotNull { it.getString("label") }.toSet()
                val existingImageUris =
                    existingSnapshots.documents.mapNotNull { it.getString("imageUri") }.toSet()


                // limit the number of images uploaded from list
                val limitedImageList = imageGroup.take(maxSeedLimit)

                for (resId in limitedImageList) {
                    // skip if null image using 'continue'
                    val name = context.resources.getResourceEntryName(resId) ?: continue

                    val label = name
                        .replace('_', ' ')
                        .replace("avatar", "Avatar")
                        .replace("trip", "Trip")
                        .replaceFirstChar { it.uppercase() }

                    // check if label or imageUri are blank
                    if (label.isBlank() || name.isBlank()) {
                        println("Skipped invalid picture: Label or URI is blank")
                        continue
                    }

                    // check if label or imageUri already exist in database
                    if (label !in existingLabels && name !in existingImageUris) {
                        val picture = Pictures(label = label, imageUri = name)
                        //insertPicture(picture)
                        //println("Seeded picture: $label")
                        db.collection(collectionName).add(picture).await()
                        println("Seeded picture in $collectionName: $label")
                    } else {
                        //println("Skipped existing picture: $label")
                        println("Skipped existing picture in $collectionName: $label")
                    }
                }
            }
        } catch (e: Exception) {
            println("seedPictures failed: ${e.localizedMessage}")
        }
    }

    /**
     * *********************************************************
     * gets firebase directory pictures for trips
     * ******************************************************
     * */

    fun getTripPictures(): Flow<List<Pictures>> = callbackFlow {
        val listener = db.collection("tripPictures")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val pictures = snapshot?.documents?.mapNotNull {
                    it.toObject(Pictures::class.java)
                } ?: emptyList()
                trySend(pictures)
            }
        awaitClose { listener.remove() }
    }
    /**
     * *********************************************************
     * gets firebase directory pictures for campsites
     * ******************************************************
     * */
    fun getCampsitePictures(): Flow<List<Pictures>> = callbackFlow {
        val listener = db.collection("campsitePictures")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val pictures = snapshot?.documents?.mapNotNull {
                    it.toObject(Pictures::class.java)
                } ?: emptyList()
                trySend(pictures)
            }
        awaitClose { listener.remove() }
    }

    /**
     * *********************************************************
     * inserts pictures for trips and campsites
     * ******************************************************
     * */
    suspend fun insertTripPicture(picture: Pictures) {
        db.collection("tripPictures").add(picture).await()
    }

    suspend fun insertCampsitePicture(picture: Pictures) {
        db.collection("campsitePictures").add(picture).await()
    }

}