package com.example.codesturadmin

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class add_video : AppCompatActivity() {
    var uri_image: Uri? = null
    var uri_pdf:Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_video)
        var storageref: FirebaseStorage
        storageref= FirebaseStorage.getInstance()
        val playlist=findViewById<Spinner>(R.id.playlist)
        var demo:String ="Select you Playlist"
        val db=Firebase.firestore
        val playlists:MutableList<String> = mutableListOf()
        db.collection("CodeStur").get().addOnSuccessListener {
            for(i in it){
                playlists.add(i.id.toString())
            }
            val adap= ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,playlists)

            playlist.adapter=adap
            findViewById<ProgressBar>(R.id.progi2).visibility=View.GONE
            playlist.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    demo = playlists[p2]
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {
                    demo="Select your Playlist"
                }

            }
        }

        var add_pdf_button=findViewById<Button>(R.id.notes_upload)
        add_pdf_button.setOnClickListener{
            view: View? -> val intent= Intent()
            intent.setType("pdf/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent,"Select PDF"),1)
        }
        var add_image=findViewById<Button>(R.id.thumbnail_upload)
        add_image.setOnClickListener {
                view: View? -> val intent= Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent,"Select IMAGE"),0)
        }
        Log.d(TAG,"${uri_image}")
        findViewById<Button>(R.id.final_upload_video).setOnClickListener {
            findViewById<ProgressBar>(R.id.progi3).visibility=View.VISIBLE
            findViewById<Button>(R.id.final_upload_video).visibility=View.GONE
            val title= findViewById<EditText>(R.id.title).text.toString()
            val tags=findViewById<EditText>(R.id.tags).text.toString()
            val link=findViewById<EditText>(R.id.link).text.toString()
            if(title=="" || tags=="" || link=="" || uri_image==null || demo=="" || uri_pdf==null){
                Toast.makeText(this,"All Fields are Compulsory",Toast.LENGTH_SHORT).show()
                findViewById<ProgressBar>(R.id.progi3).visibility=View.GONE
                findViewById<Button>(R.id.final_upload_video).visibility=View.VISIBLE
            }
            else{
                var image_URL =  ""
                var pdf_URL:String = ""
                storageref.getReference("ThumbNails").child(title).putFile(uri_image!!).addOnSuccessListener {
                    it.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                        Toast.makeText(this,"Thumbnail Uploaded",Toast.LENGTH_SHORT).show()
                        image_URL= it.toString()

                        storageref.getReference("Notes").child(title).putFile(uri_pdf!!).addOnSuccessListener {
                            it.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                                pdf_URL= it.toString()
                                Toast.makeText(this,"PDF Uploaded",Toast.LENGTH_SHORT).show()
                                if(image_URL=="" || pdf_URL==""){
                                    Toast.makeText(this,"Uploading Task Failed. Try Again",Toast.LENGTH_SHORT).show()
                                }
                                else{
                                    var x:HashMap<String,HashMap<String,String>>
                                    db.collection("CodeStur").document(demo).get().addOnSuccessListener {
                                        x=it.data as HashMap<String, HashMap<String, String>>
                                        val hashi: HashMap<String,String> = hashMapOf(
                                            "Title" to title,
                                            "Tags" to tags,
                                            "Video_Link" to link,
                                            "PDF_Link" to pdf_URL,
                                            "Thumbnail_Link" to image_URL
                                        )
                                        x[title]=hashi
                                        db.collection("CodeStur").document(demo).set(x).addOnSuccessListener {
                                            Toast.makeText(this,"Video Uploaded Successfully",Toast.LENGTH_SHORT).show()
                                            startActivity(Intent(this,MainActivity::class.java))
                                            finish();
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode== RESULT_OK){
            if(requestCode==1){
                uri_pdf= data!!.data!!
                Log.d(TAG,"${data!!.dataString}")
                val x=data!!.data.toString()
                var file_name:String=""
                for (i in x.length - 1 downTo 0) {
                    if(x[i]=='/'){
                        break
                    }
                    file_name+=x[i]
                }
                file_name=file_name.reversed()
                findViewById<TextView>(R.id.pdf_name).setText(file_name)
            }
            if(requestCode==0){
                uri_image= data!!.data!!
                Log.d(TAG,"${uri_image}")
                findViewById<ImageView>(R.id.thumbnail_image).setImageURI(uri_image)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}