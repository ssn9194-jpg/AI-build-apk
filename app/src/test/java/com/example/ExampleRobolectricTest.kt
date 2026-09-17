package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Gemini Studio", appName)
  }

  @Test
  fun `apk builder generates valid apk file`() = kotlinx.coroutines.runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val builder = com.example.data.apk.ApkBuilderService(context)
    val project = com.example.data.templates.AppTemplates.ZEN_TASKS
    val result = builder.buildApk(project)
    assertEquals(true, result.success)
    org.junit.Assert.assertNotNull(result.apkFile)
    org.junit.Assert.assertTrue(result.apkFile!!.exists())
    org.junit.Assert.assertTrue(result.apkFile!!.length() > 0)
  }
}
