package dangor.photobooth.extensions

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.FileNotFoundException
import java.io.IOException

class Bitmaps {
    companion object {
        private const val MAX_IMAGE_SIZE = 1024
        private const val SCALED_IMAGE_DENSITY = 96

        /**
         * getBitmapSampleSize returns the largest power of 2 that could be used to
         * scale the image such that at least one dimension is larger than [MAX_IMAGE_SIZE]
         */
        private fun getBitmapSampleSize(contentResolver: ContentResolver,
                                        photo: Uri) : Int {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            decodeBitmap(contentResolver, photo, options)

            val factor: Int = (1..100).first { factor ->
                val sampleFactor = factor * factor

                options.outHeight / sampleFactor < MAX_IMAGE_SIZE &&
                        options.outWidth / sampleFactor < MAX_IMAGE_SIZE
            } - 1

            return maxOf(1, factor * factor)
        }

        fun getScaledBitmap(context: Context, photo: Uri): Bitmap {
            val bitmapOptions = BitmapFactory.Options()
            val contentResolver = context.contentResolver
            bitmapOptions.inDensity = SCALED_IMAGE_DENSITY
            bitmapOptions.inSampleSize = getBitmapSampleSize(contentResolver, photo)

            val bitmap = decodeBitmap(contentResolver, photo, bitmapOptions) ?: throw BitmapIOException(photo, "Unable to decode bitmap stream.")
            if (bitmap.width == 0 || bitmap.height == 0) throw BitmapIOException(photo, "Bitmap width or height is 0.")

            val scalingRatio =
                    if (bitmap.width > bitmap.height) Math.min(MAX_IMAGE_SIZE.toDouble() / bitmap.width, 1.0)
                    else Math.min(MAX_IMAGE_SIZE.toDouble() / bitmap.height, 1.0)

            return Bitmap.createScaledBitmap(bitmap,
                    Math.round(bitmap.width * scalingRatio).toInt(),
                    Math.round(bitmap.height * scalingRatio).toInt(),
                    true)
        }

        private fun decodeBitmap(contentResolver: ContentResolver, photo: Uri, bitmapOptions: BitmapFactory.Options): Bitmap? {
            return try {
                contentResolver.openInputStream(photo).use {
                    BitmapFactory.decodeStream(it, null, bitmapOptions)
                }
            } catch (e: FileNotFoundException) {
                throw BitmapIOException(photo, "Could not open input stream.", e)
            }
        }
    }
}

class BitmapIOException(uri: Uri, message: String, cause: Throwable? = null)
    : IOException("$message  Uri=$uri  Cause=${cause?.localizedMessage}", cause)