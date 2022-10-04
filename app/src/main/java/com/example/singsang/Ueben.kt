package com.example.singsang

import android.content.res.AssetManager
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.roomapp.data.BirdDao
import com.example.roomapp.data.BirdDatabase
import com.example.singsang.Adapter.SearchAdapter
import com.example.singsang.Data.Vogel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.w3c.dom.Text
import java.io.InputStream
import kotlin.properties.Delegates


class Ueben : Fragment() {

    lateinit var assetManager: AssetManager
    lateinit var resourceId: Drawable
    lateinit var vogelImage: String
    lateinit var vogelImageList: Array<String>
    lateinit var inputStream: InputStream
    lateinit var birdName: String
    lateinit var pictureImage: ImageView
    lateinit var birdList: List<Vogel>
    var stringId by Delegates.notNull<Int>()

    var imageNumber = 0
    lateinit var uebenView: View
    private var birdDatabase: BirdDatabase? = null
    var familySelected: String = ""
    lateinit var  queryString:SupportSQLiteQuery



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        uebenView = inflater.inflate(R.layout.fragment_ueben, container, false)
        birdDatabase = BirdDatabase.getDatabase(uebenView.context)

        return uebenView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //spinner functions
        val familyNames = resources.getStringArray(R.array.FamilienNamen)
        val spinner = uebenView.findViewById<Spinner>(R.id.spinnerUeben)
        val imageView = uebenView.findViewById<ImageView>(R.id.imageUeben)
        val button1 = uebenView.findViewById<Button>(R.id.nextVogelUeben)
        val button2 = uebenView.findViewById<Button>(R.id.uebenReveal)
        val pictureInfoView = uebenView.findViewById<TextView>(R.id.textViewUeben)
        val checkBoxBirdLife = uebenView.findViewById<CheckBox>(R.id.checkBoxBirdLife)
        assetManager = resources.assets

        button1.setOnClickListener {
            pictureInfoView.text = resources.getString(R.string.werDa)
            var prequeryString = "SELECT * From Vogel_database "
            if(checkBoxBirdLife.isChecked){
                prequeryString += " Where"
            prequeryString += " (BirdLife LIKE 1) "
            prequeryString += " ORDER BY VogelName ASC "}

            queryString = SimpleSQLiteQuery(prequeryString)
            birdDatabase!!.birdDao().getFamilyNames(queryString)
                .observe(viewLifecycleOwner,object :Observer<List<Vogel>>{
                    override fun onChanged(t: List<Vogel>?){
                        if(t==null){
                            return
                        }
                        birdList = t
                        birdName = getName(birdList)
                        showPicture(birdName)
                    }
                })
        }

        button2.setOnClickListener {
            getInfo(birdName)
            pictureInfoView.text = birdName + "\n" + getInfo(birdName)
        }


        if (spinner != null) {
            val adapter = activity?.let {
                ArrayAdapter(
                    it,
                    android.R.layout.simple_spinner_item,
                    familyNames
                )
            }
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position == 0) {
                        familySelected = ""
                        Log.d("spinnerdebug", "$familySelected")
                    } else {
                        familySelected = familyNames[position]
                        Log.d("spinnerdebug", "$familySelected")
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        }
    }
//functions
    fun getInfo(name:String):String{
    val idName = vogelImageList[imageNumber].replace(".jpg","").replace("-","")
    var returnText = ""
    stringId = resources.getIdentifier(idName,"string",uebenView.context.packageName)
    if (stringId==0){Log.d("textViewDebug",idName)
        returnText = ""}
    else{     returnText    = resources.getString(stringId)
        }
    return returnText
    }

    fun showPicture(name:String){
    vogelImage = name.replace(" ","")
    vogelImageList = assetManager.list("Images/${vogelImage}") as Array<String>
    if (vogelImageList.size > 1){imageNumber = 1.rangeTo(vogelImageList.size).shuffled().first()-1}
    else{imageNumber = 0}
    inputStream = assetManager.open("Images/${vogelImage}/${vogelImageList[imageNumber]}")
    pictureImage = requireView().findViewById<ImageView>(R.id.imageUeben)
    val opts = BitmapFactory.Options()
    opts.inDensity = DisplayMetrics.DENSITY_HIGH
    resourceId = Drawable.createFromStream(inputStream,null)
    pictureImage.setImageDrawable(resourceId)
    }

    fun getName(list: List<Vogel>):String{
        val begin = 0
        val max = list.size - 1
        var randomValue = begin.rangeTo(max).shuffled().first()
        birdName = list[randomValue].word
        return birdName

    }

}






