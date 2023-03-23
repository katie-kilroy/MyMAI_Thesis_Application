package com.katiekilroy.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.katiekilroy.myapplication.BLEBeacon.Beacon
import com.katiekilroy.myapplication.BLEBeacon.BeaconsAdapter
import com.katiekilroy.myapplication.databinding.FragmentScanningBinding
import com.katiekilroy.myapplication.placeholder.BeaconContent.g
import java.util.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class ScanningFragment : Fragment() {

    private lateinit var startButton: Button

    private lateinit var recyclerView: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager

    private var _binding: FragmentScanningBinding? = null

    private var btManager: BluetoothManager? = null
    private var btAdapter: BluetoothAdapter? = null
    private var btScanner: BluetoothLeScanner? = null
    var beaconSet: HashSet<Beacon> = HashSet()
    var beaconAdapter: BeaconsAdapter? = null

    var beaconTypePositionSelected = 0
    var rssi_current = 0
    var redirect = 0
    var start = 0
    var end = 0
    var path: List<Int> = ArrayList()


    val mDatabase =
        Firebase.database("https://mai-ble-beacon-scanner-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("Map")

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //
    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScanningBinding.inflate(inflater, container, false)

        start = arguments?.getInt("fromVertex")!!
        end = arguments?.getInt("toVertex")!!
        Log.i("start", start.toString())
        Log.i("end", end.toString())

        loadVertices()

        setUpBluetoothManager()
        beaconAdapter = BeaconsAdapter(beaconSet.toList())

        recyclerView = binding.recyclerView
        linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager
        beaconAdapter = BeaconsAdapter(beaconSet.toList())
        recyclerView.adapter = beaconAdapter
        beaconAdapter!!.filter.filter(Utils.ALL)



        btScanner!!.startScan(leScanCallback)

        return binding.root

//        return view
    }

    companion object {
        private const val REQUEST_ENABLE_BT = 1
        private const val PERMISSION_REQUEST_COARSE_LOCATION = 1
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    private fun loadVertices() {

        Log.i("Database", "Database reading starting")
        mDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

//                checking if the map is empty,
                val checklist = g.checkVertices()
                val locationNameList = mutableListOf<String>()
                locationNameList.add("Please Select a Location")

//                loop through vertices
                for (locationSnapshot in dataSnapshot.children) {

                    var num = locationSnapshot.key
                    val edgesSnapshot = locationSnapshot.child("Edges")
                    val locationName = locationSnapshot.child("Name").getValue(String::class.java)

//                    getting ID of vertex
                    if (locationName != null) {
//                        if it is empty it will return false are going to fill the current vertex's edges
                        if (!checklist) {

                            Log.i(
                                "Graph", "Adding Vertices to List"
                            )

//                            iterating through edges of the current vertex
                            if (edgesSnapshot.exists()) {


                                for (edgeSnapshot in edgesSnapshot.children) {

                                    // getting the edges destination ID and properties from the data base and adding them to the linked list of the app
                                    var id2 = edgeSnapshot.key
                                    val direction = edgeSnapshot.child("direction").value as Long
                                    val weight = edgeSnapshot.child("weight").value as Long
                                    val description =
                                        edgeSnapshot.child("description").value as String
                                    val name =
                                        mDatabase.child(id2.toString()).child("Name").toString()
                                    if ((num != null) && (id2 != null) && (num != id2)) {
                                        g.addEdgeByID(
                                            num.toInt(),
                                            id2.toInt(),
                                            locationName,
                                            name,
                                            weight.toInt(),
                                            direction.toInt(),
                                            description
                                        )

                                    }
                                }
                            }



                            Log.i("vertices size:", num.toString())
                        }
                    }
                }

                if (!g.checkVertices()) {

                    Log.e("Error:", "no vertex added to Vertices")
                } else {
                    path = g.shortestPath(start, end)

                    Toast.makeText(
                        requireContext(),
                        "Start: $start and End: $end",
                        Toast.LENGTH_SHORT
                    )
                        .show()


                    val current = path[0]
                    Log.i("Path", path[0].toString())
                    val next = path[1]
                    Log.i("Path", path[1].toString())
                    val pathData = FirebaseDatabase.getInstance()
                        .getReference("Map/$current/Edges")

                    val direction =
                        dataSnapshot.child(current.toString()).child("Edges").child(next.toString())
                            .child("direction").value as Long
                    val description =
                        dataSnapshot.child(current.toString()).child("Edges").child(next.toString())
                            .child("description").value
//                    setNavView(direction.toInt(), description as String)

                }
                g.printGraph()

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    @SuppressLint("NewApi")
    private fun setUpBluetoothManager() {
        btManager =
            activity?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        btAdapter = btManager!!.adapter
        btScanner = btAdapter?.bluetoothLeScanner
        if (btAdapter != null && !btAdapter!!.isEnabled) {
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT)
        }

        checkForLocationPermission()
    }

    @SuppressLint("NewApi")
    private fun checkForLocationPermission() {
        // Make sure we have access coarse location enabled, if not, prompt the user to enable it
        if (requireActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("This app needs location access")
            builder.setMessage("Please grant location access so this app can detect  peripherals.")
            builder.setPositiveButton(android.R.string.ok, null)
            builder.setOnDismissListener {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    PERMISSION_REQUEST_COARSE_LOCATION
                )
            }
            builder.show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_COARSE_LOCATION -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    println("coarse location permission granted")
                } else {
                    val builder = AlertDialog.Builder(activity)
                    builder.setTitle("Functionality limited")
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover BLE beacons")
                    builder.setPositiveButton(android.R.string.ok, null)
                    builder.setOnDismissListener { }
                    builder.show()
                }
                return
            }
        }
    }


    private val leScanCallback: ScanCallback = object : ScanCallback() {

        @SuppressLint("MissingPermission")

        override fun onScanResult(callbackType: Int, result: ScanResult) {
            try {
                val scanRecord = result.scanRecord
                val beacon = Beacon(result.device.address)
                beacon.manufacturer = result.device.name
                rssi_current = result.rssi
                beacon.rssi
                if (scanRecord != null) {

//                binding.textView2!!.text = " "
                    val serviceUuids = scanRecord.serviceUuids
                    val iBeaconManufactureData = scanRecord.getManufacturerSpecificData(0X004c)

                    if (iBeaconManufactureData != null && iBeaconManufactureData.size >= 23) {

                        val iBeaconUUID =
                            Utils.toHexString(iBeaconManufactureData.copyOfRange(2, 18))
                        val major = Integer.parseInt(
                            Utils.toHexString(
                                iBeaconManufactureData.copyOfRange(
                                    18,
                                    20
                                )
                            ), 16
                        )
                        val minor = Integer.parseInt(
                            Utils.toHexString(
                                iBeaconManufactureData.copyOfRange(
                                    20,
                                    22
                                )
                            ), 16
                        )
                        val distance = Utils.toHexString(iBeaconManufactureData)
                        beacon.type = Beacon.beaconType.iBeacon
                        beacon.uuid = iBeaconUUID
                        beacon.major = major
                        beacon.minor = minor
                        beacon.rssi = result.rssi
                        if (beacon.major == 6544) {
                            Log.i(
                                "KK",
                                "major:$major minor:$minor Rssi:${beacon.rssi}"
                            )
                            beaconSet.add(beacon)
                            (recyclerView.adapter as BeaconsAdapter).updateData(
                                beaconSet.toList(),
                                beaconTypePositionSelected
                            )
                            (recyclerView.adapter as BeaconsAdapter).updateData(
                                beaconSet.toList(),
                                beaconTypePositionSelected
                            )

                            beacon.distance = calculateDistance(beacon.rssi!!.toDouble())
                            val distance_val = beacon.distance.toString()

                            path = g.shortestPath(start, end)
                            if (path[1] != end) {

                                if ((beacon.minor == path.first()) && (beacon.rssi!! >= -60)) {
                                    path.drop(0)
                                    binding.textView2.setText(distance_val)

                                    val current = path[0]
                                    val next = path[1]

                                    val pathData = FirebaseDatabase.getInstance()
                                        .getReference("Map/$current/Edges")
                                    pathData.addListenerForSingleValueEvent(object :
                                        ValueEventListener {
                                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                                            val direction =
                                                dataSnapshot.child(next.toString())
                                                    .child("direction").value as Long
                                            val description =
                                                dataSnapshot.child(next.toString())
                                                    .child("description").value as String
                                            Log.i("Path", "direction: ${direction.toInt()}")
                                            Log.i("Path", "description: $description")
                                            Log.i("Path", "Path current: $current")
                                            setNavView(direction.toInt(), description as String)

                                        }

                                        override fun onCancelled(error: DatabaseError) {
//                                    TODO("Not yet implemented")
                                        }
                                    })

                                } else if ((beacon.minor != path.first()) && (beacon.rssi!! >= -63)) {

                                    redirect++
                                    path = g.shortestPath(beacon.minor!!, end)
                                    val current = path[0]
                                    val next = path[1]

                                    Log.i("Navigation", "Redirecting")
                                    Log.i("Navigation", "now navigating to $next")


                                    binding?.textView2?.setText(distance_val)
                                    val pathData = FirebaseDatabase.getInstance()
                                        .getReference("Map/$current/Edges")
                                    pathData.addListenerForSingleValueEvent(object :
                                        ValueEventListener {
                                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                                            val direction =
                                                dataSnapshot.child(next.toString())
                                                    .child("direction").value as Long
                                            val description =
                                                dataSnapshot.child(next.toString())
                                                    .child("description").value as String
                                            Log.i("Path", "direction: ${direction.toInt()}")
                                            Log.i("Path", "description: $description")
                                            Log.i("Path", "Path current: $current")
                                            setNavView(direction.toInt(), description as String)

                                        }

                                        override fun onCancelled(error: DatabaseError) {
//                                    TODO("Not yet implemented")
                                        }
                                    })

                                }


//                            Log.i(
//                                "KK",
//                                "minor:$minor Rssi:${beacon.rssi}"
//                            )


                                if ((beacon.minor == path[1]) && (beacon.rssi!! >= -60)) {
                                    binding?.imageView2?.setImageResource(R.drawable.default_nav)
                                    Log.i("Navigation", "Completed Navigation!!")

                                    binding.textView3.setText(R.string.congrats_destination)
                                    binding.textView2.setText("You got redirected $redirect times")
                                }
                            }
                        }
//                    Log.i(
//                        "KK",
//                        "major:$major minor:$minor Rssi:${beacon.rssi}"
//                    )


                    }
                }
            }catch (e: NullPointerException){
                Log.e(TAG, "NullPointerException: ${e.message}")
            }

        }


        override fun onScanFailed(errorCode: Int) {
            Log.e("Katie", errorCode.toString())
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setNavView(direction: Int, description: String) {
        var text = ""

        when (direction) {
            0 -> {
                binding?.imageView2?.setImageResource(R.drawable.default_nav)
                Log.i("Navigation", "Can't find a beacon nearby!")

                text = "Can't find a beacon nearby!"

            }
            1 -> {
                binding?.imageView2?.setImageResource(R.drawable.straight)
                Log.i("Navigation", "Set straight image")
                text = "Pass the $description"
            }
            -1 -> {
                binding?.imageView2?.setImageResource(R.drawable.u_turn)
                Log.i("Navigation", "Set u turn image")
                text = "Turn around and pass the $description"
            }
            2 -> {
                binding?.imageView2?.setImageResource(R.drawable.right)
                Log.i("Navigation", "Set right image")
                text = "Turn right at $description"
            }
            -2 -> {
                binding?.imageView2?.setImageResource(R.drawable.left)
                Log.i("Navigation", "Set left image")
                text = "Turn left at $description"
            }

            else -> {
                binding?.imageView2?.setImageResource(R.drawable.default_nav)
                Log.i("Navigation", "Set default image")
                text = ""
            }
        }
        binding.textView3.setText(text)

    }

    fun calculateDistance(y: Double): Double {
        return (-y - 15.4106) / 33.7996
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}