package vn.trunglt.democamera1.managers

import java.lang.Exception

interface IScanQRManager {
    fun onSuccess(data: String?)
    fun onError(exception: Exception)
    fun onComplete()
}