package com.katiekilroy.myapplication.BLEBeacon


class Beacon(mac: String?) {
    enum class beaconType {
        iBeacon, any
    }

    val macAddress = mac
    var manufacturer: String? = null
    var type: beaconType = beaconType.any
    var uuid: String? = null
    var major: Int? = null
    var minor: Int? = null
    var rssi: Int? = null
    var distance: Double? = null
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Beacon) return false

        if (macAddress != other.macAddress) return false

        return true
    }

    override fun hashCode(): Int {
        return macAddress?.hashCode() ?: 0
    }

}