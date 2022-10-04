package com.example.singsang

import android.content.Context
import android.content.res.AssetManager
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.core.view.size
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.roomapp.data.BirdDatabase
import com.example.singsang.Adapter.SearchAdapter
import com.example.singsang.Data.Vogel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.intellij.lang.annotations.Identifier
import java.io.InputStream
import java.util.EnumSet.range
import kotlin.properties.Delegates

class Sehen : Fragment() {
    companion object{
        var globalPosition = 0
        var globalVogelList:List<Vogel>? = null

    }
    private var birdDatabase:BirdDatabase? = null
    lateinit var searchView: SearchView
    lateinit var sehenView:View
    lateinit var listView:ListView
    lateinit var assetManager: AssetManager
    lateinit var resourceId: Drawable
    lateinit var vogelImage:String
    lateinit var vogelPrint:String
    lateinit var vogelImageList: Array<String>
    lateinit var inputStream: InputStream
    lateinit var imageNext:FloatingActionButton
    lateinit var vogelSearchAdapter:SearchAdapter
    lateinit var birdInformation:TextView
    lateinit var birdName:TextView
    var stringId by Delegates.notNull<Int>()
    var posFirst = 0
    var posLast = 15
    var posList =0
    var maxBirdListView by Delegates.notNull<Int>()
    var nextPosition by Delegates.notNull<Int>()
    var previusPosition by Delegates.notNull<Int>()
    lateinit var imagePrevious:FloatingActionButton
   lateinit var vogelSelected:Vogel
   lateinit var pictureImage:ImageView
    var vogelImageMaxSize = 1
    var vogelImageListSize by Delegates.notNull<Int>()
    var highlightedPosition = 0

    var showImage = 0

    var familySelected:String = ""

    var birdLife:String = ""
    var birdLifeCheckBox:String = ""
    var oldPosition:Int? = null
    var imageNumber = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sehenView = inflater.inflate(R.layout.fragment_sehen, container, false)
        Log.d("addView","onCreateView")
        birdDatabase = BirdDatabase.getDatabase(sehenView.context)
        Log.d("addView","birdDatabase get")
        // Inflate the layout for this fragment
        Log.d("addView","hoerenView about to be returned")
        return sehenView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tryView =sehenView.findViewById<TextView>(R.id.tvSearch)
        val nextBird = sehenView.findViewById<Button>(R.id.buttonNext)
        val previousBird = sehenView.findViewById<Button>(R.id.buttonPrevious)
        birdInformation = sehenView.findViewById(R.id.birdInformation)


        listView = activity?.findViewById(R.id.listViewSehen) as ListView




        imageNext = sehenView.findViewById<FloatingActionButton>(R.id.imageNext)
         imagePrevious = sehenView.findViewById<FloatingActionButton>(R.id.imagePrevious)
        imageNext.visibility = View.GONE
        imagePrevious.visibility = View.GONE

        //checkbox
        val showList = sehenView.findViewById<Button>(R.id.listViewCheckBox)
        val checkBox = view.findViewById<CheckBox>(R.id.birdLifeCheckBox)
        checkBox.setOnClickListener {
            if (birdLife == ""){birdLife = "1" }else{birdLife = ""}
            getNamesFromDb(birdLifeCheckBox, familySelected,birdLife)
        }
        showList.setOnClickListener {
            if(showImage == 0){
            showImage = 1
            pictureImage.bringToFront()
            listView.visibility = GONE
                birdName.visibility = VISIBLE
                birdInformation.visibility = VISIBLE
            }
            else{showImage = 0
                listView.visibility = VISIBLE
                birdName.visibility = INVISIBLE
                birdInformation.visibility = INVISIBLE
            listView.bringToFront()}
        }
        //spinner functions
        val familyNames = resources.getStringArray(R.array.FamilienNamen)
        val spinner = view.findViewById<Spinner>(R.id.spinnerSehen)

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
        searchView = view.findViewById(R.id.searchpicture)
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

        //listeners

        listView.setOnItemClickListener(object: AdapterView.OnItemClickListener{
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                globalPosition = position
                Log.d("viewListDebug","position is $globalPosition")
                assetManager = resources.assets
                vogelSelected =listView.getItemAtPosition(position) as Vogel
                vogelPrint = vogelSelected.word
                vogelImage = vogelSelected.word

                vogelImage = vogelImage.replace(" ","")
                vogelImageList = assetManager.list("Images/${vogelImage}") as Array<String>
                vogelImageListSize = vogelImageList.size
                vogelImageMaxSize = vogelImageListSize -1
                imageNumber = 0



                inputStream = assetManager.open("Images/${vogelImage}/${vogelImageList[0]}")

                Log.d("listviewdebut","$position")
                oldPosition = position

                pictureImage = requireView().findViewById<ImageView>(R.id.imageViewSehen)
                val opts = BitmapFactory.Options()
                opts.inDensity = DisplayMetrics.DENSITY_HIGH
                resourceId = Drawable.createFromStream(inputStream,null)
                pictureImage.setImageDrawable(resourceId)
                imageNext.visibility = View.VISIBLE
                imagePrevious.visibility = View.VISIBLE
                getStringId()

                if (view != null) {
                    view.isSelected = true
                }
            }
        }
        )

        //buttons
        nextBird.setOnClickListener {
            nextPosition = globalPosition+1
             posFirst = listView.firstVisiblePosition
             posLast = listView.lastVisiblePosition
             posList = nextPosition - posFirst
            Log.d("previoousDebug","1posList$posList")
            if (posList >= 15){
                listView.setSelection(nextPosition-(14-posList%15))
                 posFirst = listView.firstVisiblePosition
                 posLast = listView.lastVisiblePosition
                posList = 14
                Log.d("previoousDebug","2posList${nextPosition-(14-posList%15)}")
            }
            maxBirdListView=vogelSearchAdapter.count
            if (nextPosition > maxBirdListView ){Log.d("HighlightDebug","no more birds")}
            else {
                Log.d("previoousDebug","3posList$posList")
                listView[posList-1].isSelected = false
                listView[posList].isSelected = true
                assetManager = resources.assets
                vogelSelected = vogelSearchAdapter.getItem(nextPosition)
                vogelPrint = vogelSelected.word
                vogelImage = vogelSelected.word
                vogelImage = vogelImage.replace(" ","")
                vogelImageList = assetManager.list("Images/${vogelImage}") as Array<String>
                vogelImageListSize = vogelImageList.size
                vogelImageMaxSize = vogelImageListSize - 1
                imageNumber = 0
                Log.d("debuuuug","vogelImage$vogelImage")
                inputStream = assetManager.open("Images/${vogelImage}/${vogelImageList[0]}")
                resourceId = Drawable.createFromStream(inputStream, null)
                pictureImage.setImageDrawable(resourceId)
                vogelSearchAdapter.notifyDataSetChanged()
                getStringId()
                globalPosition = nextPosition
            }
        }
        previousBird.setOnClickListener {
            previusPosition = globalPosition-1
            Log.d("previoousDebug","previusbird$previusPosition")
            posFirst = listView.firstVisiblePosition
            posLast = listView.lastVisiblePosition
            posList = previusPosition- posFirst
            Log.d("previoousDebug","posList$posList")
            if (posList <= 0){
                listView.setSelection(previusPosition)
                posFirst = listView.firstVisiblePosition
                posLast = listView.lastVisiblePosition
                posList = previusPosition- posFirst
            }
            if ( previusPosition < 0){Log.d("HighlightDebug","no more birds")}
            else {
                if (posList < 0){posList = -posList}
                listView[posList+1].isSelected = false
                listView[posList].isSelected = true
                assetManager = resources.assets
                vogelSelected = vogelSearchAdapter.getItem(previusPosition)
                vogelPrint = vogelSelected.word
                vogelImage = vogelSelected.word
                vogelImage = vogelImage.replace(" ","")
                vogelImageList = assetManager.list("Images/${vogelImage}") as Array<String>
                vogelImageListSize = vogelImageList.size
                vogelImageMaxSize = vogelImageListSize - 1
                imageNumber = 0
                inputStream = assetManager.open("Images/${vogelImage}/${vogelImageList[0]}")
                resourceId = Drawable.createFromStream(inputStream, null)
                pictureImage.setImageDrawable(resourceId)
                vogelSearchAdapter.notifyDataSetChanged()
                getStringId()
                globalPosition = previusPosition
            }
        }

        imageNext.setOnClickListener{
            if(vogelImageListSize > 1 && imageNumber<vogelImageMaxSize){
                pictureImage.setImageDrawable(null)
                imageNumber += 1
                inputStream = assetManager.open("Images/${vogelImage}/${vogelImageList[imageNumber]}")
                resourceId =Drawable.createFromStream(inputStream,null)
                Log.d("imageviewDebug","nextpicture")
                pictureImage.setImageDrawable(resourceId)
                getStringId()
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
                getStringId()
            }

        }
    }

    private fun getStringId() {
        val idName = vogelImageList[imageNumber].replace(".jpg","").replace("-","")
        birdName = sehenView.findViewById<TextView>(R.id.birdName)
        birdName.text = vogelImage
        stringId = resources.getIdentifier(idName,"string",sehenView.context.packageName)
        if (stringId==0){Log.d("textViewDebug",idName)
        birdInformation.text = ""}
        else{        birdInformation.text = resources.getString(stringId)
            birdInformation.bringToFront()}

    }


    //functions


    fun getNamesFromDb(searchText:String, searchGruppe:String,birdLifeQuery:String){
        Log.d("addViewdebug","getNamesFromDb started")
        val searchBirdLife = "%$birdLifeQuery%"
        val searchTextQuery = "%$searchText%"
        val searchGruppeQuery = "%$searchGruppe%"

        birdDatabase!!.birdDao().readAllData(searchTextQuery, searchGruppeQuery,searchBirdLife)
            .observe(viewLifecycleOwner,object : Observer<List<Vogel>> {
                override fun onChanged(t: List<Vogel>?) {
                    if(t==null){
                        return
                    }
                    globalVogelList = t
                    vogelSearchAdapter =  SearchAdapter(sehenView.context,R.layout.listview_layout,t)
                    val adapter =  SearchAdapter(sehenView.context,R.layout.listview_layout,t)
                    listView.adapter = adapter
                    var countView = adapter.count
                    Log.d("addViewdebug","this is $countView")
                    maxBirdListView =listView.adapter.count

                }
            })
    }
}


