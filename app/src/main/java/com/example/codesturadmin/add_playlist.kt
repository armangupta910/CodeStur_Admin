package com.example.codesturadmin

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class add_playlist : AppCompatActivity() {
    var image_uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_playlist)
        var storageref: FirebaseStorage
        storageref= FirebaseStorage.getInstance()

        var add_image=findViewById<Button>(R.id.thumbnail_upload)
        add_image.setOnClickListener {
                view: View? -> val intent= Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent,"Select IMAGE"),0)
        }
        val db=Firebase.firestore
        findViewById<Button>(R.id.create_playlist).setOnClickListener {
            val title=findViewById<EditText>(R.id.title).text.toString()
            val tags=findViewById<EditText>(R.id.tags).text.toString()
            if(title=="" || tags=="" || image_uri==null){
                Toast.makeText(this,"Empty Fields not Allowed",Toast.LENGTH_SHORT).show()
            }
            else{
                var Banner = ""
                findViewById<ProgressBar>(R.id.progi1).visibility=View.VISIBLE
                findViewById<Button>(R.id.create_playlist).visibility=View.GONE

                storageref.getReference("Playlist Banners").child(title).putFile(image_uri!!).addOnSuccessListener{
                    it.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                        Banner=it.toString()
                        if(Banner==""){
                            Toast.makeText(this,"Uploading Task Failed",Toast.LENGTH_SHORT).show()
                        }
                        else{
                            var hashi:HashMap<String,HashMap<String,String>> = hashMapOf()
                            hashi["Banner"] = hashMapOf("Banner" to Banner)
                            hashi["Tags"]= hashMapOf("Tags" to tags)
                            db.collection("CodeStur").document(title).set(hashi).addOnSuccessListener {
                                Toast.makeText(this,"Playlist created successfully",Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this,MainActivity::class.java))
                                finish();
                            }.addOnFailureListener {
                                Toast.makeText(this,"Some error ocurred",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }


            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode== RESULT_OK){
                image_uri= data!!.data!!
                Log.d(ContentValues.TAG,"${image_uri}")
                findViewById<ImageView>(R.id.thumbnail_image).setImageURI(image_uri)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}