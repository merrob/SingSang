package com.example.singsang

import android.animation.ObjectAnimator
import java.io.File
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.AssetManager
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.roomapp.data.BirdDatabase
import com.example.singsang.Adapter.SearchAdapter
import com.example.singsang.Data.Vogel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Runnable
import java.io.InputStream
import kotlin.properties.Delegates


class Hoeren : Fragment() {
    //create variables
    private var birdDatabase:BirdDatabase? = null
    lateinit var searchView: SearchView
    lateinit var hoerenView:View
    lateinit var vogelPrint:String
    lateinit var inputStream:InputStream
    lateinit var resourceId: Drawable
    lateinit var assetManager:AssetManager
    lateinit var vogelImage:String
    lateinit var vogelImageList: Array<String>
    var vogelImageMaxSize = 1
    var vogelImageListSize by Delegates.notNull<Int>()
    var familySelected:String = ""
    var mediaPlayer:MediaPlayer? = null
    var mSelectedItem: Int? = null
    var birdLife:String = ""
    var birdLifeCheckBox:String = ""
    var oldPosition:Int? = null
    var seekBar:SeekBar? = null
    lateinit var listView:ListView
    var imageNumber = 0




    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageNext = hoerenView.findViewById<FloatingActionButton>(R.id.imageNext)
        val imagePrevious = hoerenView.findViewById<FloatingActionButton>(R.id.imagePrevious)
        imageNext.visibility = GONE
        imagePrevious.visibility = GONE


        //checkbox
        val checkBox = view.findViewById<CheckBox>(R.id.birdLifeCheckBox)
        checkBox.setOnClickListener {
            if (birdLife == ""){birdLife = "1" }else{birdLife = ""}
            getNamesFromDb(birdLifeCheckBox, familySelected,birdLife)
        }
        val pictureCheckBox = view.findViewById<CheckBox>(R.id.showPicture)
        val pictureImage = view.findViewById<ImageView>(R.id.imageView2)
        val opts = BitmapFactory.Options()
        seekBar = hoerenView.findViewById(R.id.seekBarSound)

        opts.inDensity = DisplayMetrics.DENSITY_HIGH
        //val resourceId: Drawable? =Drawable.createFromStream(inputStream,null)
        /*val resourceId = requireContext().resources.getIdentifier(
            imgNme, "drawable",
            requireContext().packageName)*/
        pictureCheckBox.setOnClickListener {
            if(pictureCheckBox.isChecked){
                 resourceId =Drawable.createFromStream(inputStream,null)
                Log.d("imageviewDebug","pictureCheckBox is activated")
                pictureImage.setImageDrawable(resourceId)
                imageNext.visibility = VISIBLE
                imagePrevious.visibility = VISIBLE

            }else{pictureImage.setImageDrawable(null)
                imageNext.visibility = GONE
                imagePrevious.visibility = GONE}
        }
        //spinner functions
        val familyNames = resources.getStringArray(R.array.FamilienNamen)
        val spinner = view.findViewById<Spinner>(R.id.spinner)

            if(spinner != null){
            val adapter = activity?.let {
                ArrayAdapter(
                    it,
                    android.R.layout.simple_spinner_item,
                    familyNames)
            }
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener{
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if(position==0){
                        familySelected = ""
                        Log.d("spinnerdebug","$familySelected")
                    }else{
                        familySelected = familyNames[position]
                        getNamesFromDb("", familySelected,birdLife)
                        Log.d("spinnerdebug","$familySelected")}
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        }

        //searchview functions
        searchView =view.findViewById(R.id.search)
            searchView.setOnQueryTextListener(object:SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String): Boolean {
                getNamesFromDb(query, familySelected,birdLife)

                return true
            }
            override fun onQueryTextChange(newText: String): Boolean {
                getNamesFromDb(newText, familySelected,birdLife)
                birdLifeCheckBox = newText
                return true
            }
        })



        //buttons
        imageNext.setOnClickListener{
            if(vogelImageListSize > 1 && imageNumber<vogelImageMaxSize){
                pictureImage.setImageDrawable(null)
                imageNumber += 1
                inputStream = assetManager.open("Images/${vogelImage}/${vogelImageList[imageNumber]}")
                resourceId =Drawable.createFromStream(inputStream,null)
                Log.d("imageviewDebug","nextpicture")
                pictureImage.setImageDrawable(resourceId)
            }
        }
        imagePrevious.setOnClickListener {
            if(vogelImageListSize > 1 && imageNumber>0){
                pictureImage.setImageDrawable(null)
                imageNumber -= 1
                inputStream = assetManager.open("Images/${vogelImage}/${vogelImageList[imageNumber]}")
                resourceId =Drawable.createFromStream(inputStream,null)
                Log.d("imageviewDebug","previouspicture")
                pictureImage.setImageDrawable(resourceId)
            }

        }


        val playButton = hoerenView.findViewById<Button>(R.id.buttonPlay)
        playButton.setOnClickListener {
            Log.d("spinnerdebug","listen")
            if(mediaPlayer== null){
            var vogelCorrect = vogelPrint.lowercase()
                .replace("ä","ae")
                .replace("ö","oe")
                .replace("ü","ue")
            Log.d("spinnerdebug",vogelCorrect)

            val res = this.resources
            var soundId = res.getIdentifier(vogelCorrect,"raw",requireContext().packageName)
            Log.d("spinnerdebug", soundId.toString())

            mediaPlayer= MediaPlayer.create(activity, soundId)
            mediaPlayer!!.start()
                initialiseSeekbar()
                Log.d("spinnerdebug","mediaplayer")}


        else{Log.d("spinnerdebug","mediaplayer already exists")}}

        val stopButton = hoerenView.findViewById<Button>(R.id.buttonStop)
        stopButton.setOnClickListener {
            if(mediaPlayer!=null){
            mediaPlayer!!.stop()
            mediaPlayer = null }
        else{Log.d("spinnerdebug","mediaplayer does not exist")}}

    }

    //functions
    fun getNamesFromDb(searchText:String, searchGruppe:String,birdLifeQuery:String){
        Log.d("addViewdebug","getNamesFromDb started")
        val searchBirdLife = "%$birdLifeQuery%"
        val searchTextQuery = "%$searchText%"
        val searchGruppeQuery = "%$searchGruppe%"
         listView = activity?.findViewById(R.id.listView) as ListView
        listView.setOnItemClickListener(object: AdapterView.OnItemClickListener{
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (oldPosition != null){
                    if( oldPosition != position) {
                        listView[oldPosition!!].setBackgroundColor(
                            getColor(
                                requireContext(),
                                R.color.design_default_color_background
                            )
                        )
                    }
                }
                 assetManager = resources.assets
                var vogelSelected =listView.getItemAtPosition(position) as Vogel
                vogelPrint = vogelSelected.word
                 vogelImage = vogelSelected.word

                 vogelImageList = assetManager.list("Images/${vogelImage}") as Array<String>
                 vogelImageListSize = vogelImageList.size
                vogelImageMaxSize = vogelImageListSize -1

                Log.d("imageDebug", vogelImageListSize.toString())
                Log.d("imageDebug", vogelImageList[0])
                inputStream = assetManager.open("Images/${vogelImage}/${vogelImageList[0]}")

                 oldPosition = position
                if (view != null) {
                    view.isSelected = true
                }
            }


        })
        birdDatabase!!.birdDao().readAllData(searchTextQuery, searchGruppeQuery,searchBirdLife)
            .observe(viewLifecycleOwner,object : Observer<List<Vogel>>{
                override fun onChanged(t: List<Vogel>?) {
                    if(t==null){
                        return
                    }
                    Log.d("addViewdebug","Searchadapter begins")
                    val adapter =  SearchAdapter(hoerenView.context,R.layout.listview_layout,t)
                    listView.adapter = adapter

                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        hoerenView = inflater.inflate(R.layout.fragment_hoeren, container, false)
        Log.d("addView","onCreateView")
        birdDatabase = BirdDatabase.getDatabase(hoerenView.context)
        Log.d("addView","birdDatabase get")
        // Inflate the layout for this fragment
        Log.d("addView","hoerenView about to be returned")
        return hoerenView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if(mediaPlayer!= null){
            mediaPlayer!!.stop()
            mediaPlayer = null
        }
    }
    private fun initialiseSeekbar(){
        val seekDuration =mediaPlayer!!.duration
        seekBar!!.max = mediaPlayer!!.duration
        Log.d("seekBarDebug","$seekDuration")
        val handler = Handler()
        handler.postDelayed(object :Runnable{
            override fun run() {
                try {
                    seekBar!!.progress = mediaPlayer!!.currentPosition
                    Log.d("seekBarDebug","loop is running")
                    handler.postDelayed(this, 1000)
                }catch (e:Exception){
                    seekBar!!.progress = 0}
            }
        },0)

    }
}









