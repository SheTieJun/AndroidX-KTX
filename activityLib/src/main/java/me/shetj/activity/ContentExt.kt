/*
 * MIT License
 *
 * Copyright (c) 2021 SheTieJun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.shetj.activity

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


/**
 * 创建一条图片地址uri,用于保存拍照后的照片
 * 兼容核心就是这里
 * @param context
 * @return 图片的uri
 */
fun createImagePathUri(context: Context): Uri {
    return when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
            createImageUri(context)
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
            val file = File(createImagePath(context))
            FileProvider.getUriForFile(
                context.applicationContext,
                context.packageName + ".FileProvider",
                file
            )
        }
        else -> {
            val file = File(createImagePath(context))
            Uri.fromFile(file)
        }
    }
}

fun createImagePath(context: Context): String {
    val timeFormatter = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA)
    val time = System.currentTimeMillis()
    val imageName = timeFormatter.format(Date(time))
    return getPath(
        root = context.getFilesDir(Environment.DIRECTORY_PICTURES),
        packagePath = "image"
    ) + "/" + imageName + ".jpg"
}

fun createImageUri(context: Context): Uri {
    return context.contentResolver.insert(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        ContentValues()
    ) ?: throw NullPointerException("create createImageUri fail")
}


fun createVideoPathUri(context: Context): Uri {
    return when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
            createVideoUri(context)
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
            val file = File(createVideoPath(context))
            FileProvider.getUriForFile(
                context.applicationContext,
                context.packageName + ".FileProvider",
                file
            )
        }
        else -> {
            val file = File(createVideoPath(context))
            Uri.fromFile(file)
        }
    }
}



fun createVideoUri(context: Context): Uri {
    return context.contentResolver.insert(
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
        ContentValues()
    ) ?: throw NullPointerException("create createImageUri fail")
}

fun getPath(root: String, packagePath: String): String {
    val path = StringBuilder(root)
    val f = packagePath.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    for (aF in f) {
        val dirFile = File("$path/$aF")
        if (!dirFile.exists()) {
            dirFile.mkdir()
        }
        path.append("/").append(aF)
    }
    return path.toString()
}


fun createVideoPath(context:Context): String {
    val timeFormatter = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA)
    val time = System.currentTimeMillis()
    val imageName = timeFormatter.format(Date(time))
    return getPath(
        root = context.getFilesDir(Environment.DIRECTORY_PICTURES),
        packagePath = "video"
    ) + "/" + imageName + ".mp4"
}


/**
 * mnt/sdcard/Android/data/< package name >/files/type
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"></uses-permission>
 * <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"></uses-permission>
 *   type =   {{@link android.os.EnvironmentDIRECTORY_MUSIC}},
 *            {@link android.os.Environment#DIRECTORY_PODCASTS},
 *            {@link android.os.Environment#DIRECTORY_RINGTONES},
 *            {@link android.os.Environment#DIRECTORY_ALARMS},
 *            {@link android.os.Environment#DIRECTORY_NOTIFICATIONS},
 *            {@link android.os.Environment#DIRECTORY_PICTURES}, or
 *            {@link android.os.Environment#DIRECTORY_MOVIES}. or null
 */
fun Context.getFilesDir(type: String = Environment.DIRECTORY_DOWNLOADS): String {
    val file: File? = getExternalFilesDir(type)
    return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() && file != null) {
        file.absolutePath
    } else {
        filesDir.toString() + File.separator + type
    }
}