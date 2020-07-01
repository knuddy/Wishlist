package com.example.knuddj1wishlist.helpers

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Environment
import android.widget.ImageView
import androidx.core.content.FileProvider
import com.example.knuddj1wishlist.BuildConfig
import kotlinx.android.synthetic.main.activity_new_wishlist_item.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class ImageCapture(val context: Context) {
    lateinit var imgFile: File
    lateinit var imgUri: Uri

    fun prepare(): Uri {
        imgFile = Image.create()
        imgUri = FileProvider.getUriForFile(
            context,
            BuildConfig.APPLICATION_ID + ".provider",
            imgFile
        )
        return imgUri
    }

    object Image {
        private lateinit var name: String

        private val timeStamp: String
            get(){
                val outputPattern = "yyyyMMddHHmmss"
                val outputFormat = SimpleDateFormat(outputPattern, Locale.ENGLISH)
                val currentTime = Date()
                return outputFormat.format(currentTime)
            }

        fun create(): File {
            val rootPath: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val storageDir = File(rootPath, "camera")
            if (!storageDir.exists())
                storageDir.mkdirs()
            name = "img_${timeStamp}.jpg"
            return File(storageDir.path + File.separator + name)
        }
    }

    object BitmapProcessor {
        fun process(imageFile: String): Bitmap {
            val photoBitmap: Bitmap = BitmapFactory.decodeFile(imageFile)
            return scale(photoBitmap)
        }

        fun scale(bitmap: Bitmap): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(90F)
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }

        fun convertBitmapToDrawable(resources: Resources, bitmap: Bitmap): BitmapDrawable {
            return BitmapDrawable(resources, bitmap)
        }

        fun setImageToImageView(filePath: String, imv: ImageView, context: Context){
            val bitmap: Bitmap = ImageCapture.BitmapProcessor.process(filePath)
            val bitmapDrawable: BitmapDrawable = ImageCapture.BitmapProcessor.convertBitmapToDrawable(context.resources, bitmap)
            imv.setImageDrawable(bitmapDrawable)
        }
    }
}