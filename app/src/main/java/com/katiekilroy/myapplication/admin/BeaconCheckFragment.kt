package com.katiekilroy.myapplication.admin

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.katiekilroy.myapplication.BLEBeacon.Beacon
import com.katiekilroy.myapplication.Utils
import com.katiekilroy.myapplication.databinding.FragmentBeaconCheckBinding
import com.katiekilroy.myapplication.shortestpath.KalmanFilter
import java.util.ArrayList
import java.util.HashMap


class BeaconCheckFragment : Fragment() {

    private var _binding: FragmentBeaconCheckBinding? = null

    private var btManager: BluetoothManager? = null
    private var btAdapter: BluetoothAdapter? = null
    private var btScanner: BluetoothLeScanner? = null

    var rssi_current = 0
    var beacon_id = 0
    var path: List<Int> = ArrayList()
    private val kalmanFilters = HashMap<String, KalmanFilter>()

    var less_than_a_metre_rssi = -58
    var one_metre_rssi = -75
    var two_metre_rssi = -76
    var three_metre_rssi = -88

    var checkdist = false
    val mDatabase =
        Firebase.database("https://mai-ble-beacon-scanner-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("Map")

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBeaconCheckBinding.inflate(inflater, container, false)

        setUpBluetoothManager()
        binding.button.setOnClickListener {

            val ID = binding?.beaconID?.text.toString()
            Log.i("ID", ID)
            if (ID.isEmpty()) {
                Toast.makeText(
                    context,
                    "Add the ID of the beacon you want to check",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                beacon_id = ID.toInt()
                btScanner!!.startScan(leScanCallback)
            }
        }

        return binding.root
    }

    companion object {
        private const val REQUEST_ENABLE_BT = 1
        private const val PERMISSION_REQUEST_COARSE_LOCATION = 1
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


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
                        if ((beacon.major == 6544) && (beacon.minor == beacon_id)) {
                            val address = result.device.address
                            var kalmanFilter: KalmanFilter? = kalmanFilters[address]
                            if (kalmanFilter == null) {
                                kalmanFilter = KalmanFilter(
                                    0.01,
                                    0.1
                                ) // set process and measurement noise variance
                                kalmanFilters[address] = kalmanFilter
                            }
                            kalmanFilter.update(rssi_current.toDouble()) // update the Kalman filter with the latest RSSI measurement
                            beacon.rssi = kalmanFilter.x.toInt()

                            calculateDistance(beacon.rssi!!, beacon_id!!)
                            Log.i(
                                "KK",
                                "major:$major minor:$minor Rssi:${beacon.rssi}"
                            )
                        }
                    }
                }

            } catch (e: NullPointerException) {
                Log.e(ContentValues.TAG, "NullPointerException: ${e.message}")
            }

        }


        override fun onScanFailed(errorCode: Int) {
            Log.e("Katie", errorCode.toString())
        }
    }


    private fun isNetworkConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }

    fun calculateDistance(rssi_val: Int, id: Int) {
        Log.i("KK", "Calculating distance")
        binding?.Beacon1Img?.visibility = View.VISIBLE
        var dist = 0.0
        binding?.Beaconrssi?.setText("Beacon: $id, RSSI: $rssi_val")
        if (rssi_val!! >= less_than_a_metre_rssi) {
            dist = 0.5
            binding?.Beacon1?.setText("Beacon $id is less than 1m away")

        } else if (rssi_val!! >= one_metre_rssi) {
            binding?.Beacon1?.setText("Beacon $id is 1m away")

            dist = 1.0
        } else if ((rssi_val!! <= two_metre_rssi) && (rssi_val!! >= three_metre_rssi)
        ) {

            Log.i("kk", "$id is 2 meters away")
            binding?.Beacon1?.setText("Beacon $id is 2 meters away")

            dist = 2.0

        } else if (rssi_val!! <= -three_metre_rssi) {

            binding?.Beacon1?.setText("Beacon $id is 3+ meters away")
            checkdist = false
            dist = 3.0


        }


        Log.i("RSSI", "$rssi_val")
        Log.i("distance", "$dist")
    }

    @SuppressLint("MissingPermission")
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        kalmanFilters.clear()
        btScanner?.stopScan(leScanCallback)
    }

}