package com.katiekilroy.myapplication.shortestpath;

class KalmanFilter (val Q: Double, val R: Double) {
        var x = 0.0 // estimate of the state
        var P = 1.0 // estimate of the state covariance
        var K = 0.0 // Kalman gain

        fun update(z: Double) {
            // predict
            x = x
            P = P + Q

            // update
            K = P / (P + R)
            x = x + K * (z - x)
            P = (1 - K) * P
        }
}

