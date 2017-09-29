package syk.aayushf.syk

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.content_profile.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.util.*
import kotlinx.android.synthetic.main.content_profile.*
import android.provider.MediaStore
import android.content.Intent
import android.graphics.Bitmap





class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        fab.onClick{
            val sp = getSharedPreferences("SHARED_PREFS", Context.MODE_PRIVATE)
            sp.edit().putString("NAME_PREF", nameet.text.toString()).commit()
            sp.edit().putString("ID_PREF", UUID.randomUUID().toString().substring(0,7))



        }
        snap_fab.onClick {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(takePictureIntent, 0)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            val extras = data.extras
            val imageBitmap = extras.get("data") as Bitmap
            iv_profile.setImageBitmap(imageBitmap)
        }
    }

}
