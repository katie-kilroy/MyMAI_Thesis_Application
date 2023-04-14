package com.katiekilroy.maitestapplication

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import io.reactivex.Flowable
import io.reactivex.processors.BehaviorProcessor
import javax.inject.Inject

class BluetoothManager @Inject constructor(private val adapter: BluetoothAdapter?, context: Context) {

    private val subject: BehaviorProcessor<MainActivity2.BluetoothState> =
        BehaviorProcessor.createDefault<MainActivity2.BluetoothState>(getStaeFromAdapterState(adapter?.state ?: BluetoothAdapter.STATE_OFF))

    init {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                if (BluetoothAdapter.ACTION_STATE_CHANGED == intent.action) {
                    val state = getStaeFromAdapterState(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR))

                    subject.onNext(state)
                }
            }
        }
        context.registerReceiver(receiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
    }

    fun getStaeFromAdapterState(state: Int) : MainActivity2.BluetoothState {
        return when (state) {
            BluetoothAdapter.STATE_OFF -> MainActivity2.BluetoothState.STATE_OFF
            BluetoothAdapter.STATE_TURNING_OFF -> MainActivity2.BluetoothState.STATE_TURNING_OFF
            BluetoothAdapter.STATE_ON -> MainActivity2.BluetoothState.STATE_ON
            BluetoothAdapter.STATE_TURNING_ON -> MainActivity2.BluetoothState.STATE_TURNING_ON
            else -> MainActivity2.BluetoothState.STATE_OFF
        }
    }

    @SuppressLint("MissingPermission")
    fun disable() = adapter?.disable()

    @SuppressLint("MissingPermission")
    fun enable() = adapter?.enable()

    fun asFlowable(): Flowable<MainActivity2.BluetoothState> {
        return subject
    }

    fun isEnabled() = adapter?.isEnabled == true

    fun toggle() {
        if (isEnabled()) {
            disable()
        } else {
            enable()
        }
    }
}
